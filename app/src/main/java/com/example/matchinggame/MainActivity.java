package com.example.matchinggame;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
    private Button startbtn;
    private ProgressBar bar;
    private TextView msg;
    private TextView guide;
    int status;
    private final int imagetotal = 20;
    private final int selectableLimit = 6;
    private GridView simplegrid;
    private List<PhotoId> imageClicked = new ArrayList<PhotoId>();
    private List<Photo> photoList;
    private CustomAdapter adapter;
    Thread thr;
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

    public void setAdapterState(View view){
        Photo myPhoto = ((PhotoFrame) view).getImage();
        myPhoto.setOppositeCheck();
        int numSelected = imageClicked.size();
        if(numSelected==selectableLimit){
            startbtn.setVisibility(View.VISIBLE);
            guide.setVisibility(View.GONE);
        } else if (numSelected>selectableLimit){
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
        if(thr!=null){
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
        if(imageurl==null){
            Toast.makeText(this, "Invalid url", Toast.LENGTH_SHORT).show();
            return;
        }else if(imageurl.size()<20){
            Toast.makeText(this, "Images not sufficient", Toast.LENGTH_SHORT).show();
            return;
        }

        bar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        status = 0;
        bar.setProgress(status * 10);

        thr= new Thread (()->{
            while (status < imagetotal) {
                status++;
                runOnUiThread(()-> {
                    loadImage(im, imageurl, status);
                    if (status == imagetotal) {
                        bar.setVisibility(View.GONE);
                        msg.setVisibility(View.GONE);
                        if(imageClicked.size()== selectableLimit){
                            startbtn.setVisibility(View.VISIBLE);
                        }else{
                            guide.setVisibility(View.VISIBLE);
                        }
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    status=imagetotal;
                    //e.printStackTrace();
                }
            }
        });
        thr.start();

    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String manualDecode(String url){
        if(url.contains("&#x27;")){
            url=url.replaceAll("&#x27;","'");
        }
        return url;
    }

    private void loadImage(ImageFetcher im, List<String> imageurl, int x) {
        int id = x-1;
        Log.i("MainActivity.loadImage", "Loading image " + id);
        String imgurl = imageurl.get(id);
        imgurl=manualDecode(imgurl);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, "image" + x + ".jpg");
        storeImageInStorage(im, imgurl, file);
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
        imgbtn.setBackground(Drawable.createFromPath(file.toString()));

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