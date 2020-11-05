package com.boardinglabs.mireta.selada.component.fontview;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Dhimas on 9/25/17.
 */

public class RobotoLightTextView extends AppCompatTextView {
    public RobotoLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setTypeface(FontHelper.getRobotoLight(context));
        }
    }

    public RobotoLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setTypeface(FontHelper.getRobotoLight(context));
        }
    }
}
