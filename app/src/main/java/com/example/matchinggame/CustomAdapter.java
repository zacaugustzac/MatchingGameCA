package com.example.matchinggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int logos[];
    LayoutInflater inflter;
    public CustomAdapter(Context appContext, int[]logos){
        this.logos=logos;
        this.context=appContext;
        inflter=(LayoutInflater.from(context));
    }
    @Override
    public int getCount() {
        return logos.length;
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
        convertView=inflter.inflate(R.layout.gridview_item,null);
        ImageView icon=convertView.findViewById(R.id.imageView);
        icon.setBackgroundResource(logos[position]);
        return convertView;
    }
}
