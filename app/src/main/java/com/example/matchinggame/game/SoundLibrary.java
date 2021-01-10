package com.example.matchinggame.game;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.matchinggame.R;

public class SoundLibrary {

    protected final AudioAttributes audioAttributes = new AudioAttributes
            .Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();

    public final SoundPool soundPool = new SoundPool
            .Builder()
            .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();


    public final int correct;
    public final int wrong;
    public final int countdown;

    public SoundLibrary(Context cx){
        correct = soundPool.load(cx, R.raw.correct,1);
        wrong  = soundPool.load(cx, R.raw.wrong, 1);
        countdown = soundPool.load(cx, R.raw.countdown, 1);
    }



    public void play(int sound){
        soundPool.play(sound, 1,1,0,0,1);
        soundPool.pause(sound);
    }

    public void release(){
        soundPool.release();
    }
}
