package com.example.matchinggame;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
    private ProgressBar bar;
    private TextView msg;
    private TextView guide;
    private Handler handler = new Handler();
    int status;
    private final int imagetotal = 20;
    private final int imageselected = 6;
    int[] logos = new int[imagetotal];
    private GridView simplegrid;
    private List<Integer> imageClicked = new ArrayList<Integer>();
    private List<Photo> photoList;
    private CustomAdapter adapter;
    EditText enterUrl;
    private ImageView check;
    private boolean status_thread=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        enterUrl = (EditText)findViewById(R.id.enteredUrl);
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
            Toast.makeText(this,"Please choose six images!",Toast.LENGTH_SHORT).show();
        }
    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        String url = "https://stocksnap.io/search/cars";
        @Override
        protected String doInBackground(String... strings) {
            closeKeyboard();
            if (url.equals("")) {
                Toast.makeText(MainActivity.this, "You did not enter a url", Toast.LENGTH_SHORT).show();
            }
            ImageFetcher im = new ImageFetcher(url);
            try {

                List<String> imageurl = im.extractImage();
//
                while (status < imagetotal) {
                    status++;

                    handler.post(() -> {
                        publishProgress(status * 10);
                        loadImage(im, imageurl, status);
                        if (status == imagetotal) {
                            bar.setVisibility(View.GONE);
                            msg.setVisibility(View.GONE);
                            guide = findViewById(R.id.guide);
                            guide.setVisibility(View.VISIBLE);

                        }

                    });

                    Thread.sleep(500);
                }
            } catch (MalformedURLException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            msg.setVisibility(View.VISIBLE);

            msg.setText("Loading");
            // 执行前显示提示
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {

            bar.setProgress(progresses[0]);
            String mess = "Downloading " + (status) + " of " + imagetotal + " images...";
            msg.setText(mess);


        }

        @Override
        protected void onPostExecute(String result) {
            msg.setText("Loading Completed");
            status_thread=true;

        }
        @Override
        protected void onCancelled() {

//            msg.setText("已取消");
//            bar.setProgress(0);

        }
    }


    @Override
    public void onClick(View view) {


        if (view==fetchbtn){

            MyTask myTask=new MyTask();
            myTask.execute();

        }
        else {
            //assign every button click function to update the state of app for having only 6 images

            if (status_thread==true){

                for (int x = 0; x < imagetotal; x++) {
                    if (view == simplegrid.getChildAt(x).findViewById(R.id.imageView)) {
                        setAdapterState(x);
                    }
                }
            }


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

    private void loadImage(ImageFetcher im, List<String> imageurl, int x) {
        String imgurl = imageurl.get(x - 1);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, "image" + x + ".jpg");
        storeImageInStorage(im, imgurl, file);
        View viewitem = simplegrid.getChildAt(x - 1);
        imgbtn = (ImageView) viewitem.findViewById(R.id.imageView);
        imgbtn.setOnClickListener(this);

        bar.setProgress(10 * x);
        String mess = "Downloading " + x + " of " + imagetotal + " images...";
        msg.setText(mess);
        imgbtn.setBackground(Drawable.createFromPath(file.toString()));

    }


    private void getPhotoData() {

        photoList = new ArrayList<Photo>();
        for (int i = 0; i < imagetotal; i++) {
            Photo photo = new Photo(logos[i], false);
            photoList.add(photo);
        }
    }
}