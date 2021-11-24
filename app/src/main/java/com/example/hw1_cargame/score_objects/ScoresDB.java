package com.example.hw1_cargame.score_objects;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ScoresDB {
    public final static String SCORES_DB_KEY = "SCORES_DB_KEY";

    private ArrayList<ScoreRecord> records = new ArrayList<ScoreRecord>();

    public ScoresDB(){ }

    public ArrayList<ScoreRecord> getRecords() {
        return records;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ScoresDB setRecords(ArrayList<ScoreRecord> records) {
        this.records = records;
        return this;
    }
}
