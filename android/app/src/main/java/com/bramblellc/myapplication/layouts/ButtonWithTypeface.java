package com.bramblellc.myapplication.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonWithTypeface extends Button {

    public ButtonWithTypeface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SIMPLIFICA Typeface.ttf"));
    }

    public ButtonWithTypeface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SIMPLIFICA Typeface.ttf"));
    }

    public ButtonWithTypeface(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SIMPLIFICA Typeface.ttf"));
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
}
