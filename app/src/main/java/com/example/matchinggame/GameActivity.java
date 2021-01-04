package com.example.matchinggame;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {

    TextView timerText;
    Button stopStartButton;
    GridView gridView;
    ImageView curView = null;
    private int countPair = 0;
    int card[] ={R.drawable.card,R.drawable.card,R.drawable.card,R.drawable.card,
            R.drawable.card,R.drawable.card,R.drawable.card,R.drawable.card,
            R.drawable.card,R.drawable.card,R.drawable.card,R.drawable.card} ;
    int[] pos = {0,1,2,3,4,5,6,0,1,2,3,4,5,6};
    int currentPos = -1;

    Timer timer;
    TimerTask timerTask;
    Double time= 0.0;

    AnimatorSet set;
    ValueAnimator newtimer;

    boolean timerStarted =false;

    TextView mTextField;

    Integer[] answer = {0,0,1,1,2,2,3,3,4,4,5,5}; //to be shuffled on create
    ArrayList<Integer> chosenImagesArr = new ArrayList<>(); //from intent
    ArrayList<Bitmap> chosenImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //shuffle answer
        answer = shuffle(answer);

        //get intent get chosen images
        Intent intent = getIntent();
        chosenImagesArr = intent.getIntegerArrayListExtra("chosenimage");

        //get 6 images in to ArrayList<Bitmap> chosenImages
        for (int i=0; i<chosenImagesArr.size(); i++){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, "image"+(chosenImagesArr.get(i)+1)+".jpg");
            chosenImages.add( ( (BitmapDrawable)Drawable.createFromPath( file.toString() ) ).getBitmap());
        }

        final MediaPlayer correct = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer wrong = MediaPlayer.create(this, R.raw.wrong);
        final MediaPlayer count = MediaPlayer.create(this, R.raw.countdown);
        gridView = findViewById(R.id.GridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentPos <0){

                    currentPos = position;
                    curView = (ImageView)view;
                    ((ImageView)view).setImageResource
                            (card[pos[position]]);

                }

                else{

                    if(currentPos == position){
                        correct.start();  // if two images match, correct sound
                        ((ImageView)view).setImageResource
                                (R.drawable.card);
                        //Animation
                        set = (AnimatorSet) AnimatorInflater.loadAnimator(parent.getContext(), R.animator.flip);
                        set.setTarget((ImageView) view);
                        set.start();
                    }

                    else if (pos[currentPos] != pos[position]) {
                        wrong.start(); // if two images mismatch, wrong sound
                        curView.setImageResource(R.drawable.card);
                        Toast.makeText(getApplicationContext(),
                                //TODO with
                                "No Match",Toast.LENGTH_SHORT).show();
                        //Animation
                        set = (AnimatorSet) AnimatorInflater.loadAnimator(parent.getContext(), R.animator.flip);
                        set.setTarget((ImageView) view);
                        set.start();
                    }

                    else{

                        ((ImageView)view).setImageResource
                                (card[pos[position]]);

                        countPair++;

                        if(countPair == 6){

                            Toast.makeText(getApplicationContext(),
                                    "You have Won",Toast.LENGTH_SHORT).show();

                        }

                    }

                    currentPos = -1;

                }
            }
        });



        timerText =(TextView) findViewById(R.id.timerText);
        stopStartButton=(Button)findViewById(R.id.startStopButton);

        timer = new Timer();

        activateCountDown(count);


    }

    private void activateCountDown(MediaPlayer count) {
        //the countdown feature
        mTextField=findViewById(R.id.countdown);
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                count.start();
                if((millisUntilFinished)<1000){
                    mTextField.setText("START");
                }else{
                    mTextField.setText(""+(millisUntilFinished) / 1000);
                }
            }

            public void onFinish() {
                startStopTapped(findViewById(R.id.startStopButton));
                mTextField.setVisibility(View.GONE);
            }
        }.start();
    }

    //to shuffle the answer
    public Integer[] shuffle(Integer[] intArray){
        List<Integer> intList = Arrays.asList(intArray);
        Collections.shuffle(intList);
        return intList.toArray(intArray);
    }

    //to change default card to image behind the answer, work in progress
    public void flip(View v){
        ImageView imageView = (ImageView)v; //convert to imageview
        Bitmap current = ((BitmapDrawable)imageView.getDrawable()).getBitmap(); //get current picture bitmap
        Drawable defDrawable = getResources().getDrawable(R.drawable.default_image,null); //get default picture bitmap
        Bitmap def = ((BitmapDrawable)defDrawable).getBitmap();
        //compare - if default change to picture, else change to default
        if(current == def) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, "image9.jpg");
            imageView.setImageDrawable(Drawable.createFromPath((file.toString())));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_image,null));
        }
    }

    public void back(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    public void resetTapped(View view){
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("Reset Timer");
        resetAlert.setMessage("Are you sure you want to reset the timer?");
        resetAlert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(timerTask != null){
                    Intent intentfrom1= getIntent();
                    intentfrom1.putIntegerArrayListExtra("chosenimage",intentfrom1.getIntegerArrayListExtra("chosenimage"));
                    recreate();
                    //comment the sentence below (unsure if above work perfectly)
//                    timerTask.cancel();
//                    setButtonUI("START", R.color.green);
//                    time = 0.0;
//                    timerStarted = false;
//                    timerText.setText(formatTime(0,0,0));
                }

            }
        });
        resetAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //do nothing
            }
        });

        resetAlert.show();

    }

    public void startStopTapped(View view) {
        if(timerStarted ==false){
            timerStarted =true;
            setButtonUI("PAUSE", R.color.red);
            //GridView.setEnabled(true);
            gridView.setEnabled(true);
            startTimer();

        }
        else{
            timerStarted =false;
            setButtonUI("RESTART", R.color.green);
            //GridView.setEnabled(false);
            gridView.setEnabled(false);
            timerTask.cancel();
        }
    }

    private void setButtonUI(String start, int color) {
        stopStartButton.setText(start);
        stopStartButton.setTextColor(ContextCompat.getColor(this, color));
    }


    private void startTimer()
    {   //GridView.setVisibility(View.VISIBLE);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1000);
    }

    private String getTimerText()
    {
        int rounded =(int) Math.round(time);

        int seconds =((rounded%86400)%3600)%60;
        int minutes =((rounded%86400)%3600)/60;
        int hours =((rounded%86400)/3600);

        return formatTime(seconds,minutes,hours);
    }


    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours)+" : "+ String.format("%02d",minutes)+" : "+ String.format("%02d",seconds);
    }
}