package com.example.matchinggame;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matchinggame.GameActivity;

import java.util.Random;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("CLOSE_TWO_CARDS")) {
//
//            Random rand = new Random();
//            Toast.makeText(context, quotes[rand.nextInt(quotes.length)],
//                    Toast.LENGTH_SHORT).show();
//
//
//
////            GameActivity.closeTwoCards(R.id.gridview);
////            context.getResources().get;
//            ImageView firstItem = (ImageView) intent.getSerializableExtra("firstItem");
//            ImageView secondItem = (ImageView) intent.getSerializableExtra("secondItem");
//        }

        if (intent.getAction().equals("CLOSE_TWO")) {

            Toast.makeText(context, "@@@@@@@@@@@@@@@@@@@@@@@", Toast.LENGTH_LONG).show();
            GameActivity gameActivity = new GameActivity();

//            ImageView firstItem = (ImageView)((GridView)gameActivity.findViewById(R.id.GridView)).getChildAt(0);
//            firstItem.setImageDrawable(getDrawable(card, null));

            GridView gridView = ((Activity)context).findViewById(R.id.GridView);
            ImageView firstItem = (ImageView) gridView.getChildAt(0);
            firstItem.setImageResource(R.drawable.card);
        }

    }
}