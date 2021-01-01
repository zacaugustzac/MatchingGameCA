package com.example.matchinggame;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button fetchbtn;
    private ImageView imgbtn;
    private Button startbtn;
    private ProgressBar bar;
    private TextView msg;
    private TextView guide;
    private Handler handler = new Handler();
    int status = 0;
    private final int imagetotal = 20;
    private final int imageselected = 6;
    int[] logos = new int[imagetotal];
    private GridView simplegrid;
    private List<Integer> imageClicked = new ArrayList<Integer>();
    private List<Photo> photoList;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPhotoData();
        simplegrid = (GridView) findViewById(R.id.GridView);
        adapter = new CustomAdapter(getApplicationContext(), photoList);
        simplegrid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        fetchbtn = findViewById(R.id.fetchbtn);
        fetchbtn.setOnClickListener(this);
        startbtn = findViewById(R.id.start);
        startbtn.setOnClickListener((view->{
            Intent intent= new Intent(this,GameActivity.class);
            intent.putIntegerArrayListExtra("chosenimage",(ArrayList<Integer>)imageClicked);
            startActivity(intent);

        }));
        bar = findViewById(R.id.progress);
        msg = findViewById(R.id.progressmsg);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    public void storeImageInStorage(ImageFetcher im, String imgurl, File file) {
        Log.d("path", file.toString());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            im.convertImage(imgurl).compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    public void setAdapterState(int index){
        if(imageClicked.size()<imageselected){
            photoList.get(index).setOppositeCheck();
            boolean photoChecked = photoList.get(index).isPhotoChecked();
            if (photoChecked==true){
                imageClicked.add(index);
                if(imageClicked.size()==imageselected){
                    startbtn.setVisibility(View.VISIBLE);
                    guide.setVisibility(View.GONE);
                }
            }else {
                for (int i = 0; i < imageClicked.size(); i++) {
                    if (imageClicked.get(i)==index){
                        imageClicked.remove(i);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }else if(imageClicked.size()==imageselected&&photoList.get(index).isPhotoChecked()) {
            photoList.get(index).setOppositeCheck();
            boolean photoChecked = photoList.get(index).isPhotoChecked();
            if (photoChecked==true){
                imageClicked.add(index);
            }else {
                for (int i = 0; i < imageClicked.size(); i++) {
                    if (imageClicked.get(i)==index){
                        imageClicked.remove(i);
                        startbtn.setVisibility(View.GONE);
                        guide.setVisibility(View.VISIBLE);

                    }
                }
            }
            adapter.notifyDataSetChanged();
        }else {
            Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onClick(View view) {
        if (view == fetchbtn) {

            ImageFetcher im = new ImageFetcher();
            try {
                List<String> imageurl = im.extractImage();
                bar.setVisibility(View.VISIBLE);
                msg.setVisibility(View.VISIBLE);

                new Thread(() -> {
                    while (status < imagetotal) {
                        status++;
                        handler.post(() -> {
                            bar.setProgress(status * 10);
                            String mess = "Downloading " + (status) + " of " + imagetotal + " images...";
                            msg.setText(mess);
                            loadImage(im, imageurl, status);
                            if (status == imagetotal) {
                                bar.setVisibility(View.GONE);
                                msg.setVisibility(View.GONE);
                                //startbtn.setVisibility(View.VISIBLE);
                                guide = findViewById(R.id.guide);
                                guide.setVisibility(View.VISIBLE);
                            }
                        });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        //assign every button click function to update the state of app for having only 6 images
        for(int x=0;x<imagetotal;x++){
            if(view==simplegrid.getChildAt(x).findViewById(R.id.imageView)){
                setAdapterState(x);
            }
        }
    }

    private void loadImage(ImageFetcher im, List<String> imageurl, int x) {
        String imgurl = imageurl.get(x - 1);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, "image" + x + ".jpg");
        if (!file.exists()) {
            storeImageInStorage(im, imgurl, file);
        }
        View viewitem = simplegrid.getChildAt(x - 1);
        imgbtn = (ImageView) viewitem.findViewById(R.id.imageView);
        imgbtn.setOnClickListener(this);

        bar.setProgress(10 * x);
        String mess = "Downloading " + x + " of " + imagetotal + " images...";
        msg.setText(mess);
        imgbtn.setBackground(Drawable.createFromPath(file.toString()));
        //imgbtn.setImageResource(Drawable.createFromPath(file.toString()));

    }


    private void getPhotoData() {

        photoList = new ArrayList<Photo>();
        for (int i = 0; i < imagetotal; i++) {
            Photo photo = new Photo(logos[i], false);
            photoList.add(photo);
        }
    }
}