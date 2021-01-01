package com.example.matchinggame;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.ContextWrapper;
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
        startbtn.setOnClickListener(this);
        bar = findViewById(R.id.progress);
        msg = findViewById(R.id.progressmsg);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        System.out.println("123");

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
        } else if (view == simplegrid.getChildAt(0).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(0).setOppositeCheck();
                boolean photoChecked = photoList.get(0).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(0);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==0){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(0).isPhotoChecked()) {
                photoList.get(0).setOppositeCheck();
                boolean photoChecked = photoList.get(2).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(0);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==0){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(1).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(1).setOppositeCheck();
                boolean photoChecked = photoList.get(1).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(1);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==1){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(1).isPhotoChecked()) {
                photoList.get(1).setOppositeCheck();
                boolean photoChecked = photoList.get(1).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(1);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==1){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }


        } else if (view == simplegrid.getChildAt(2).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(2).setOppositeCheck();
                boolean photoChecked = photoList.get(2).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(2);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==2){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(2).isPhotoChecked()) {
                photoList.get(2).setOppositeCheck();
                boolean photoChecked = photoList.get(2).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(2);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==2){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }



        } else if (view == simplegrid.getChildAt(3).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(3).setOppositeCheck();
                boolean photoChecked = photoList.get(3).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(3);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==3){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(3).isPhotoChecked()) {
                photoList.get(3).setOppositeCheck();
                boolean photoChecked = photoList.get(3).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(3);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==3){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(4).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(4).setOppositeCheck();
                boolean photoChecked = photoList.get(4).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(4);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==4){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(4).isPhotoChecked()) {
                photoList.get(4).setOppositeCheck();
                boolean photoChecked = photoList.get(4).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(4);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==4){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(5).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(5).setOppositeCheck();
                boolean photoChecked = photoList.get(5).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(5);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==5){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(5).isPhotoChecked()) {
                photoList.get(5).setOppositeCheck();
                boolean photoChecked = photoList.get(5).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(5);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==5){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(6).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(6).setOppositeCheck();
                boolean photoChecked = photoList.get(6).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(6);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==6){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(6).isPhotoChecked()) {
                photoList.get(6).setOppositeCheck();
                boolean photoChecked = photoList.get(6).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(6);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==6){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(7).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(7).setOppositeCheck();
                boolean photoChecked = photoList.get(7).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(7);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==7){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(7).isPhotoChecked()) {
                photoList.get(7).setOppositeCheck();
                boolean photoChecked = photoList.get(7).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(7);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==7){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(8).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(8).setOppositeCheck();
                boolean photoChecked = photoList.get(8).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(8);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==8){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(8).isPhotoChecked()) {
                photoList.get(8).setOppositeCheck();
                boolean photoChecked = photoList.get(8).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(8);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==8){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(9).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(9).setOppositeCheck();
                boolean photoChecked = photoList.get(9).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(9);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==9){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(9).isPhotoChecked()) {
                photoList.get(9).setOppositeCheck();
                boolean photoChecked = photoList.get(9).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(9);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==9){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(10).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(10).setOppositeCheck();
                boolean photoChecked = photoList.get(10).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(10);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==10){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(10).isPhotoChecked()) {
                photoList.get(10).setOppositeCheck();
                boolean photoChecked = photoList.get(10).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(10);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==10){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(11).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(11).setOppositeCheck();
                boolean photoChecked = photoList.get(11).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(11);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==11){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(11).isPhotoChecked()) {
                photoList.get(11).setOppositeCheck();
                boolean photoChecked = photoList.get(11).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(11);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==11){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(12).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(12).setOppositeCheck();
                boolean photoChecked = photoList.get(12).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(12);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==12){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(12).isPhotoChecked()) {
                photoList.get(12).setOppositeCheck();
                boolean photoChecked = photoList.get(12).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(12);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==12){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(13).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(13).setOppositeCheck();
                boolean photoChecked = photoList.get(13).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(13);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==13){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(13).isPhotoChecked()) {
                photoList.get(13).setOppositeCheck();
                boolean photoChecked = photoList.get(13).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(13);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==13){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(14).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(14).setOppositeCheck();
                boolean photoChecked = photoList.get(14).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(14);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==14){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(14).isPhotoChecked()) {
                photoList.get(14).setOppositeCheck();
                boolean photoChecked = photoList.get(14).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(14);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==14){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(15).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(15).setOppositeCheck();
                boolean photoChecked = photoList.get(15).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(15);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==15){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(15).isPhotoChecked()) {
                photoList.get(15).setOppositeCheck();
                boolean photoChecked = photoList.get(15).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(15);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==15){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(16).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(16).setOppositeCheck();
                boolean photoChecked = photoList.get(16).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(16);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==16){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(16).isPhotoChecked()) {
                photoList.get(16).setOppositeCheck();
                boolean photoChecked = photoList.get(16).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(16);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==16){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(17).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(17).setOppositeCheck();
                boolean photoChecked = photoList.get(17).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(17);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==17){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(17).isPhotoChecked()) {
                photoList.get(17).setOppositeCheck();
                boolean photoChecked = photoList.get(17).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(17);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==17){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(18).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(18).setOppositeCheck();
                boolean photoChecked = photoList.get(18).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(18);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==18){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(18).isPhotoChecked()) {
                photoList.get(18).setOppositeCheck();
                boolean photoChecked = photoList.get(18).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(18);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==18){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        } else if (view == simplegrid.getChildAt(19).findViewById(R.id.imageView)) {
            if(imageClicked.size()<6){
                photoList.get(19).setOppositeCheck();
                boolean photoChecked = photoList.get(19).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(19);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==19){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else if(imageClicked.size()==6&&photoList.get(19).isPhotoChecked()) {
                photoList.get(19).setOppositeCheck();
                boolean photoChecked = photoList.get(19).isPhotoChecked();
                if (photoChecked==true){
                    imageClicked.add(19);
                }else {
                    for (int i = 0; i < imageClicked.size(); i++) {
                        if (imageClicked.get(i)==19){
                            imageClicked.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this,"Please chooser six images!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setListener(CustomAdapter adapter, int index) {
        simplegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photoList.get(index).setOppositeCheck();
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void loadImage(ImageFetcher im, List<String> imageurl, int x) {
        String imgurl = imageurl.get(x - 1);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, "image" + x + ".jpg");
        if (!file.exists()) {
            storeImageInStorage(im, imgurl, file);
        }
        //imgbtn = findViewById(getResources().getIdentifier("img" + x, "id", this.getPackageName()));
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
        for (int i = 0; i < 20; i++) {
            Photo photo = new Photo(logos[i], false);
            photoList.add(photo);
        }
    }
}