package com.paranoidandroid.journey.support.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A Toolbar that allows clicks to pass through. This is really only useful if the toolbar is
 * transparent.
 */
public class NonClickableToolbar extends Toolbar {
    public NonClickableToolbar(Context context) {
        super(context);
    }

    public NonClickableToolbar(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NonClickableToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
