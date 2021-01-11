package com.example.matchinggame;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class PhotoFrame extends androidx.appcompat.widget.AppCompatImageView {

    Photo image = new Photo();

    public PhotoFrame(Context context) {
        super(context);
    }

    public PhotoFrame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoFrame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Photo getImage() {
        return image;
    }

    public void setImage(Photo image) {
        this.image = image;
    }
}
