package com.bramblellc.myapplication.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithTypeface extends TextView {

    public TextViewWithTypeface(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SIMPLIFICA Typeface.ttf"));
    }

    public TextViewWithTypeface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SIMPLIFICA Typeface.ttf"));
    }

    public TextViewWithTypeface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SIMPLIFICA Typeface.ttf"));
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
}
