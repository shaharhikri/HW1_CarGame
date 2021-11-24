package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hw1_cargame.score_fragments.CallBack_List;
import com.example.hw1_cargame.score_fragments.CallBack_Map;
import com.example.hw1_cargame.score_fragments.Fragment_GoogleMap;
import com.example.hw1_cargame.score_fragments.Fragment_ScoreList;
import com.example.hw1_cargame.score_objects.ScoresDB;
import com.google.gson.Gson;

public class Activity_Scores extends AppCompatActivity {


    private ScoresDB scoresDB;
    private Button exitBtn;
    //    private TextView scores_data_label;

    private Fragment_ScoreList fragmentList;
    private Fragment_GoogleMap fragmentMap;
    private CallBack_Map callBack_map = new CallBack_Map() {
        @Override
        public void mapClicked(double lat, double lon) {

        }
    };
    private CallBack_List callBackList = new CallBack_List() {
        @Override
        public void setMainTitle(String str) {

        }

        @Override
        public void setTitleColor(int color) {

        }

        @Override
        public void rowSelected(String name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        fixBackground();
        findViews();
        initScoresDBfromMSP();
        initViews();
    }

    //Prevent errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.settings_background).into((ImageView)findViewById(R.id.scores_back));
    }

    private void findViews() {
//        scores_data_label = findViewById(R.id.scores_data_label);

        exitBtn = findViewById(R.id.scores_exit_btn);
    }

    private void initScoresDBfromMSP(){
        String scoresDB_json = MSP.getMe().getString(ScoresDB.SCORES_DB_KEY,"none");
        if(scoresDB_json.compareTo("none")==0)
            scoresDB = new ScoresDB();
        else
            scoresDB = new Gson().fromJson(scoresDB_json, ScoresDB.class);
    }

    private void initViews(){
//        scores_data_label.setText(scoresDB.toString());
        exitBtn.setOnClickListener( v -> {
            finish();
        });

        //fragments
        fragmentList = new Fragment_ScoreList();
        fragmentList.setActivity(this);
        fragmentList.setCallBackList(callBackList);
        fragmentList.setScoresDB(scoresDB);
        getSupportFragmentManager().beginTransaction().add(R.id.frame1, fragmentList).commit();

        fragmentMap = new Fragment_GoogleMap();
        fragmentMap.setActivity(this);
        fragmentMap.setCallBack_map(callBack_map);
        getSupportFragmentManager().beginTransaction().add(R.id.frame2, fragmentMap).commit();
    }
}