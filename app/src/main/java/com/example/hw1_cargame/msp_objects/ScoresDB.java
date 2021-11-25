package com.example.hw1_cargame.msp_objects;

import java.util.ArrayList;

public class ScoresDB{

    private ArrayList<ScoreRecord> records = new ArrayList<ScoreRecord>();

    public ScoresDB(){ }

    public ArrayList<ScoreRecord> getRecords() {
        return records;
    }

    public ScoresDB setRecords(ArrayList<ScoreRecord> records) {
        this.records = records;
        return this;
    }

    public Object clone(){
        ScoresDB sb_clone = new ScoresDB();
        for(ScoreRecord r: this.records){
            sb_clone.getRecords().add(r);
        }
        return sb_clone;
    }
}
