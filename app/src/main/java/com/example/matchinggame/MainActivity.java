package com.example.matchinggame;


import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button fetchbtn;
    private ImageButton imgbtn;
    private Button startbtn;
    private ProgressBar bar;
    private TextView msg;
    private TextView guide;
    private Handler handler= new Handler();
    int status=0;
    private final int imagetotal=8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchbtn = findViewById(R.id.fetchbtn);
        fetchbtn.setOnClickListener(this);
        startbtn=findViewById(R.id.start);
        startbtn.setOnClickListener(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void storeImageInStorage(ImageFetcher im,String imgurl,File file){
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

    @Override
    public void onClick(View view) {
        if (view == fetchbtn) {

            ImageFetcher im = new ImageFetcher();
            try {
                List<String> imageurl = im.extractImage(); //all image downloaded
                bar = findViewById(R.id.progress);
                bar.setVisibility(View.VISIBLE);
                msg = findViewById(R.id.progressmsg);
                msg.setVisibility(View.VISIBLE);
                //setProgressValue(0,im,imageurl,1);
                new Thread(new Runnable(){
                    @Override
                    public void run() {

                        while (status<imagetotal){
                            status++;
                            handler.post(new Runnable() {
                                public void run() {
                                    bar.setProgress(status * 10);
                                    String mess = "Downloading " + (status) + " of "+imagetotal+" images...";
                                    msg.setText(mess);
                                    loadImage(im,  imageurl, status);
                                    if(status==imagetotal){
                                        bar.setVisibility(View.GONE);
                                        msg.setVisibility(View.GONE);
                                        //startbtn.setVisibility(View.VISIBLE);
                                        guide=findViewById(R.id.guide);
                                        guide.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            try {Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if(view.getId()==R.id.img1){
            Toast.makeText(this,"image 1 is clicked", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(ImageFetcher im, List<String> imageurl, int x) {
        String imgurl=imageurl.get(x-1);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, "image" + x + ".jpg");
        if (!file.exists()) {
            storeImageInStorage(im,imgurl,file);
        }
        imgbtn = findViewById(getResources().getIdentifier("img" + x, "id", this.getPackageName()));
        imgbtn.setOnClickListener(this);

        bar.setProgress(10*x);
        String mess="Downloading " + x + " of "+imagetotal+" images...";
        msg.setText(mess);
        imgbtn.setBackground(Drawable.createFromPath(file.toString()));

    }
}