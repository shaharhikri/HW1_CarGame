package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.example.hw1_cargame.msp_objects.MSP_Manager;

public class Activity_Splash extends AppCompatActivity {

    private MSP_Manager msp_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        msp_manager = new MSP_Manager();
        msp_manager.save_ScoreDB_changes();
        msp_manager.create_SpeedValue_IfDoesntExist();
        msp_manager.create_ControlsType_IfDoesntExist();

        openGame();
    }



    private void openGame(){
        Intent myIntent = new Intent(this, Activity_Entry.class);

        Bundle bundle = new Bundle();
        String speed_setting = msp_manager.getSpeed();
        String controlsType_setting = msp_manager.getControlsType();

        bundle.putString(MSP_Manager.SPEED_KEY,speed_setting);
        bundle.putString(MSP_Manager.CONTROL_TYPE_KEY,controlsType_setting);
        bundle.putInt(Activity_GameOver.SCORE_KEY,-1);

        myIntent.putExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE, bundle);
        startActivity(myIntent);
        finish();
    }
}