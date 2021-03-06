/*
DConnectUtil
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.sonycamera.utils;

import org.deviceconnect.android.deviceplugin.sonycamera.BuildConfig;
import org.deviceconnect.profile.ServiceDiscoveryProfileConstants;

/**
 * ユーティリティクラス.
 * @author NTT DOCOMO, INC.
 */
public final class DConnectUtil {
    /** SonyCameraのWiFiのプレフィックス. */
    private static final String WIFI_PREFIX = "DIRECT-";
    /**
     * Camera Remote APIに対応したWiFiのSSIDのサフィックスを定義.
     */
    public static final String[] CAMERA_SUFFIX = {"HDR-AS100", "ILCE-6000", "DSC-HC60V", "DSC-HX400", "ILCE-5000",
            "DSC-QX10", "DSC-QX100", "HDR-AS15", "HDR-AS30", "HDR-MV1", "NEX-5R", "NEX-5T", "NEX-6", "ILCE-7R/B",
            "ILCE-7/B" };

    /** dConnectManagerのURI. */
    private static final String BASE_URI = "http://localhost:4035";

    /** Service Discovery ProfileのURI. */
    private static final String DISCOVERY_URI = BASE_URI + "/" + ServiceDiscoveryProfileConstants.PROFILE_NAME;

    /**
     * デバック用フラグ.
     */
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * デバック用タグを定義します.
     */
    private static final String TAG = "SONYCAMERA";

    /**
     * コンストラクタ. ユーティリティクラスなのでprivateにしておく。
     */
    private DConnectUtil() {
    }

    /**
     * 指定されたSSIDがSonyCameraデバイスのWifiのSSIDかチェックする.
     * 
     * @param ssid SSID
     * @return SonyCameraデバイスのSSIDの場合はtrue、それ以外はfalse
     */
    public static boolean checkSSID(final String ssid) {
        if (ssid == null) {
            return false;
        }
        String id = ssid.replace("\"", "");
        if (id.startsWith(WIFI_PREFIX)) {
            for (String aCAMERA_SUFFIX : CAMERA_SUFFIX) {
                if (id.indexOf(aCAMERA_SUFFIX) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
