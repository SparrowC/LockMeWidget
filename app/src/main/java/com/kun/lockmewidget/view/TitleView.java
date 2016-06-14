package com.kun.lockmewidget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kun.lockmewidget.R;

/**
 * Created by kun on 16-6-13.
 */
public class TitleView extends RelativeLayout {

    private CustomShapeImageView logoImage;
    private TextView titleText;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        logoImage= (CustomShapeImageView) findViewById(R.id.iv_logo);
        titleText = (TextView) findViewById(R.id.tv_text);
    }

    public TextView getTitleText() {
        return titleText;
    }

    public void setTitleText(String text) {
        titleText.setText(text);
    }

    public CustomShapeImageView getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(int imageID) {
        logoImage.setImageResource(imageID);
    }

    public void setLogoImage(Drawable image) {
        logoImage.setImageDrawable(image);
    }

    public void setLogoImage(Bitmap image) {
        logoImage.setImageBitmap(image);
    }


}
