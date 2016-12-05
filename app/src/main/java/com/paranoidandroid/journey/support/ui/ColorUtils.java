package com.paranoidandroid.journey.support.ui;

import android.graphics.Color;

public class ColorUtils {
    /**
     * @param color opaque RGB integer color for ex: -11517920
     * @param ratio ratio of transparency for ex: 0.5f
     * @return transparent RGB integer color
     */
    public static int getColorWithAplha(int color, float ratio)
    {
        int transColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        transColor = Color.argb(alpha, r, g, b);
        return transColor ;
    }
}
