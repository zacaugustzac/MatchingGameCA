package com.example.matchinggame;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class Card {

    private ImageView card_front;
    private ImageView card_back;
    private Integer position;
    public Drawable card_backDrawable = card_back.getDrawable();
    public Drawable card_frontDrawable = card_front.getDrawable();
    ArrayList<Integer> chosenImagesArr = new ArrayList<>();
    ArrayList<Bitmap> chosenImages = new ArrayList<>();

    public Card(ImageView card_front, ImageView card_back) {
        this.card_front = card_front;
        this.card_back = card_back;
    }

    public void cardFlip(FragmentActivity flip){

    }
}
