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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bramblellc.myapplication.R;


public class CustomActionbar extends RelativeLayout {

    private TextView title;
    private Button backButton;
    private ImageView backButtonImage;
    private ImageButton customActionbarsearchButton;
    private ImageButton customActionbarsettingsButton;
    private EditText searchBarEditText;

    private String text;
    private Integer textColor;
    private Integer backgroundType;
    private String backButtonColor;
    private boolean searchButton;
    private boolean settingsButton;
    private boolean searchBar;

    public CustomActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomActionbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomActionbar);
        //Use a
        this.text = a.getString(R.styleable.CustomActionbar_android_text);
        this.textColor = a.getInteger(R.styleable.CustomActionbar_android_textColor, getResources().getColor(R.color.black));
        this.backgroundType = a.getInteger(R.styleable.CustomActionbar_backgroundType, -1);
        this.backButtonColor = a.getString(R.styleable.CustomActionbar_backButtonColor);
        this.searchButton = a.getBoolean(R.styleable.CustomActionbar_searchButton, false);
        this.settingsButton = a.getBoolean(R.styleable.CustomActionbar_settingsButton, false);
        this.searchBar = a.getBoolean(R.styleable.CustomActionbar_searchBar, false);
        //Don't forget this
        a.recycle();

        this.setBackgroundColor(getResources().getColor(R.color.white));



        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        this.backButton = new Button(context);
        this.backButton.setWidth(75 * (int) logicalDensity);
        this.backButton.setBackgroundColor(getResources().getColor(R.color.transparent));

        LayoutParams backButtonParams = new LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        backButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        this.addView(backButton, backButtonParams);

        backButtonImage = new ImageView(context);
        if(backButtonColor.equals("blue")) {
            this.backButtonImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.custom_white_up_button));
        }
        else if (backButtonColor.equals("purple")) {
            this.backButtonImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.custom_purple_up_button));
        }
        else {
            this.backButtonImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.custom_white_up_button));
        }

        LayoutParams backButtonImageParams = new LayoutParams
                ((int) (getResources().getDimension(R.dimen.up_button_side_lengths)*logicalDensity),
                        (int) (getResources().getDimension(R.dimen.up_button_side_lengths)*logicalDensity));
        this.backButtonImage.setLayoutParams(backButtonImageParams);
        this.backButtonImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.backButtonImage.setPadding((int) (logicalDensity * getResources().getDimension(R.dimen.up_button_padding)), 0,
                (int) (logicalDensity * getResources().getDimension(R.dimen.up_button_padding)), 0);
        this.backButtonImage.setId(1);

        LayoutParams upButtonImageParams = new LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        upButtonImageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        upButtonImageParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        this.addView(backButtonImage, upButtonImageParams);

        LayoutParams textParams = new LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.RIGHT_OF, backButtonImage.getId());
        textParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        this.title = new TextView (context);
        this.title.setText(text);
        this.title.setTextColor(textColor);
        this.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.action_bar_text_size));
        this.addView(title,textParams);
    }

    public void setEnabled(boolean enabled) {
        this.backButton.setEnabled(enabled);
        if(searchButton)
            this.customActionbarsearchButton.setEnabled(enabled);
        if(settingsButton)
            this.customActionbarsettingsButton.setEnabled(enabled);
        if(searchBar)
            this.searchBarEditText.setEnabled(enabled);
    }

    public Button getBackButton() {
        return this.backButton;
    }

    public void setEditTextHint(String hint) {
        if(searchBar)
            this.searchBarEditText.setHint(hint);
    }

    public EditText getSearchBar() {
        if(searchBar)
            return this.searchBarEditText;
        return null;
    }

    public ImageButton getSearchButton() {
        if(searchBar||searchButton)
            return this.customActionbarsearchButton;
        return null;
    }

    public ImageButton getSettingsButton() {
        if(settingsButton)
            return this.customActionbarsettingsButton;
        return null;
    }
}
