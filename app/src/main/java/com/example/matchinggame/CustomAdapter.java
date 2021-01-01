package com.example.matchinggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int logos[];
    LayoutInflater inflter;
    private List<Photo> objects = new ArrayList<Photo>();
//    private List<Integer> imageClicked = new ArrayList<Integer>();
    public CustomAdapter(Context appContext, List<Photo> logos) {
//        this.logos=logos;
        this.context = appContext;
        inflter = (LayoutInflater.from(context));
        this.objects = logos;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Photo getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
            viewHolder = new ViewHolder();

            viewHolder.photos = (ImageView) convertView.findViewById(R.id.imageView);

            viewHolder.check = (ImageView) convertView.findViewById(R.id.check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.photos.setImageResource(getItem(position).getPhotoId());
        if (getItem(position).isPhotoChecked()) {
            viewHolder.check.setImageResource(R.drawable.selected);
        }

        else {
            viewHolder.check.setImageResource(R.drawable.non_selected);
        }
        return convertView;
    }

    protected class ViewHolder {
        ImageView photos;
        ImageView check;
    }
}

