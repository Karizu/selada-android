package com.boardinglabs.mireta.selada.component.fontview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Dhimas on 9/25/17.
 */

public class RobotoBoldTextView extends AppCompatTextView {

    public RobotoBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setTypeface(FontHelper.getRobotoBold(context));
        }
    }

    public RobotoBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setTypeface(FontHelper.getRobotoBold(context));
        }
    }

}
