package com.example.hw1_cargame.BackgroundMusic;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hw1_cargame.BackgroundMusic.BackgroundSoundService;

public abstract class Activity_BackgroundMusic extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BackgroundSoundService.getMe().play();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundSoundService.getMe().play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundSoundService.getMe().pause();
    }

    @Override
    public void finish() {
        BackgroundSoundService.getMe().pause();
        super.finish();
    }
}
