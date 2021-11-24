package com.example.hw1_cargame.score_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hw1_cargame.R;
import com.example.hw1_cargame.score_objects.ScoreRecord;
import com.example.hw1_cargame.score_objects.ScoresDB;

public class Fragment_ScoreList extends Fragment {

    private AppCompatActivity activity;
    private CallBack_List callBackList;
    private ScoresDB scoresDB;

    //Rows
    private LinearLayout[] layouts;
    private TextView[] scores;
    private TextView[] times;

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setCallBackList(CallBack_List callBackList) {
        this.callBackList = callBackList;
    }

    public void setScoresDB(ScoresDB scoresDB){
        this.scoresDB = scoresDB;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scorelist, container, false);
        findViews(view);
        initViews();
        return view;
    }

    private void findViews(View view) {
        layouts = new LinearLayout[]{
                view.findViewById(R.id.scorelist_row0),
                view.findViewById(R.id.scorelist_row1),
                view.findViewById(R.id.scorelist_row2),
                view.findViewById(R.id.scorelist_row3),
                view.findViewById(R.id.scorelist_row4),
                view.findViewById(R.id.scorelist_row5),
                view.findViewById(R.id.scorelist_row6),
                view.findViewById(R.id.scorelist_row7),
                view.findViewById(R.id.scorelist_row8),
                view.findViewById(R.id.scorelist_row9),
        };

        times = new TextView[]{
                view.findViewById(R.id.scorelist_row0_time),
                view.findViewById(R.id.scorelist_row1_time),
                view.findViewById(R.id.scorelist_row2_time),
                view.findViewById(R.id.scorelist_row3_time),
                view.findViewById(R.id.scorelist_row4_time),
                view.findViewById(R.id.scorelist_row5_time),
                view.findViewById(R.id.scorelist_row6_time),
                view.findViewById(R.id.scorelist_row7_time),
                view.findViewById(R.id.scorelist_row8_time),
                view.findViewById(R.id.scorelist_row9_time),
        };

        scores = new TextView[]{
                view.findViewById(R.id.scorelist_row0_score),
                view.findViewById(R.id.scorelist_row1_score),
                view.findViewById(R.id.scorelist_row2_score),
                view.findViewById(R.id.scorelist_row3_score),
                view.findViewById(R.id.scorelist_row4_score),
                view.findViewById(R.id.scorelist_row5_score),
                view.findViewById(R.id.scorelist_row6_score),
                view.findViewById(R.id.scorelist_row7_score),
                view.findViewById(R.id.scorelist_row8_score),
                view.findViewById(R.id.scorelist_row9_score),
        };
    }

    private void initViews() {
        for (int i = 0; i < 10; i++)
            layouts[i].setVisibility(View.GONE);
        int len = Math.min(10, scoresDB.getRecords().size());
        for (int i = 0; i < len; i++) {
            layouts[i].setVisibility(View.VISIBLE);
            layouts[i].setOnClickListener(v -> {
                //callBackList.
            });
            ScoreRecord r = scoresDB.getRecords().get(i);
            scores[i].setText(""+r.getScore());
            times[i].setText(r.getDate());
        }
    }
}