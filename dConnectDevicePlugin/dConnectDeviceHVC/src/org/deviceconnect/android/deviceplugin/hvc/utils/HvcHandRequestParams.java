package org.deviceconnect.android.deviceplugin.hvc.utils;

/**
 * HVC hand detect request parameter class.
 */
public class HvcHandRequestParams extends HumanDetectHandRequestParams {

    /**
     * Constructor(with default value).
     * @param normalizeThreshold threshold
     * @param normalizeMinWidth minWidth
     * @param normalizeMinHeight minHeight
     * @param normalizeMaxWidth maxWidth
     * @param normalizeMaxHeight maxHeight
     */
    public HvcHandRequestParams(final double normalizeThreshold, final double normalizeMinWidth,
            final double normalizeMinHeight, final double normalizeMaxWidth, final double normalizeMaxHeight) {
        super(normalizeThreshold, normalizeMinWidth, normalizeMinHeight, normalizeMaxWidth, normalizeMaxHeight);
    }

    /**
     * Get threshold value(HVC device value).
     * @return HVC threshold(HVC device value)
     */
    public int getHvcThreshold() {
        int hvcThreshold = HvcConvertUtils.convertToHvcThreshold(getThreshold());
        return hvcThreshold;
    }
    
    /**
     * Get min width value(HVC device value).
     * @return HVC min width value(HVC device value)
     */
    public int getHvcMinWidth() {
        int hvcMinWidth = HvcConvertUtils.convertToHvcWidth(getMinWidth());
        return hvcMinWidth;
    }
    
    /**
     * Get min height value(HVC device value).
     * @return HVC min height value(HVC device value)
     */
    public int getHvcMinHeight() {
        int hvcMinHeight = HvcConvertUtils.convertToHvcHeight(getMinHeight());
        return hvcMinHeight;
    }
    
    /**
     * Get max width value(HVC device value).
     * @return HVC max width value(HVC device value)
     */
    public int getHvcMaxWidth() {
        int hvcMaxWidth = HvcConvertUtils.convertToHvcWidth(getMaxWidth());
        return hvcMaxWidth;
    }
    
    /**
     * Get max height value(HVC device value).
     * @return HVC max height value(HVC device value)
     */
    public int getHvcMaxHeight() {
        int hvcMaxHeight = HvcConvertUtils.convertToHvcHeight(getMaxHeight());
        return hvcMaxHeight;
    }
    
}
