/*
 HumanDetectProfile.java
 Copyright (c) 2015 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import java.util.List;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.profile.HumanDetectProfileConstants;

import android.content.Intent;
import android.os.Bundle;

//TODO: コメント修正する
/**
 * Human Detect Profile.
 * 
 * <p>
 * API that provides Setting, the Detection feature for Human Detect Device.<br/>
 * 
 * DevicePlugin that provides a HumanDetect operation function of for smart device inherits an equivalent class, and implements the corresponding API thing. <br/>
 * </p>
 * 
 * <h1>API provides methods</h1>
 * <p>
 * For requests to each API of HumanDetectProfile, following callback method group is automatically invoked.<br/>
 * Subclasses override the methods for API provided by the DevicePlugin from the following methods group, to implement the functionality that.<br/>
 * Features that are not overridden automatically return the response as non-compliant API.
 * </p>
 * <ul>
 * <li>Human Detect API [POST] :
 * {@link HumanDetectProfile#detection(Intent, Intent, String, Byte[], double, double, String)}</li>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public abstract class HumanDetectProfile extends DConnectProfile implements HumanDetectProfileConstants {
    
    
    /**
     * Constructor.
     */
    public HumanDetectProfile() {
    }

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }
    
    @Override
    protected boolean onGetRequest(Intent request, Intent response) {
        String interface_ = getInterface(request);
        String attribute = getAttribute(request);
        boolean result = true;
        if (INTERFACE_DETECTION.equals(interface_) && ATTRIBUTE_BODY_DETECTION.equals(attribute)) {
            String serviceId = getServiceID(request);
            List<String> options = getOptions(request);
            result = onGetBodyDetection(request, response, serviceId, options);
        } else if (INTERFACE_DETECTION.equals(interface_) && ATTRIBUTE_HAND_DETECTION.equals(attribute)) {
            String serviceId = getServiceID(request);
            List<String> options = getOptions(request);
            result = onGetHandDetection(request, response, serviceId, options);
        } else if (INTERFACE_DETECTION.equals(interface_) && ATTRIBUTE_FACE_DETECTION.equals(attribute)) {
            String serviceId = getServiceID(request);
            List<String> options = getOptions(request);
            result = onGetFaceDetection(request, response, serviceId, options);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        String interface_ = getInterface(request);
        String attribute = getAttribute(request);
        boolean result = true;
        if (INTERFACE_DETECTION.equals(interface_) && ATTRIBUTE_BODY_DETECTION.equals(attribute)) {
            String serviceId = getServiceID(request);
            List<String> options = getOptions(request);
            result = onPostBodyDetection(request, response, serviceId, options);
        } else if (INTERFACE_DETECTION.equals(interface_) && ATTRIBUTE_HAND_DETECTION.equals(attribute)) {
            String serviceId = getServiceID(request);
            List<String> options = getOptions(request);
            result = onPostHandDetection(request, response, serviceId, options);
        } else if (INTERFACE_DETECTION.equals(interface_) && ATTRIBUTE_FACE_DETECTION.equals(attribute)) {
            String serviceId = getServiceID(request);
            List<String> options = getOptions(request);
            result = onPostFaceDetection(request, response, serviceId, options);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }
    /**
     * body detection attribute request handler.<br/>
     * And ask the human body detection, and the result is stored in the response parameters.
     * If the response parameter is ready, please return true.
     * If you are not ready, please return false to start the process in the thread.
     * Once the thread is complete, send the response parameters.
     * @param request request parameter
     * @param response response parameter.
     * @param serviceId serviceID
     * @param options options.
     * @return send response flag.(true:sent / unsent (Send after the thread has been completed))
     */
    protected boolean onGetBodyDetection(final Intent request, final Intent response, final String serviceId, final List<String> options) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * hand detection attribute request handler.<br/>
     * And ask the human hand detection, and the result is stored in the response parameters.
     * If the response parameter is ready, please return true.
     * If you are not ready, please return false to start the process in the thread.
     * Once the thread is complete, send the response parameters.
     * @param request request parameter
     * @param response response parameter.
     * @param serviceId serviceID
     * @param options options.
     * @return send response flag.(true:sent / unsent (Send after the thread has been completed))
     */
    protected boolean onGetHandDetection(final Intent request, final Intent response, final String serviceId, final List<String> options) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * face detection attribute request handler.<br/>
     * And ask the human face detection, and the result is stored in the response parameters.
     * If the response parameter is ready, please return true.
     * If you are not ready, please return false to start the process in the thread.
     * Once the thread is complete, send the response parameters.
     * @param request request parameter
     * @param response response parameter.
     * @param serviceId serviceID
     * @param options options.
     * @return send response flag.(true:sent / unsent (Send after the thread has been completed))
     */
    protected boolean onGetFaceDetection(final Intent request, final Intent response, final String serviceId, final List<String> options) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * body detection attribute request handler.<br/>
     * And ask the human body detection, and the result is stored in the response parameters.
     * If the response parameter is ready, please return true.
     * If you are not ready, please return false to start the process in the thread.
     * Once the thread is complete, send the response parameters.
     * @param request request parameter
     * @param response response parameter.
     * @param serviceId serviceID
     * @param options options.
     * @return send response flag.(true:sent / unsent (Send after the thread has been completed))
     */
    protected boolean onPostBodyDetection(final Intent request, final Intent response, final String serviceId, final List<String> options) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * hand detection attribute request handler.<br/>
     * And ask the human hand detection, and the result is stored in the response parameters.
     * If the response parameter is ready, please return true.
     * If you are not ready, please return false to start the process in the thread.
     * Once the thread is complete, send the response parameters.
     * @param request request parameter
     * @param response response parameter.
     * @param serviceId serviceID
     * @param options options.
     * @return send response flag.(true:sent / unsent (Send after the thread has been completed))
     */
    protected boolean onPostHandDetection(final Intent request, final Intent response, final String serviceId, final List<String> options) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * face detection attribute request handler.<br/>
     * And ask the human face detection, and the result is stored in the response parameters.
     * If the response parameter is ready, please return true.
     * If you are not ready, please return false to start the process in the thread.
     * Once the thread is complete, send the response parameters.
     * @param request request parameter
     * @param response response parameter.
     * @param serviceId serviceID
     * @param options options.
     * @return send response flag.(true:sent / unsent (Send after the thread has been completed))
     */
    protected boolean onPostFaceDetection(final Intent request, final Intent response, final String serviceId, final List<String> options) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // Getter methods.
    // ------------------------------------
    
    /**
     * get options string array list from request.
     * 
     * @param request request parameter.
     * @return options. if nothing, null.
     */
    public static List<String> getOptions(final Intent request) {
        return request.getStringArrayListExtra(PARAM_OPTIONS);
    }

    
    /**
     * get threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_THRESHOLD);
        return threshold;
    }
    
    /**
     * get minWidth from request.
     * 
     * @param request request parameter.
     * @return minWidth(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getMinWidth(final Intent request) {
        Double minWidth = parseDouble(request, PARAM_MINWIDTH);
        return minWidth;
    }
    
    /**
     * get minHeight from request.
     * 
     * @param request request parameter.
     * @return minHeight(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getMinHeight(final Intent request) {
        Double minHeight = parseDouble(request, PARAM_MINHEIGHT);
        return minHeight;
    }
    
    /**
     * get maxWidth from request.
     * 
     * @param request request parameter.
     * @return maxWidth(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getMaxWidth(final Intent request) {
        Double maxWidth = parseDouble(request, PARAM_MAXWIDTH);
        return maxWidth;
    }
    
    /**
     * get maxHeight from request.
     * 
     * @param request request parameter.
     * @return maxHeight(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getMaxHeight(final Intent request) {
        Double maxHeight = parseDouble(request, PARAM_MAXHEIGHT);
        return maxHeight;
    }

    /**
     * get eye threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getEyeThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_EYE_THRESHOLD);
        return threshold;
    }

    /**
     * get nose threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getNoseThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_NOSE_THRESHOLD);
        return threshold;
    }

    /**
     * get mouth threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getMouthThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_MOUTH_THRESHOLD);
        return threshold;
    }

    /**
     * get blink threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getBlinkThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_BLINK_THRESHOLD);
        return threshold;
    }

    /**
     * get age threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getAgeThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_AGE_THRESHOLD);
        return threshold;
    }

    /**
     * get gender threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getGenderThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_GENDER_THRESHOLD);
        return threshold;
    }

    /**
     * get face direction threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getFaceDirectionThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_FACE_DIRECTION_THRESHOLD);
        return threshold;
    }

    /**
     * get gaze threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getGazeThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_GAZE_THRESHOLD);
        return threshold;
    }

    /**
     * get expression threshold from request.
     * 
     * @param request request parameter.
     * @return threshold(0.0 ... 1.0). if nothing, null.
     * @throw NumberFormatException
     */
    public static Double getExpressionThreshold(final Intent request) {
        Double threshold = parseDouble(request, PARAM_EXPRESSION_THRESHOLD);
        return threshold;
    }
    
    // ------------------------------------
    // Setter methods.
    // ------------------------------------

    /**
     * set body detects data to response.
     * 
     * @param response response
     * @param bodyDetects body detects data.
     */
    public static void setBodyDetects(final Intent response, final Bundle[] bodyDetects) {
        response.putExtra(PARAM_BODYDETECTS, bodyDetects);
    }

    /**
     * set hand detects data to response.
     * 
     * @param response response
     * @param handDetects hand detects data.
     */
    public static void setHandDetects(final Intent response, final Bundle[] handDetects) {
        response.putExtra(PARAM_HANDDETECTS, handDetects);
    }

    /**
     * set face detects data to response.
     * 
     * @param response response
     * @param faceDetects face detects data.
     */
    public static void setFaceDetects(final Intent response, final Bundle[] faceDetects) {
        response.putExtra(PARAM_FACEDETECTS, faceDetects);
    }

    /**
     * set normalize x value to bundle.
     * @param bundle bundle
     * @param normalizeX normalize X value.
     */
    public static void setParamX(final Bundle bundle, final double normalizeX) {
        bundle.putDouble(PARAM_X, normalizeX);
    }

    /**
     * set normalize y value to bundle.
     * @param bundle bundle
     * @param normalizeY normalize Y value.
     */
    public static void setParamY(final Bundle bundle, final double normalizeY) {
        bundle.putDouble(PARAM_Y, normalizeY);
    }

    /**
     * set normalize width value to bundle.
     * @param bundle bundle
     * @param normalizeWidth normalize width value
     */
    public static void setParamWidth(final Bundle bundle, final double normalizeWidth) {
        bundle.putDouble(PARAM_WIDTH, normalizeWidth);
    }

    /**
     * set normalize height value to bundle.
     * @param bundle bundle
     * @param normalizeHeight normalize height value
     */
    public static void setParamHeight(final Bundle bundle, final double normalizeHeight) {
        bundle.putDouble(PARAM_HEIGHT, normalizeHeight);
    }

    /**
     * set normalize confidence value to bundle.
     * @param bundle bundle
     * @param normalizeConfidence normalize confidence value
     */
    public static void setParamConfidence(final Bundle bundle, final double normalizeConfidence) {
        bundle.putDouble(PARAM_CONFIDENCE, normalizeConfidence);
    }
    
    /**
     * set yaw degree value to bundle.
     * @param bundle bundle
     * @param yawDegree yaw Degree value.
     */
    public static void setParamYaw(final Bundle bundle, final double yawDegree) {
        bundle.putDouble(PARAM_YAW, yawDegree);
    }
    
    /**
     * set roll degree value to bundle.
     * @param bundle bundle
     * @param rollDegree roll Degree value.
     */
    public static void setParamRoll(final Bundle bundle, final double rollDegree) {
        bundle.putDouble(PARAM_ROLL, rollDegree);
    }
    
    /**
     * set pitch degree value to bundle.
     * @param bundle bundle
     * @param pitchDegree pitch Degree value.
     */
    public static void setParamPitch(final Bundle bundle, final double pitchDegree) {
        bundle.putDouble(PARAM_PITCH, pitchDegree);
    }
    
    /**
     * set age value to bundle.
     * @param bundle bundle
     * @param age age value.
     */
    public static void setParamAge(final Bundle bundle, final int age) {
        bundle.putInt(PARAM_AGE, age);
    }
    
    /**
     * set gender value to bundle.
     * @param bundle bundle
     * @param gender gender value.
     */
    public static void setParamGender(final Bundle bundle, final String gender) {
        bundle.putString(PARAM_GENDER, gender);
    }
    
    /**
     * set gazeLR value to bundle.
     * @param bundle bundle
     * @param gazeLR gazeLR value.
     */
    public static void setParamGazeLR(final Bundle bundle, final double gazeLR) {
        bundle.putDouble(PARAM_GAZE_LR, gazeLR);
    }
    
    /**
     * set gazeUD value to bundle.
     * @param bundle bundle
     * @param gazeUD gazeUD value.
     */
    public static void setParamGazeUD(final Bundle bundle, final double gazeUD) {
        bundle.putDouble(PARAM_GAZE_UD, gazeUD);
    }
    
    /**
     * set left eye value to bundle.
     * @param bundle bundle
     * @param leftEye left eye value.
     */
    public static void setParamLeftEye(final Bundle bundle, final double leftEye) {
        bundle.putDouble(PARAM_LEFTEYE, leftEye);
    }
    
    /**
     * set right eye value to bundle.
     * @param bundle bundle
     * @param rightEye right eye value.
     */
    public static void setParamRightEye(final Bundle bundle, final double rightEye) {
        bundle.putDouble(PARAM_RIGHTEYE, rightEye);
    }
    
    /**
     * set expression value to bundle.
     * @param bundle bundle
     * @param expression expression value.
     */
    public static void setParamExpression(final Bundle bundle, final String expression) {
        bundle.putString(PARAM_EXPRESSION, expression);
    }
    
    
    
    
    
    /**
     * set face direction result to bundle.
     * @param bundle bundle
     * @param faceDirectionResult face direction result.
     */
    public static void setParamFaceDirectionResult(final Bundle bundle, final Bundle faceDirectionResult) {
        bundle.putBundle(PARAM_FACEDIRECTIONRESULTS, faceDirectionResult);
    }
    
    /**
     * set age value to bundle.
     * @param bundle bundle
     * @param ageResult age result.
     */
    public static void setParamAgeResult(final Bundle bundle, final Bundle ageResult) {
        bundle.putBundle(PARAM_AGERESULTS, ageResult);
    }
    
    /**
     * set gender value to bundle.
     * @param bundle bundle
     * @param genderResult gender result.
     */
    public static void setParamGenderResult(final Bundle bundle, final Bundle genderResult) {
        bundle.putBundle(PARAM_GENDERRESULTS, genderResult);
    }
    
    /**
     * set gaze value to bundle.
     * @param bundle bundle
     * @param gazeResult gaze result.
     */
    public static void setParamGazeResult(final Bundle bundle, final Bundle gazeResult) {
        bundle.putBundle(PARAM_GAZERESULTS, gazeResult);
    }
    
    /**
     * set blink value to bundle.
     * @param bundle bundle
     * @param blinkResult blink result.
     */
    public static void setParamBlinkResult(final Bundle bundle, final Bundle blinkResult) {
        bundle.putBundle(PARAM_BLINKRESULTS, blinkResult);
    }
    
    /**
     * set expression value to bundle.
     * @param bundle bundle
     * @param expressionResult expression result.
     */
    public static void setParamExpressionResult(final Bundle bundle, final Bundle expressionResult) {
        bundle.putBundle(PARAM_EXPRESSIONRESULTS, expressionResult);
    }
}
