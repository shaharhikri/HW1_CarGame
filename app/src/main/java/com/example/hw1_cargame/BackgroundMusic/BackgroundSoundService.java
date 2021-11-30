package com.example.hw1_cargame.BackgroundMusic;


import android.content.Context;
import android.media.MediaPlayer;

import com.example.hw1_cargame.R;

public class BackgroundSoundService {
    public static BackgroundSoundService me;

    private MediaPlayer player;
    private Context myContext;

    private BackgroundSoundService(Context context){
        myContext = context;
        player = MediaPlayer.create(context, R.raw.background_music);
        player.setLooping(true);
        player.setVolume(0.1f,0.1f);
    }

    public static BackgroundSoundService initHelper(Context context){
        if(me==null)
            me = new BackgroundSoundService(context);
        return me;
    }

    public static BackgroundSoundService getMe(){
        return me;
    }

    public void play(){
        player.start();
    }

    public void pause(){
        player.pause();
    }
}
