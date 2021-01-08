package com.example.matchinggame;

import java.io.File;
import java.io.FileOutputStream;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
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
    private Button mscore;
    private ProgressBar bar;
    private TextView msg;
    private TextView guide;
    int status;
    private String actiondownload="DOWNLOAD";
    private final int imagetotal = 20;
    private final int imageselected = 6;
    int[] logos = new int[imagetotal];
    private GridView simplegrid;
    private List<Integer> imageClicked = new ArrayList<Integer>();
    private List<Photo> photoList;
    private CustomAdapter adapter;
    Thread thr;
    EditText enterUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        enterUrl = (EditText) findViewById(R.id.enteredUrl);
        getPhotoData();
        simplegrid = (GridView) findViewById(R.id.GridView);
        adapter = new CustomAdapter(getApplicationContext(), photoList);
        simplegrid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        fetchbtn = findViewById(R.id.fetchbtn);
        fetchbtn.setOnClickListener(this);
        mscore = findViewById(R.id.score);
        mscore.setOnClickListener(this);
        guide = findViewById(R.id.guide);
        startbtn = findViewById(R.id.start);
        startbtn.setOnClickListener((view -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putIntegerArrayListExtra("chosenimage", (ArrayList<Integer>) imageClicked);
            startActivity(intent);

        }));
        bar = findViewById(R.id.progress);
        msg = findViewById(R.id.progressmsg);

        IntentFilter filter=new IntentFilter();
        filter.addAction(actiondownload);
        filter.addAction("completed");
        registerReceiver(br,filter);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    private BroadcastReceiver br= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            int x=intent.getIntExtra("index",0);
            String file=intent.getStringExtra("stringfile");
            if(action.equals("completed")){
                loadImage(x, file);
                if (status == imagetotal) {
                    bar.setVisibility(View.GONE);
                    msg.setVisibility(View.GONE);
                    if (imageClicked.size() == imageselected) {
                        startbtn.setVisibility(View.VISIBLE);
                        stopService(new Intent(MainActivity.this,DownloadService.class));
                    } else {
                        guide.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    };


    public void setAdapterState(int index) {
        if (imageClicked.size() < imageselected) {
            photoList.get(index).setOppositeCheck();
            boolean photoChecked = photoList.get(index).isPhotoChecked();
            if (photoChecked == true) {
                imageClicked.add(index);
                if (imageClicked.size() == imageselected && bar.getVisibility() != View.VISIBLE) {
                    startbtn.setVisibility(View.VISIBLE);
                    guide.setVisibility(View.GONE);
                }
            } else {
                for (int i = 0; i < imageClicked.size(); i++) {
                    if (imageClicked.get(i) == index) {
                        imageClicked.remove(i);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        } else if (imageClicked.size() == imageselected && photoList.get(index).isPhotoChecked()) {
            photoList.get(index).setOppositeCheck();
            boolean photoChecked = photoList.get(index).isPhotoChecked();
            if (photoChecked == true) {
                imageClicked.add(index);
            } else {
                for (int i = 0; i < imageClicked.size(); i++) {
                    if (imageClicked.get(i) == index) {
                        imageClicked.remove(i);
                        if (bar.getVisibility() != View.VISIBLE) {
                            startbtn.setVisibility(View.GONE);
                            guide.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Please choose six images!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

        String url = enterUrl.getText().toString();

        if (view == fetchbtn) {
            fetchImages(url);
        } else if(view==mscore){
            Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
            startActivity(intent);
        } else {
            //assign every button click function to update the state of app for having only 6 images
            for (int x = 0; x < imagetotal; x++) {
                if (view == simplegrid.getChildAt(x).findViewById(R.id.imageView)) {
                    setAdapterState(x);
                }
            }
        }
    }

    private void fetchImages(String url) {
        if (thr != null) {
            System.out.println("interupt here");
            thr.interrupt();
        }

        closeKeyboard();
        if (url.equals("")) {
            Toast.makeText(this, "You did not enter a url", Toast.LENGTH_SHORT).show();
            return;
        }

        guide.setVisibility(View.GONE);
        startbtn.setVisibility(View.GONE);
        ImageFetcher im = new ImageFetcher(url);
        List<String> imageurl = im.extractImage();
        if (imageurl == null) {
            Toast.makeText(this, "Invalid url", Toast.LENGTH_SHORT).show();
            return;
        } else if (imageurl.size() < 20) {
            Toast.makeText(this, "Images not sufficient", Toast.LENGTH_SHORT).show();
            return;
        }

        bar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        status = 0;
        bar.setProgress(status * 10);
        for(int x=1;x<=imagetotal;x++){
            Intent intent= new Intent(this,DownloadService.class);
            intent.setAction(actiondownload);
            intent.putStringArrayListExtra("url",(ArrayList<String>)imageurl);
            intent.putExtra("index",x);
            startService(intent);
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadImage( int x,String file) {
        View viewitem = simplegrid.getChildAt(x - 1);
        imgbtn = (ImageView) viewitem.findViewById(R.id.imageView);
        imgbtn.setOnClickListener(this);
        status=x;

        bar.setProgress(10 * x);
        String mess = "Downloading " + x + " of " + imagetotal + " images...";
        msg.setText(mess);
        imgbtn.setBackground(Drawable.createFromPath(file));

    }


    private void getPhotoData() {

        photoList = new ArrayList<Photo>();
        for (int i = 0; i < imagetotal; i++) {
            Photo photo = new Photo(logos[i], false);
            photoList.add(photo);
        }
    }
}