package com.example.matchinggame.game;

import android.os.CountDownTimer;

import java.util.Timer;
import java.util.function.Consumer;

public class PreGameCountdown extends CountDownTimer {

    private Consumer<Long> onTickListener;
    private Runnable onFinishListener;

    private double time;

    public PreGameCountdown(){
        super(4000, 1000);
        time = 0.0;
        this.onFinishListener = () -> {};
        this.onTickListener = notUsed -> {};
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.onTickListener.accept(millisUntilFinished);
    }

    public PreGameCountdown onTick(Consumer<Long> lis){
        this.onTickListener = lis;
        return this;
    }

    @Override
    public void onFinish() {
        this.onFinishListener.run();
    }

    public PreGameCountdown onFinish(Runnable r){
        this.onFinishListener = r;
        return this;
    }
}
