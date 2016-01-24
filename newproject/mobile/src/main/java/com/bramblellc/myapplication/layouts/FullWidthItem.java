package com.bramblellc.myapplication.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bramblellc.myapplication.R;

public class FullWidthItem extends RelativeLayout {

    private TextView primaryTextView;
    private TextView secondaryTextView;
    private RelativeLayout textViewContainer;

    private String text;
    private boolean lowerText;
    private boolean lowerLine;

    public FullWidthItem(Context context) {
        super(context);
    }

    public FullWidthItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FullWidthItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FullWidthButton);
        //Use a
        this.text = a.getString(R.styleable.FullWidthItem_android_text);
        this.lowerText = a.getBoolean(R.styleable.FullWidthItem_lowerText, false);
        this.lowerLine = a.getBoolean(R.styleable.FullWidthItem_lowerLine, false);
        //Don't forget this
        a.recycle();

        if(lowerLine)
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.full_width_button));
        else
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.full_width_button_nobottom));

        // retrieves the density of current phone for scaling px to dp in setPadding(w,x,y,z);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        primaryTextView = new TextView(context);
        primaryTextView.setText(text);
        primaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.list_item_primary_text_size));
        // left, top, right, bottom v v v v
        primaryTextView.setPadding(12*(int)logicalDensity, 0, 0, 0);

        secondaryTextView = new TextView(context);

        LayoutParams params = new LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        if(lowerText) {
            // configures the secondary textview
            secondaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.list_item_secondary_text_size));
            secondaryTextView.setTextColor(context.getResources().getColor(R.color.secondary_body_text_color));
            secondaryTextView.setPadding(12*(int)logicalDensity, 0, 0, 0);

            LayoutParams paramsPrimary = new LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            primaryTextView.setId(1);

            LayoutParams paramsSecondary = new LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsSecondary.addRule(RelativeLayout.BELOW, primaryTextView.getId());

            // adds primary and secondary textviews to the relative layout container
            textViewContainer = new RelativeLayout(context);
            textViewContainer.addView(primaryTextView, paramsPrimary);
            textViewContainer.addView(secondaryTextView, paramsSecondary);

            // adds the container to this custom relative layout with appropriate "rules"
            this.addView(textViewContainer, params);
        }
        else {
            // adds the primary textview to this custom relative layout with appropriate "rules"
            this.addView(primaryTextView, params);
        }
    }

    public String getSecondaryTextViewText() {
        return secondaryTextView.getText().toString();
    }

    public void setSecondaryTextViewText(String newText) {
        secondaryTextView.setText(newText);
    }
}
