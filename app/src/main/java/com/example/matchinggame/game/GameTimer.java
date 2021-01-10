package com.example.matchinggame.game;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class GameTimer extends androidx.appcompat.widget.AppCompatTextView implements Runnable {

    private double time = 0.0;
    private final Runnable countUpTask = () -> {
        time++;
        setText(getTimerText());
    };
    public TimerTask tsk;
    private Timer timer = new Timer();




    public GameTimer(@NonNull Context context) {
        super(context);
    }

    public GameTimer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameTimer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameTimer(Activity parent){
        super(parent);
    }

    public void init(){
        time = 0.0;
    }

    public String getTimerText()
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

    public void start(){
        if(tsk==null || !tsk.cancel()){
            tsk = new TimerTask() {
                @Override
                public void run() {
                   countUpTask.run();
                }
            };
            timer.scheduleAtFixedRate(tsk, 0,1000);
        }
    }

    public boolean stop(){
        if(tsk !=null){
            return tsk.cancel();
        } else
            return false;
    }

    public void reset(){
        tsk.cancel();
        time = 0.0;
    }

    @Override
    public void run() {
        this.start();
    }
}
