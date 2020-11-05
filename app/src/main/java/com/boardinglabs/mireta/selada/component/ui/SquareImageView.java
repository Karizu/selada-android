package com.boardinglabs.mireta.selada.component.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Dhimas on 2/6/18.
 */

public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(final Context context, final AttributeSet attrs,
                           final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (d != null) {
            final int width = getMeasuredWidth();
            final int height = width;
            this.setMeasuredDimension(width, height);
        }
    }
}
