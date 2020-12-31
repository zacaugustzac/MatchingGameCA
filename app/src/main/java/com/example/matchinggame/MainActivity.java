package com.example.matchinggame;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity
{
    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private String[] icon = ImagesUrl.Urls;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);

        dataList = new ArrayList<Map<String, Object>>();

        adapter = new SimpleAdapter(this, getdata(), R.layout.gridview_item,
                new String[] {"image"}, new int[] { R.id.imageView});



        adapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if(view instanceof ImageView){
                    ImageView iv = (ImageView)view;

                    GlideUtil.GlideWithPlaceHolder(MainActivity.this, data.toString()).into(iv);
                    return true;
                }else
                    return false;
            }
        });
        gridView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getdata()
    {
        for(int i=0;i<icon.length;i++){
            Map<String, Object>map=new HashMap<String, Object>();
            map.put("image", icon[i]);
            dataList.add(map);
        }
        return dataList;
    }
}