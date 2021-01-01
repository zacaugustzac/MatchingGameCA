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
    private Handler handler= new Handler();
    int status=0;
    private final int imagetotal=20;
    int[]logos=new int[imagetotal];
    private GridView simplegrid;
    private List<Integer> imageClicked= new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for(int x=0;x<20;x++){
            logos[x]=R.drawable.card;
        }
        simplegrid=(GridView)findViewById(R.id.GridView);
        CustomAdapter adapter=new CustomAdapter(getApplicationContext(),logos);
        simplegrid.setAdapter(adapter);
        simplegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"image "+position+" is clicked!",Toast.LENGTH_SHORT).show();
                ImageView img=view.findViewById(R.id.imageView);
                //implemented when the image is clicked, should have some border and framed with the array

            }
        });
        fetchbtn = findViewById(R.id.fetchbtn);
        fetchbtn.setOnClickListener(this);
        startbtn=findViewById(R.id.start);
        startbtn.setOnClickListener(this);
        bar = findViewById(R.id.progress);
        msg = findViewById(R.id.progressmsg);
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
                bar.setVisibility(View.VISIBLE);
                msg.setVisibility(View.VISIBLE);

                new Thread(() -> {
                    while (status<imagetotal){
                        status++;
                        handler.post(()-> {
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
                        });
                        try {Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if(view==simplegrid.getChildAt(0).findViewById(R.id.imageView)){
            //imageClicked.add(0);
            Toast.makeText(getApplicationContext(),"image 0 is clicked!",Toast.LENGTH_SHORT).show();
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
        //imgbtn = findViewById(getResources().getIdentifier("img" + x, "id", this.getPackageName()));
        View viewitem = simplegrid.getChildAt(x-1);
        imgbtn=(ImageView)viewitem.findViewById(R.id.imageView);
        imgbtn.setOnClickListener(this);

        bar.setProgress(10*x);
        String mess="Downloading " + x + " of "+imagetotal+" images...";
        msg.setText(mess);
        imgbtn.setBackground(Drawable.createFromPath(file.toString()));
        //imgbtn.setImageResource(Drawable.createFromPath(file.toString()));

    }
}