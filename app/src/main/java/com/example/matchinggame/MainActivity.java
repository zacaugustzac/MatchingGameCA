package com.example.matchinggame;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity
    implements View.OnClickListener
{
    //for game
    private int WRITE_QUOTE_RESPONSE = 1;
    private String lastQuote = "To find yourself, think for yourself.";


    //main page
    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private String[] icon = ImagesUrl.Urls;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //game
        Button btn = findViewById(R.id.next);
        if (btn != null)
            btn.setOnClickListener((View.OnClickListener) this);
        //game


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


    //copy from the demo with the button link to next page
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.next) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivityForResult(intent, WRITE_QUOTE_RESPONSE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == WRITE_QUOTE_RESPONSE) {
            String newQuote = intent.getStringExtra("newQuote");
            if (newQuote != null)
                lastQuote = newQuote;
        }
    }
}