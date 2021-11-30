package com.example.hw1_cargame.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hw1_cargame.BackgroundMusic.Activity_BackgroundMusic;
import com.example.hw1_cargame.R;
import com.example.hw1_cargame.score_fragments.Fragment_GoogleMap;
import com.example.hw1_cargame.score_fragments.Fragment_ScoreList;
import com.example.hw1_cargame.msp_objects.MSP_Manager;

public class Activity_Scores extends Activity_BackgroundMusic {

    MSP_Manager msp_manager;
    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        fixBackground();
        findViews();
        msp_manager = new MSP_Manager();
        initViews();
    }

    //fix background bitmap - prevents errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.settings_background).into((ImageView)findViewById(R.id.scores_back));
    }

    private void findViews() {
        exitBtn = findViewById(R.id.scores_exit_btn);
    }

    private void initViews(){
        exitBtn.setOnClickListener( v -> {
            finish();
        });

        //fragments
        Fragment_GoogleMap fragmentMap = new Fragment_GoogleMap();
        fragmentMap.setActivity(this);
        getSupportFragmentManager().beginTransaction().add(R.id.frame2, fragmentMap).commit();
        Fragment_ScoreList fragmentList = new Fragment_ScoreList();
        fragmentList.setActivity(this);
        fragmentList.setCallBackList(fragmentMap::setLocation);
        fragmentList.setScoresDB(msp_manager.getScoresDB());
        getSupportFragmentManager().beginTransaction().add(R.id.frame1, fragmentList).commit();
    }
}