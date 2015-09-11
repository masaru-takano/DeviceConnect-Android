package org.deviceconnect.android.deviceplugin.theta.walkthrough;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import org.deviceconnect.android.deviceplugin.theta.opengl.PixelBuffer;
import org.deviceconnect.android.deviceplugin.theta.opengl.SphereRenderer;
import org.deviceconnect.android.deviceplugin.theta.utils.Quaternion;
import org.deviceconnect.android.deviceplugin.theta.utils.Vector3D;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WalkthroughContext implements SensorEventListener {

    private static final String TAG = "Walk";

    private static final int NUM_PRELOAD = 5;
    private static final long EXPIRE_INTERVAL = 10 * 1000;
    private static final float NS2S = 1.0f / 1000000000.0f;

    private Logger mLogger = Logger.getLogger("theta.dplugin");

    private final SensorManager mSensorMgr;
    private long mLastEventTimestamp;
    private float mEventInterval;
    private final int mDisplayRotation;
    private Quaternion mCurrentRotation = new Quaternion(1, new Vector3D(0, 0, 0));

    private final File[] mAllFiles;
    private final BitmapLoader mBitmapLoader;
    private byte[] mRoi;
    private String mUri;
    private String mSegment;

    private final long mInterval; // milliseconds
    private Timer mExpireTimer;
    private EventListener mListener;

    private final ExecutorService mExecutor = Executors.newFixedThreadPool(1);
    private PixelBuffer mPixelBuffer;
    private final SphereRenderer mRenderer = new SphereRenderer();
    private ByteArrayOutputStream mBaos;
    private boolean mIsStopped = true;

    public WalkthroughContext(final Context context, final File omniImageDir,
                              final int width, final int height, final float fps) {
        WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplayRotation = windowMgr.getDefaultDisplay().getRotation();
        mSensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        mAllFiles = loadFiles(omniImageDir);
        mBitmapLoader = new BitmapLoader(mAllFiles);
        mBitmapLoader.setLoaderListener(new BitmapLoaderListener() {

            @Override
            public void onLoad(int pos) {
                Log.d(TAG, "onLoad: " + pos);
                if (mIsStopped) {
                    return;
                }
                if (pos == NUM_PRELOAD - 1) {
                    startVideo();
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
                //stopVideo();
                if (mListener != null) {
                    mListener.onComplete(WalkthroughContext.this);
                }
            }

            @Override
            public void onError(int pos, Exception e) {
                e.printStackTrace();
                Log.e("Walk", "Error: " + e.getMessage());
            }
        });

        mInterval = (long) (1000.0f / fps);

        mBaos = new ByteArrayOutputStream(width * height);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPixelBuffer = new PixelBuffer(width, height, false);
                mPixelBuffer.setRenderer(mRenderer);
            }
        });

        Param param = new Param();
        param.setImageWidth(width);
        param.setImageHeight(height);
        initRendererParam(param);
    }

    private File[] loadFiles(final File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("dir is not directory.");
        }
        Log.d(TAG, "Loading Omni images directory: " + dir.getAbsolutePath());
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String filename) {
                return filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg");
            }
        });
        Log.d(TAG, "Files: " + files.length);
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileList.add(files[i]);
        }
        Collections.sort(fileList);
        return fileList.toArray(new File[fileList.size()]);
    }

    private boolean startVrMode() {
        // Reset current rotation.
        mCurrentRotation = new Quaternion(1, new Vector3D(0, 0, 0));

        List<Sensor> sensors = mSensorMgr.getSensorList(Sensor.TYPE_ALL);
        if (sensors.size() == 0) {
            mLogger.warning("Failed to start VR mode: any sensor is NOT found.");
            return false;
        }
        for (Sensor sensor : sensors) {
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                mLogger.info("Started VR mode: GYROSCOPE sensor is found.");
                mSensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                return true;
            }
        }
        mLogger.warning("Failed to start VR mode: GYROSCOPE sensor is NOT found.");
        return false;
    }

    private void stopVrMode() {
        mSensorMgr.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // Nothing to do.
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (mLastEventTimestamp != 0) {
            float EPSILON = 0.000000001f;
            float[] vGyroscope = new float[3];
            float[] deltaVGyroscope = new float[4];
            Quaternion qGyroscopeDelta;
            float dT = (event.timestamp - mLastEventTimestamp) * NS2S;

            System.arraycopy(event.values, 0, vGyroscope, 0, vGyroscope.length);
            float tmp = vGyroscope[2];
            vGyroscope[2] = vGyroscope[0] * -1;
            vGyroscope[0] = tmp;

            float magnitude = (float) Math.sqrt(Math.pow(vGyroscope[0], 2)
                    + Math.pow(vGyroscope[1], 2) + Math.pow(vGyroscope[2], 2));
            if (magnitude > EPSILON) {
                vGyroscope[0] /= magnitude;
                vGyroscope[1] /= magnitude;
                vGyroscope[2] /= magnitude;
            }

            float thetaOverTwo = magnitude * dT / 2.0f;
            float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);

            deltaVGyroscope[0] = sinThetaOverTwo * vGyroscope[0];
            deltaVGyroscope[1] = sinThetaOverTwo * vGyroscope[1];
            deltaVGyroscope[2] = sinThetaOverTwo * vGyroscope[2];
            deltaVGyroscope[3] = cosThetaOverTwo;

            float[] delta = new float[3];
            switch (mDisplayRotation) {
                case Surface.ROTATION_0:
                    delta[0] = deltaVGyroscope[0];
                    delta[1] = deltaVGyroscope[1];
                    delta[2] = deltaVGyroscope[2];
                    break;
                case Surface.ROTATION_90:
                    delta[0] = deltaVGyroscope[0];
                    delta[1] = deltaVGyroscope[2] * -1;
                    delta[2] = deltaVGyroscope[1];
                    break;
                case Surface.ROTATION_180:
                    delta[0] = deltaVGyroscope[0];
                    delta[1] = deltaVGyroscope[1] * -1;
                    delta[2] = deltaVGyroscope[2];
                    break;
                case Surface.ROTATION_270:
                    delta[0] = deltaVGyroscope[0];
                    delta[1] = deltaVGyroscope[2];
                    delta[2] = deltaVGyroscope[1] * -1;
                    break;
                default:
                    break;
            }

            qGyroscopeDelta = new Quaternion(deltaVGyroscope[3], new Vector3D(delta));

            mCurrentRotation = qGyroscopeDelta.multiply(mCurrentRotation);

            float[] qvOrientation = new float[4];
            qvOrientation[0] = mCurrentRotation.imaginary().x();
            qvOrientation[1] = mCurrentRotation.imaginary().y();
            qvOrientation[2] = mCurrentRotation.imaginary().z();
            qvOrientation[3] = mCurrentRotation.real();

            float[] rmGyroscope = new float[9];
            SensorManager.getRotationMatrixFromVector(rmGyroscope,
                    qvOrientation);

            float[] vOrientation = new float[3];
            SensorManager.getOrientation(rmGyroscope, vOrientation);

            SphereRenderer.Camera currentCamera = mRenderer.getCamera();
            SphereRenderer.CameraBuilder newCamera = new SphereRenderer.CameraBuilder(currentCamera);
            newCamera.rotate(mCurrentRotation);
            mRenderer.setCamera(newCamera.create());
        }
        mLastEventTimestamp = event.timestamp;
    }

    public void setUri(final String uriString) {
        mUri = uriString;
        mSegment = Uri.parse(uriString).getLastPathSegment();
    }

    public String getUri() {
        return mUri;
    }

    public String getSegment() {
        return mSegment;
    }

    public synchronized void start() {
        Log.d(TAG, "Walkthrough.start()");
        if (mIsStopped) {
            mIsStopped = false;
            startVrMode();
            mBitmapLoader.init(NUM_PRELOAD);
        }
    }

    public synchronized void stop() {
        Log.d(TAG, "Walkthrough.stop()");
        if (!mIsStopped) {
            mIsStopped = true;
            stopVrMode();
            mBitmapLoader.reset();
            mPixelBuffer.destroy();
            mExecutor.shutdownNow();
        }
    }

    private Thread mRendererThread;

    private synchronized void startVideo() {
        Log.d(TAG, "Walkthrough.startVideo()");
        if (mRendererThread != null) {
            return;
        }

        mRendererThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long start, end, interval;
                    while (!mIsStopped) {
                        start = System.currentTimeMillis();
                        render();
                        end = System.currentTimeMillis();

                        interval = mInterval - (end - start);
                        if (interval > 0) {
                            Thread.sleep(interval);
                        }
                    }
                } catch (InterruptedException e) {
                    // Nothing to do.
                }

                Log.d(TAG, "Stopped video.");
            }
        });
        mRendererThread.start();
    }

    private void render() {
        Log.d(TAG, "Walkthrough.render()");
        if (mIsStopped) {
            return;
        }
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Bitmap texture;
                    try {
                        texture = mBitmapLoader.pull();
                        if (texture == null) {
                            Log.d("Walk", "no longer bitmap.");
                            return;
                        }
                    } catch (InterruptedException e) {
                        Log.d("Walk", "thread is interrupted.");
                        return;
                    }
                    Log.i(TAG, "Changing Texure: size=" + texture.getWidth() + " x " + texture.getHeight());

                    mRenderer.setTexture(texture);
                    mPixelBuffer.render();
                    Bitmap result = mPixelBuffer.convertToBitmap();
                    if (result == null) {
                        return;
                    }

                    mBaos.reset();
                    result.compress(Bitmap.CompressFormat.JPEG, 100, mBaos);
                    mRoi = mBaos.toByteArray();

                    if (mListener != null) {
                        mListener.onUpdate(WalkthroughContext.this, mRoi);
                    }
                } catch (Throwable e) {
                   Log.d(TAG, "ERROR: Executor:", e);
                    e.printStackTrace();
                }

            }
        });
    }

    private void initRendererParam(final Param param) {
        SphereRenderer.CameraBuilder builder = new SphereRenderer.CameraBuilder();
        builder.setPosition(new Vector3D(
            (float) param.getCameraX(),
            (float) param.getCameraY() * -1,
            (float) param.getCameraZ()));
        builder.setFov((float) param.getCameraFov());
        mRenderer.setCamera(builder.create());
        mRenderer.setSphereRadius((float) param.getSphereSize());
        mRenderer.setScreenWidth(param.getImageWidth());
        mRenderer.setScreenHeight(param.getImageHeight());
    }

    public void startExpireTimer() {
        if (mExpireTimer != null) {
            return;
        }
        long now = System.currentTimeMillis();
        Date expireTime = new Date(now + EXPIRE_INTERVAL);
        mExpireTimer = new Timer();
        mExpireTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mExpireTimer.cancel();
                mExpireTimer = null;
                if (mListener != null) {
                    mListener.onExpire(WalkthroughContext.this);
                }
            }
        }, expireTime);
    }

    public void stopExpireTimer() {
        if (mExpireTimer != null) {
            mExpireTimer.cancel();
            mExpireTimer = null;
        }
    }

    public void restartExpireTimer() {
        stopExpireTimer();
        startExpireTimer();
    }

    public void setEventListener(final EventListener listener) {
        mListener = listener;
    }

    public static interface EventListener {
        void onUpdate(WalkthroughContext context, byte[] roi);

        void onComplete(WalkthroughContext context);

        void onExpire(WalkthroughContext roiContext);
    }

    private static class BitmapLoader {

        private final File[] mFiles;

        private final Bitmap[] mBitmaps;

        private final ExecutorService mExecutor = Executors.newFixedThreadPool(1);

        private BitmapLoaderListener mListener;

        private int mPos;

        public BitmapLoader(final File[] files) {
            for (File file : files) {
                if (file == null) {
                    throw new IllegalArgumentException("files must not have a null object.");
                }
            }
            mFiles = files;
            mBitmaps = new Bitmap[files.length];
        }

        public void setLoaderListener(final BitmapLoaderListener listener) {
            mListener = listener;
        }

        public void init(final int num) {
            Log.d(TAG, "Preloading bitmaps: num=" + num);
            for (int i = 0; i < num; i++) {
                loadBitmap(i);
            }
        }

        public synchronized void reset() {
            Log.d(TAG, "Walkthrough.reset()");
            mPos = 0;
            for (int i = 0; i < mBitmaps.length; i++) {
                Bitmap bitmap = mBitmaps[i];
                if (bitmap != null) {
                    bitmap.recycle();
                    mBitmaps[i] = null;
                }
            }
        }

        public synchronized Bitmap pull() throws InterruptedException {
            if (mPos == mBitmaps.length) {
                Log.w(TAG, "Walkthrough.pull(): out of range");
                return null;
            }

            int pos = mPos++;
            Log.d(TAG, "Walkthrough.pull(): changed pos: " + pos);
            if (pos == mBitmaps.length - 1) {
                if (mListener != null) {
                    mListener.onComplete();
                }
            }

            File file = mFiles[pos];
            synchronized (file) {
                Bitmap bitmap = mBitmaps[pos];
                if (bitmap == null) {
                    Log.d(TAG, "Now loading...: pos=" + pos);
                    loadBitmap(pos);
                    while ((bitmap = mBitmaps[pos]) == null) {
                        file.wait(100);
                    }
                    Log.d(TAG, "Loaded: pos=" + pos);
                } else {
                    Log.d(TAG, "Already loaded: pos=" + pos);
                }

                // Remove pulled bitmap from this buffer.
                mBitmaps[pos] = null;

                loadBitmap(pos + 1);
                return bitmap;
            }
        }

        private void loadBitmap(final int pos) {
            if (0 > pos || pos >= mBitmaps.length) {
                return;
            }

            Log.d(TAG, "Loading bitmap: pos=" + pos);

            final File file = mFiles[pos];

            if (mBitmaps[pos] != null) {
                Log.d(TAG, "Already loaded bitmap: pos=" + pos);
                synchronized (file) {
                    file.notifyAll();
                }
                return;
            }

            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        synchronized (file) {
                            mBitmaps[pos] = bitmap;
                            file.notifyAll();
                        }

                        if (mListener != null) {
                            mListener.onLoad(pos);
                        }
                    } catch (IOException e) {
                        if (mListener != null) {
                            mListener.onError(pos, e);
                        }
                    } catch (Throwable e) {
                        Log.d(TAG, "ERROR: loadBitmap: ", e);
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch(IOException e) {
                                // Nothing to do.
                            }
                        }
                    }
                }
            });
        }
    }

    private static interface BitmapLoaderListener {

        void onLoad(int pos);

        void onComplete();

        void onError(int pos, Exception e);

    }

    public static class Param {

        double mCameraX;

        double mCameraY;

        double mCameraZ;

        double mCameraYaw;

        double mCameraRoll;

        double mCameraPitch;

        double mCameraFov = 90.0d;

        double mSphereSize = 1.0d;

        int mImageWidth = 480;

        int mImageHeight = 270;

        boolean mStereoMode;

        boolean mVrMode;

        public double getCameraX() {
            return mCameraX;
        }

        public void setCameraX(final double x) {
            mCameraX = x;
        }

        public double getCameraY() {
            return mCameraY;
        }

        public void setCameraY(final double y) {
            mCameraY = y;
        }

        public double getCameraZ() {
            return mCameraZ;
        }

        public void setCameraZ(final double z) {
            mCameraZ = z;
        }

        public double getCameraYaw() {
            return mCameraYaw;
        }

        public void setCameraYaw(final double yaw) {
            mCameraYaw = yaw;
        }

        public double getCameraRoll() {
            return mCameraRoll;
        }

        public void setCameraRoll(final double roll) {
            mCameraRoll = roll;
        }

        public double getCameraPitch() {
            return mCameraPitch;
        }

        public void setCameraPitch(final double pitch) {
            mCameraPitch = pitch;
        }

        public double getCameraFov() {
            return mCameraFov;
        }

        public void setCameraFov(final double fov) {
            mCameraFov = fov;
        }

        public double getSphereSize() {
            return mSphereSize;
        }

        public void setSphereSize(final double size) {
            mSphereSize = size;
        }

        public int getImageWidth() {
            return mImageWidth;
        }

        public void setImageWidth(final int width) {
            mImageWidth = width;
        }

        public int getImageHeight() {
            return mImageHeight;
        }

        public void setImageHeight(final int height) {
            mImageHeight = height;
        }

        public boolean isStereoMode() {
            return mStereoMode;
        }

        public void setStereoMode(final boolean isStereo) {
            mStereoMode = isStereo;
        }

        public boolean isVrMode() {
            return mVrMode;
        }

        public void setVrMode(final boolean isVr) {
            mVrMode = isVr;
        }
    }
}
