package com.example.matchinggame;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ScoreAdapter extends ArrayAdapter {
    private Context context;
    private JSONArray content;

    public ScoreAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context=context;
    }

    public void setData(JSONArray content){
        this.content=content;
        for(int x=0;x<content.length();x++){
            add(null);
        }
    }

    public View getView(int pos, View view, ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.row,parent,false);
        }
        TextView textname=view.findViewById(R.id.playername);
        TextView texttime=view.findViewById(R.id.playertime);
        try {
            textname.setText(content.getJSONObject(pos).optString("name","noname"));
            texttime.setText(content.getJSONObject(pos).get("time").toString()+" second");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }


}
