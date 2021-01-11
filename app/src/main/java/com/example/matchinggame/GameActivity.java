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
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {

    private TextView timerText;
    private TextView numberOfMatchesTextView;
    private Button stopStartButton;
    private Button resetButton;
    private GridView gridView;
    private int countPair = 0;
    private Timer timerback = new Timer();
    private Timer timer;
    private TimerTask timerTask;
    private Double time= 0.0;
    private AnimatorSet set;
    boolean timerStarted =false;
    private TextView mTextField;
    private Integer[] answer = {0,0,1,1,2,2,3,3,4,4,5,5}; //to be shuffled on create
    private ArrayList<Integer> chosenImagesArr = new ArrayList<>(); //from intent
    private ArrayList<Drawable> chosenImagesDrawable = new ArrayList<>();
    private ArrayList<Drawable> answerDrawable = new ArrayList<>();
    private ArrayList<Integer> chosenPosition = new ArrayList<>();
    private Handler mainHandler;
    private Runnable myRunnable;

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
            chosenImagesDrawable.add(Drawable.createFromPath( file.toString() ));
        }

        //prepare answerDrawable
        for (int i=0; i<answer.length ; i++){
            answerDrawable.add(chosenImagesDrawable.get(answer[i]));
        }

        final MediaPlayer correct = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer wrong = MediaPlayer.create(this, R.raw.wrong);
        final MediaPlayer count = MediaPlayer.create(this, R.raw.countdown);
        gridView = findViewById(R.id.GridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);

        timerText =(TextView) findViewById(R.id.timerText);
        stopStartButton=(Button)findViewById(R.id.startStopButton);
        resetButton=(Button)findViewById(R.id.restTapped);
        timer = new Timer();

        activateCountDown(count);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //show image and set animation
                ((ImageView)view).setBackground(answerDrawable.get(position));
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
                    ((ImageView)view).setBackground(answerDrawable.get(position));

                }

                //if clicked second item
                if (chosenPosition.size() == 2){
                    //compare if match
                    if (answer[chosenPosition.get(0)] == answer[chosenPosition.get(1)]){
                        correct.start(); // correct sound

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
                        wrong.start(); // wrong sound
                        autoClose(parent, mainHandler);
                    }
                }

                //when win
                if(countPair == 6){
                    winningCondition();
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        timerTask.cancel();
        setButtonUI("RESUME", R.color.green);
    }
    @Override
    protected  void onRestart() {
        super.onRestart();
        startTimer();
        setButtonUI("PAUSE", R.color.red);
    }

    private void winningCondition() {
        Toast.makeText(getApplicationContext(),"You have Won!",Toast.LENGTH_SHORT).show();
        if(timerStarted == true){
            stopStartButton.setEnabled(false);
            resetButton.setEnabled(false);
            timerTask.cancel();
        }
        promptUser();
    }

    private void autoClose(AdapterView<?> parent, Handler mainHandler) {
        mainHandler.postDelayed(myRunnable,2000);
    }

    private void activateCountDown(MediaPlayer count) {
        //the countdown feature
        mTextField=findViewById(R.id.countdown);
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                stopStartButton.setVisibility(View.INVISIBLE);
                count.start();
               gridView.setEnabled(false);
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

    public void promptUser(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptView=li.inflate(R.layout.prompt,null);
        AlertDialog.Builder dlg=new AlertDialog.Builder(this);
        dlg.setView(promptView);
        EditText input= promptView.findViewById(R.id.nameinput);
        dlg.setCancelable(false)
                .setPositiveButton("submit", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dlg,int which){
                        String name=input.getText().toString();
                        String time=timerText.getText().toString();
                        sendTheScore(name,time);
                        TimerTask task = new TimerTask() {;
                            @Override
                            public void run() {
                                finish();
                            }
                        };
                        timerback.schedule(task, 1000 * 3);

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        });
        dlg.create().show();

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

    public void sendTheScore(String name,String time){
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        int min=Integer.valueOf(time.split(":")[1].trim());
        int sec=Integer.valueOf(time.split(":")[2].trim());
        String url="http://10.0.2.2:8080/api/leaderboard/player?name="+name+"&min="+min+"&sec="+sec;
        System.out.println(url);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        System.out.println("Response is: "+ response.toString());
                        try {
                            JSONObject result= new JSONObject(response.toString());
                            Toast.makeText(GameActivity.this,"Saved successfully",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error.getMessage());
                Toast.makeText(GameActivity.this,"Saving is failed!",Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void closeTwoCards(AdapterView<?> parent){
        //flip back first item
        ImageView firstItem = (ImageView) parent.getChildAt(chosenPosition.get(0));
        //firstItem.setImageDrawable(getDrawable(R.drawable.card));
        firstItem.setBackgroundResource(R.drawable.card);

        //flip back 2nd item
        ImageView secondItem = (ImageView) parent.getChildAt(chosenPosition.get(1));
        //secondItem.setImageDrawable(getDrawable(R.drawable.card));
        secondItem.setBackgroundResource(R.drawable.card);
    }
}