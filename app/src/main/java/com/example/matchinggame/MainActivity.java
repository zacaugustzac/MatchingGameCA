package com.example.matchinggame;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
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

import static java.util.stream.Collectors.toList;

public class MainActivity extends AppCompatActivity {
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
    private List<PhotoId> imageClicked = new ArrayList<PhotoId>();
    private List<Photo> photoList;
    private CustomAdapter adapter;
    //Thread thr;
    EditText enterUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        enterUrl = (EditText)findViewById(R.id.enteredUrl);
        photoList = Arrays.asList(getPhotoData());
        simplegrid = (GridView) findViewById(R.id.GridView);
        adapter = new CustomAdapter(getApplicationContext(), photoList);
        simplegrid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        fetchbtn = findViewById(R.id.fetchbtn);
        fetchbtn.setOnClickListener(notUsed -> fetchImages());
        mscore = findViewById(R.id.score);
        mscore.setOnClickListener(notUsed -> {
            Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
            startActivity(intent);
        });
        guide = findViewById(R.id.guide);
        startbtn = findViewById(R.id.start);
        startbtn.setOnClickListener((view->{
            Intent intent= new Intent(this,GameActivity.class);
            List<Integer> transferValues = imageClicked.stream().map(pid -> pid.value).collect(toList());
            intent.putIntegerArrayListExtra("chosenimage", new ArrayList<>(transferValues));
            startActivity(intent);

        }));
        bar = findViewById(R.id.progress);
        msg = findViewById(R.id.progressmsg);
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

    public void setAdapterState(View view){
        Photo myPhoto = ((PhotoFrame) view).getImage();
        myPhoto.setOppositeCheck();
        int numSelected = imageClicked.size();
        if(numSelected== imageselected){
            startbtn.setVisibility(View.VISIBLE);
            guide.setVisibility(View.GONE);
        } else if (numSelected> imageselected){
            myPhoto.setOppositeCheck();
        } else {
            Toast.makeText(this,"Please choose six images!",Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchImages(){
        String url = enterUrl.getText().toString();
        fetchImages(url);
    }

    private void fetchImages(String url) {

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

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadImage(int x,String file) {
        int id = x-1;
        Log.i("MainActivity.loadImage", "Loading image " + id);
        View viewitem = simplegrid.getChildAt(id);
        PhotoFrame imgbtn = (PhotoFrame) viewitem.findViewById(R.id.imageView);
        Photo image = photoList.get(id);
        imgbtn.setImage(image);
        //imgbtn.setOnClickListener(this);
        imgbtn.setOnClickListener(self -> {
            Log.d("MainActivity.Image OnClick #" + id, "Setting Adapter State");
            setAdapterState(self);
        });
        bar.setProgress(10 * x);
        String mess = "Downloading " + x + " of " + imagetotal + " images...";
        msg.setText(mess);
        imgbtn.setBackground(Drawable.createFromPath(file));
    }


    private Photo[] getPhotoData() {

        Photo[] photos = new Photo[imagetotal];
        photoList = new ArrayList<Photo>();
        for (int i = 0; i < imagetotal; i++) {
            PhotoId photoId = new PhotoId(i);
            Photo p = new Photo(photoId.value, false);
            p.setOnToggleListener(selecting -> {
                if(selecting) {
                    imageClicked.add(photoId);
                    Log.i("MainActivity Click", imageClicked.toString());
                    Log.d("MainActivity Click", "Added #" + photoId);
                } else {
                    imageClicked.remove(photoId);
                    Log.i("MainActivity Click", imageClicked.toString());
                    Log.d("MainActivity Click", "Removed #" + photoId);
                }
            });
            photos[i] = p;
        }
        return photos;
    }
}