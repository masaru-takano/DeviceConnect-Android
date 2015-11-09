/*
 ThetaFeatureActivity.java
 Copyright (c) 2015 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.theta.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import org.deviceconnect.android.deviceplugin.theta.ThetaDeviceApplication;
import org.deviceconnect.android.deviceplugin.theta.fragment.ThetaShootingModeFragment;
import org.deviceconnect.android.deviceplugin.theta.fragment.ThetaVRModeFragment;

/**
 * Activity for the transition from the gallery to the function screen of THETA.
 * 
 * @author NTT DOCOMO, INC.
 */
public class ThetaFeatureActivity extends FragmentActivity {

    /**
     * Feature Mode.
     */
    public static final String FEATURE_MODE = "org.deviceconnect.android.feature.MODE";

    /**
     * Mode VR.
     */
    public static final int MODE_VR = 0;

    /**
     * Mode Shooting.
     */
    public static final int MODE_SHOOTING = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mode = getIntent().getIntExtra(FEATURE_MODE, -1);
        if (null == savedInstanceState) {
            startApp(mode, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    /**
     * Move Page.
     * @param pageId pageId
     * @param serviceId TODO Theta Object?
     */
    public void startApp(final int pageId, final String serviceId) {
        if (pageId == MODE_SHOOTING) {
            ThetaShootingModeFragment f = new ThetaShootingModeFragment();
            moveFragment(f);
        } else if (pageId == MODE_VR) {
            ThetaVRModeFragment f = new ThetaVRModeFragment();
            moveFragment(f);
        }
    }

    /**
     * Fragment の遷移.
     * @param f Fragment
     */
    private void moveFragment(final Fragment f) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.setTransition(FragmentTransaction.TRANSIT_NONE);
        t.replace(android.R.id.content, f);
        t.addToBackStack(null);
        t.commit();

    }

    /**
     * Return ThetaDeviceApplication.
     * @return IRKitApplication
     */
    public ThetaDeviceApplication getIRKitApplication() {
        return (ThetaDeviceApplication) getApplication();
    }

}