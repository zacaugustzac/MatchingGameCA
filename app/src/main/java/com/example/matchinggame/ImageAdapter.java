package com.example.matchinggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;



public class ImageAdapter extends BaseAdapter {

    private Context context;

    public ImageAdapter (Context context){

        this.context = context;

    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(250,280));

        }

        else imageView = (ImageView)convertView;

        imageView.setBackgroundResource(R.drawable.card);

        return imageView;
    }
}