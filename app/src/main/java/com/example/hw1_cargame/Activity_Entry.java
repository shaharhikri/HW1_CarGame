package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hw1_cargame.score_objects.ScoresDB;
import com.google.gson.Gson;

public class Activity_Entry extends AppCompatActivity {

    /*Entry fields*/
    private Button entry_play_btn;
    private Button entry_scores_btn;
    private Button entry_settings_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
//        Objects.requireNonNull(getSupportActionBar()).hide(); //Hide app header
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //ignore night mode
        findViews();
        fixBackground();
        initEntryPlayBtn();
        create_values_in_MSP_if_doesnt_exists();
        grantPermission();
    }

    private void grantPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
    }

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
        });
    }

    private void gotoActivity(Class activityClass){
        Intent myIntent = new Intent(this, activityClass);
        startActivity(myIntent);
    }

    private void create_values_in_MSP_if_doesnt_exists(){
        String speed = MSP.getMe().getString(Activity_Game.SPEED_KEY,"none");
        String controlsType = MSP.getMe().getString(Activity_Game.CONTROL_TYPE_KEY,"none");
        String scoresDB_json = MSP.getMe().getString(ScoresDB.SCORES_DB_KEY,"none");
        if(speed.compareTo(Activity_Game.LOW_VAL)!=0 && speed.compareTo(Activity_Game.HIGH_VAL)!=0)
            MSP.getMe().putString(Activity_Game.SPEED_KEY, Activity_Game.LOW_VAL);
        if(controlsType.compareTo(Activity_Game.BUTTONS_VAL)!=0 && controlsType.compareTo(Activity_Game.ACCELOMETER_VAL)!=0)
            MSP.getMe().putString(Activity_Game.CONTROL_TYPE_KEY, Activity_Game.BUTTONS_VAL);
        if(scoresDB_json.compareTo("none")==0) {
            //CREATE SCOREDB
            ScoresDB scoresDB = new ScoresDB();
            //CONVERT TO JSON
            String json = new Gson().toJson(scoresDB);
            //SAVE IN MSP AS STRING
            MSP.getMe().putString(ScoresDB.SCORES_DB_KEY, json);
        }
    }

}