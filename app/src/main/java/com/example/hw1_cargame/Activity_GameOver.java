package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class Activity_GameOver extends AppCompatActivity {
    public final static String SCORE_KEY = "SCORE_KEY";

    /*GameOver fields*/
    private Button gameover_play_btn;
    private RelativeLayout gameover_layout;
    private TextView gameover_score_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);
        fixBackground();
        findViews();
        initGameOverPlayBtn();
        initScoreLabel();
    }

    //Prevent errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.gameover_background).into((ImageView)findViewById(R.id.gameover_back));
    }

    private void findViews() {
        gameover_play_btn = findViewById(R.id.gameover_play_btn);
        gameover_layout = findViewById(R.id.gameover_layout);
        gameover_score_label = findViewById(R.id.gameover_score_label);
    }

    private void initGameOverPlayBtn(){
        gameover_play_btn.setOnClickListener(v -> {
            startGame();
        });
    }

    private void initScoreLabel() {
        int score = getIntent().getBundleExtra("Bundle").getInt(Activity_GameOver.SCORE_KEY, -1);
        gameover_score_label.setText("SCORE: "+score);
    }

    private void startGame() {
        Intent myIntent = new Intent(this, Activity_Game.class);
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        myIntent.putExtra("Bundle", bundle);
        startActivity(myIntent);
        finish();
    }
}