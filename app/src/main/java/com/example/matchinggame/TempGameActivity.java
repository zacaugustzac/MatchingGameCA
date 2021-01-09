package com.example.matchinggame;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.SoundPool;
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


public class TempGameActivity extends AppCompatActivity {

    TextView timerText;
    TextView numberOfMatchesTextView;
    Button stopStartButton;
    Button resetButton;
    GridView gridView;
    private int countPair = 0;
    Timer timerback = new Timer();
    Timer timer;
    TimerTask timerTask;
    Double time= 0.0;
    AnimatorSet set;
    boolean timerStarted =false;
    TextView mTextField;
    Integer[] answer = {0,0,1,1,2,2,3,3,4,4,5,5}; //to be shuffled on create
    ArrayList<Integer> chosenImagesArr = new ArrayList<>(); //from intent
    ArrayList<Drawable> chosenImagesDrawable = new ArrayList<>();
    ArrayList<Drawable> answerDrawable = new ArrayList<>();
    ArrayList<Integer> chosenPosition = new ArrayList<>();
    Handler mainHandler;
    Runnable myRunnable;
    SoundPool soundPool;
    int correct, wrong, countdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        answer = shuffle(answer);        //shuffle answer

        //prepare mainHandler and runnable
        mainHandler = new Handler(Looper.getMainLooper()); //set mainHandler

        //get intent get chosen images arr from MainActivity
        Intent intent = getIntent();
        chosenImagesArr = intent.getIntegerArrayListExtra("chosenimage");

        //prepare chosenImageDrawable (get 6 images in to ArrayList<Bitmap> chosenImagesDrawable)
        for (int i=0; i<chosenImagesArr.size(); i++){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, "image"+(chosenImagesArr.get(i)+1)+".jpg");
//            chosenImages.add( ( (BitmapDrawable)Drawable.createFromPath( file.toString() ) ).getBitmap());
            chosenImagesDrawable.add(Drawable.createFromPath( file.toString() ));
        }

        //prepare answerDrawable
        for (int i=0; i<answer.length ; i++){
            answerDrawable.add(chosenImagesDrawable.get(answer[i]));
        }

        AudioAttributes audioAttributes = new AudioAttributes
        .Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build();
        soundPool = new SoundPool
                .Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        correct
                = soundPool.load(this, R.raw.correct,1);
        wrong
                = soundPool.load(this, R.raw.wrong, 1);
        countdown
                = soundPool.load(this, R.raw.countdown, 1);

        gridView = findViewById(R.id.GridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);

        timerText =(TextView) findViewById(R.id.timerText);
        stopStartButton=(Button)findViewById(R.id.startStopButton);
        resetButton=(Button)findViewById(R.id.restTapped);
        timer = new Timer();

        activateCountDown(null);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //show image and set animation
                ((ImageView)view).setImageDrawable(answerDrawable.get(position));
                set = (AnimatorSet) AnimatorInflater.loadAnimator(parent.getContext(), R.animator.flip);
                set.setTarget((ImageView) view);
                set.start();

                //record clicked position
                chosenPosition.add(position);

                //clear handler
                mainHandler.removeCallbacksAndMessages(null);

                //if second item selected is same as first item
                if (chosenPosition.size() == 2){
                    if (chosenPosition.get(0) == chosenPosition.get(1)){
                        Toast.makeText(getApplicationContext(),"Please select different card",Toast.LENGTH_SHORT).show();
                        chosenPosition.remove(chosenPosition.size()-1);
                    }
                }

                //if clicked 3rd item
                if (chosenPosition.size() == 3) {
                    closeTwoCards(parent);

                    chosenPosition.clear();
                    chosenPosition.add(position);

                    //to cover case where 3rd item is one of the first 2 items
                    ((ImageView)view).setImageDrawable(answerDrawable.get(position));
                }

                //if clicked second item
                if (chosenPosition.size() == 2){
                    //compare if match
                    if (answer[chosenPosition.get(0)] == answer[chosenPosition.get(1)]){
                        soundPool.play(correct, 1, 1, 0, 0, 1); // correct sound
                        soundPool.pause(correct);

                        //make first item not clickable
                        ImageView firstItem = (ImageView) parent.getChildAt(chosenPosition.get(0));
                        firstItem.setOnClickListener(null);

                        //make second item not clickable
                        view.setOnClickListener(null);

                        //update number of matched pairs textview
                        countPair++;
                        String noOfMatches = countPair+"/6 Matches";
                        numberOfMatchesTextView = findViewById(R.id.numberOfMatchesTextView);
                        numberOfMatchesTextView.setText(noOfMatches);

                        //clear selections
                        chosenPosition.clear();
                    }
                    else{ //if mismatch
                        myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                    closeTwoCards(parent);
                            }
                        };
                        Toast.makeText(getApplicationContext(),"No Match",Toast.LENGTH_SHORT).show();
                        soundPool.play(wrong,1,1,0,0,1); // wrong sound
                        soundPool.pause(wrong);
                        autoClose(parent, mainHandler);
                    }
                }

                //when win
                if(countPair == 6){
                    Toast.makeText(getApplicationContext(),"You have Won!",Toast.LENGTH_SHORT).show();
                    if(timerStarted == true){
                        stopStartButton.setEnabled(false);
                        resetButton.setEnabled(false);
                        timerTask.cancel();
                    }
                    TimerTask task = new TimerTask() {;
                        @Override
                        public void run() {
                            finish();
                        }
                    };
                    timerback.schedule(task, 1000 * 5);
                }
            }
        });
    }

    private void autoClose(AdapterView<?> parent, Handler mainHandler) {
//        mainHandler.removeCallbacksAndMessages(null);
        mainHandler.postDelayed(myRunnable,3000);
    }

    private void activateCountDown(SoundPool soundPool) {
        //the countdown feature
        mTextField=findViewById(R.id.countdown);
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                stopStartButton.setVisibility(View.INVISIBLE);
                soundPool.play(countdown, 1,1,0,0,1);
               gridView.setEnabled(false);
                if((millisUntilFinished)<1000){
                    mTextField.setText("START");
                    soundPool.pause(countdown);
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

    public void back(View view){
        Intent intent = new Intent(this, TempMainActivity.class);
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
            gridView.setEnabled(true);
            startTimer();

        }
        else{
            timerStarted =false;
            setButtonUI("RESUME", R.color.green);
            gridView.setEnabled(false);
            timerTask.cancel();
        }
    }

    private void setButtonUI(String start, int color) {
        stopStartButton.setText(start);
        stopStartButton.setTextColor(ContextCompat.getColor(this, color));
    }

    private void startTimer() {
        stopStartButton.setVisibility(View.VISIBLE);
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

    public void closeTwoCards(AdapterView<?> parent){
        //flip back first item
        ImageView firstItem = (ImageView) parent.getChildAt(chosenPosition.get(0));
        firstItem.setImageDrawable(getDrawable(R.drawable.card));

        //flip back 2nd item
        ImageView secondItem = (ImageView) parent.getChildAt(chosenPosition.get(1));
        secondItem.setImageDrawable(getDrawable(R.drawable.card));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        getDelegate().onDestroy();
    }
}