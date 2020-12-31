package com.example.matchinggame;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

public class GlideUtil {
    public static RequestBuilder<Drawable> GlideWithPlaceHolder(Context context, Object object) {
        return Glide.with(context).load(object).apply(new RequestOptions().placeholder(R.drawable.default_image).dontAnimate());
    }


}
