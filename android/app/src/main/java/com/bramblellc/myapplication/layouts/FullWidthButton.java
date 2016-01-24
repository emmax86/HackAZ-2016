package com.bramblellc.myapplication.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bramblellc.myapplication.R;


public class FullWidthButton extends RelativeLayout {

    // view contents
    private TextView primaryTextView;
    private TextView secondaryTextView;
    private Button fullWidthButton;
    private RelativeLayout textViewContainer;

    // variables for storing design preferences
    private String text;
    private boolean lowerText;
    private boolean lowerLine;

    public FullWidthButton(Context context) {
        super(context);
    }

    public FullWidthButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FullWidthButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // retrieves information from the xml item
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FullWidthButton);
        this.text = a.getString(R.styleable.FullWidthButton_android_text);
        this.lowerText = a.getBoolean(R.styleable.FullWidthButton_lowerText, false);
        this.lowerLine = a.getBoolean(R.styleable.FullWidthButton_lowerLine, false);
        //Don't forget this
        a.recycle();

        // retrieves the density of current phone for scaling px to dp in setPadding(w,x,y,z);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        // creates button used in this view and then sets the background
        // according to style preferences detailed in the xml item
        // before adding it to the custom relative layout
        fullWidthButton = new Button(context);
        if(lowerLine)
            fullWidthButton.setBackground(ContextCompat.getDrawable(context, R.drawable.full_width_selector));
        else
            fullWidthButton.setBackground(ContextCompat.getDrawable(context, R.drawable.full_width_selector_nobottom));
        LayoutParams buttonParams = new LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(fullWidthButton, buttonParams);

        // creates textview used in this view and then sets the text
        // according to style preferences detailed in the xml item
        primaryTextView = new TextView(context);
        primaryTextView.setText(text);
        primaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.list_item_primary_text_size));
        primaryTextView.setPadding(12*(int)logicalDensity, 0, 0, 0);
        // initializes secondary text view
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

    @Override
    public void setEnabled(boolean enabled) {
        fullWidthButton.setEnabled(enabled);
        if(enabled) {
            this.primaryTextView.setTextColor(getResources().getColor(R.color.primary_body_text_color));
            this.secondaryTextView.setTextColor(getResources().getColor(R.color.secondary_body_text_color));
        }
        else {
            this.primaryTextView.setTextColor(getResources().getColor(R.color.darkGray));
            this.secondaryTextView.setTextColor(getResources().getColor(R.color.mediumGray));
        }
    }

    public Button getFullWidthButton() {
        return this.fullWidthButton;
    }

    public String getSecondaryTextViewText() {
        return secondaryTextView.getText().toString();
    }

    public void setSecondaryTextViewText(String newText) {
        secondaryTextView.setText(newText);
    }
}
