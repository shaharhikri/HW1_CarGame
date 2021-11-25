package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hw1_cargame.msp_objects.MSP_Manager;

public class Activity_Entry extends AppCompatActivity {

    /*Entry fields*/
    private Button entry_play_btn;
    private Button entry_scores_btn;
    private Button entry_settings_button;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        findViews();
        fixBackground();
        grantLocationPermission();
        bundle = getIntent().getBundleExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE);
        initEntryPlayBtn();
    }

    private void grantLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
    }


    //fix background bitmap - prevents errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.entry_background).into((ImageView)findViewById(R.id.entry_back));
    }

    private void findViews() {
        entry_play_btn = findViewById(R.id.entry_play_btn);
        entry_scores_btn = findViewById(R.id.entry_scores_btn);
        entry_settings_button = findViewById(R.id.entry_settings_btn);
    }

    private void initEntryPlayBtn(){
        entry_play_btn.setOnClickListener(v -> {
            gotoActivity(Activity_Game.class);
            finish();
        });
        entry_scores_btn.setOnClickListener(v -> {
            gotoActivity(Activity_Scores.class);
        });
        entry_settings_button.setOnClickListener(v -> {
            gotoActivity(Activity_Settings.class);
            finish();
        });
    }

    private void gotoActivity(Class activityClass){
        Intent myIntent = new Intent(this, activityClass);
        // settings to bundle
        myIntent.putExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE, bundle);
        startActivity(myIntent);
    }

}