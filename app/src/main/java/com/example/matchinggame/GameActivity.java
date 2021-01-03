package com.example.matchinggame;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {

    TextView timerText;
    Button stopStartButton;
    GridView GridView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final MediaPlayer correct = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer wrong = MediaPlayer.create(this, R.raw.wrong);
        final MediaPlayer count = MediaPlayer.create(this, R.raw.countdown);
        GridView gridView = (GridView)findViewById(R.id.GridView);
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


    public void back(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    public void restTapped(View view){
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("Reset Timer");
        resetAlert.setMessage("Are you sure you want to reset the timer?");
        resetAlert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(timerTask != null){
                    timerTask.cancel();
                    setButtonUI("START", R.color.green);
                    time = 0.0;
                    timerStarted = false;
                    timerText.setText(formatTime(0,0,0));
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
            startTimer();

        }
        else{
            timerStarted =false;
            setButtonUI("RESTART", R.color.green);
            timerTask.cancel();
        }
    }

    private void setButtonUI(String start, int color) {
        stopStartButton.setText(start);
        stopStartButton.setTextColor(ContextCompat.getColor(this, color));
    }


    private void startTimer()
    {
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