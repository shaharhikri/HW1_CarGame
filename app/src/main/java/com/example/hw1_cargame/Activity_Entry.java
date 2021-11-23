package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class Activity_Entry extends AppCompatActivity {

    /*Entry fields*/
    private Button entry_play_btn;
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
        create_controlType_And_Speed_In_MSP_if_Doesnt_Exists();
    }

    private void findViews() {
        entry_play_btn = findViewById(R.id.entry_play_btn);
        entry_settings_button = findViewById(R.id.entry_settings_btn);
    }

    private void initEntryPlayBtn(){
        entry_play_btn.setOnClickListener(v -> {
            startGame();
        });
        entry_settings_button.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, Activity_Settings.class);
            startActivity(myIntent);
        });
    }

    private void startGame() {
        Intent myIntent = new Intent(this, Activity_Game.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt(Activity_Game.CONTROL_TYPE_KEY, control_type.ordinal());
//        myIntent.putExtra("Bundle", bundle);
        startActivity(myIntent);
        finish();
    }

    private void fixBackground(){
        Glide.with(this).load(R.drawable.entry_background).into((ImageView)findViewById(R.id.entry_back));
    }

    private void create_controlType_And_Speed_In_MSP_if_Doesnt_Exists(){
        String speed = MSP.getMe().getString(Activity_Game.SPEED_KEY,"none");
        String controlsType = MSP.getMe().getString(Activity_Game.CONTROL_TYPE_KEY,"none");
        if(speed.compareTo(Activity_Game.LOW_VAL)!=0 && speed.compareTo(Activity_Game.HIGH_VAL)!=0)
            MSP.getMe().putString(Activity_Game.SPEED_KEY, Activity_Game.LOW_VAL);
        if(controlsType.compareTo(Activity_Game.BUTTONS_VAL)!=0 && controlsType.compareTo(Activity_Game.ACCELOMETER_VAL)!=0)
            MSP.getMe().putString(Activity_Game.CONTROL_TYPE_KEY, Activity_Game.BUTTONS_VAL);
//
//        speed = MSP.getMe().getString(Activity_Game.SPEED_KEY,"no");
//        controlsType = MSP.getMe().getString(Activity_Game.CONTROL_TYPE_KEY,"no");
//        Log.d("SHAHARMSP1", speed+" "+controlsType);
    }

}