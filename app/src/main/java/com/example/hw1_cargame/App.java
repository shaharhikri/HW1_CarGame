package com.example.hw1_cargame;

import android.app.Application;

import com.example.hw1_cargame.BackgroundMusic.BackgroundSoundService;
import com.example.hw1_cargame.msp_objects.MSP;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MSP.initHelper(this);

        BackgroundSoundService.initHelper(this);
    }
}
