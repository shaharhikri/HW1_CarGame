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
    private RelativeLayout entry_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
//        Objects.requireNonNull(getSupportActionBar()).hide(); //Hide app header
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //ignore night mode
        findViews();
        fixBackground();
        initEntryPlayBtn();
    }

    private void findViews() {
        //Entry
        entry_play_btn = findViewById(R.id.entry_play_btn);
        entry_layout = findViewById(R.id.entry_layout);
    }

    private void initEntryPlayBtn(){
        entry_play_btn.setOnClickListener(v -> {
            startGame(Activity_Game.ControlsType.BUTTONS);
        });
    }

    private void startGame(Activity_Game.ControlsType control_type) {
        Intent myIntent = new Intent(this, Activity_Game.class);

        Bundle bundle = new Bundle();
        Log.d("pttt", "control_type.ordinal(): "+control_type.ordinal());
        bundle.putInt(Activity_Game.CONTROL_TYPE_KEY, control_type.ordinal());

        myIntent.putExtra("Bundle", bundle);
        startActivity(myIntent);
        finish();
    }

    private void fixBackground(){
        Glide.with(this).load(R.drawable.entry_background).into((ImageView)findViewById(R.id.entry_back));
    }
}