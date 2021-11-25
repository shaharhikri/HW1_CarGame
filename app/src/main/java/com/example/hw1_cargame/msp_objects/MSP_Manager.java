package com.example.hw1_cargame.msp_objects;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MSP_Manager {
    public final static String SCORES_DB_KEY = "SCORES_DB_KEY";

    public static final String ACCELOMETER_VAL = "ACCELOMETER_VAL";
    public static final String BUTTONS_VAL = "BUTTONS_VAL";
    public static final String CONTROL_TYPE_KEY = "CONTROL_TYPE_KEY";

    public static final String LOW_VAL = "LOW_VAL";
    public static final String HIGH_VAL = "HIGH_VAL";
    public static final String SPEED_KEY = "LOW_KEY";

    public static final String NONE_VAL = "none";

    private ScoresDB scoresDB;

    public MSP_Manager(){
        initScoresDB();
    }


    /*-------------------------ScoreDb----------------------------*/
    private void initScoresDB(){
        String scoresDB_json = MSP.getMe().getString(MSP_Manager.SCORES_DB_KEY,NONE_VAL);
        if(scoresDB_json.compareTo(NONE_VAL)==0)
            scoresDB = new ScoresDB();
        else
            scoresDB = new Gson().fromJson(scoresDB_json, ScoresDB.class);
    }

    public ScoresDB getScoresDB(){
        return scoresDB;
    }

    public void addScoreRecord_To_ScoreDB(ScoreRecord r){
        insertToTopTen(r,scoresDB.getRecords());
    }

    public void save_ScoreDB_changes(){
        //CONVERT TO JSON
        String json = new Gson().toJson(scoresDB);
        //SAVE IN MSP AS STRING
        MSP.getMe().putString(MSP_Manager.SCORES_DB_KEY, json);
    }

    private void insertToTopTen(ScoreRecord r, ArrayList<ScoreRecord> msp_records){
        boolean flag_inserted = false;
        if(msp_records.size()<10){
            flag_inserted = msp_records.add(r);
        }
        else{
            ScoreRecord min_r = msp_records.get(0);
            for(ScoreRecord rc : msp_records){
                if(rc.getScore()<min_r.getScore())
                    min_r = rc;
            }
            if(min_r.getScore()<r.getScore()) {
                flag_inserted = msp_records.add(r);
                if(msp_records.add(r)){
                    flag_inserted = true;
                    msp_records.remove(min_r);
                }
            }
        }

        if(flag_inserted) {
            Collections.sort(msp_records, new Comparator<ScoreRecord>() {
                @Override
                public int compare(ScoreRecord o1, ScoreRecord o2) {
                    if (o1.getScore() < o2.getScore())
                        return 1;
                    else if (o1.getScore() > o2.getScore())
                        return -1;
                    else
                        return 0;
                }
            });
        }

        Log.d("TopTenArrayList_add", "add: "+flag_inserted);
    }

    /*-------------------------Speed----------------------------*/
    public String getSpeed(){
        return MSP.getMe().getString(MSP_Manager.SPEED_KEY,MSP_Manager.NONE_VAL);
    }

    public void updateSpeed(String speed){
        MSP.getMe().putString(MSP_Manager.SPEED_KEY, speed);
    }


    /*-------------------------ControlsType----------------------------*/
    public String getControlsType(){
        return MSP.getMe().getString(MSP_Manager.CONTROL_TYPE_KEY,MSP_Manager.NONE_VAL);
    }

    public void updateControlsType(String controlsType){
        MSP.getMe().putString(MSP_Manager.CONTROL_TYPE_KEY, controlsType);
    }

    /*-------------------------Check isExist----------------------------*/
    public boolean scoresDB_isExist(){
        String scoresDB_json = MSP.getMe().getString(MSP_Manager.SCORES_DB_KEY,NONE_VAL);
        return (scoresDB_json.compareTo(NONE_VAL)!=0);
    }

    public boolean speed_isExist(){
        String speed = MSP.getMe().getString(MSP_Manager.SPEED_KEY,NONE_VAL);
        return (speed.compareTo(MSP_Manager.LOW_VAL)==0 || speed.compareTo(MSP_Manager.HIGH_VAL)==0);
    }

    public boolean controlsType_isExist(){
        String controlsType = MSP.getMe().getString(MSP_Manager.CONTROL_TYPE_KEY,NONE_VAL);
        return (controlsType.compareTo(MSP_Manager.BUTTONS_VAL)==0 || controlsType.compareTo(MSP_Manager.ACCELOMETER_VAL)==0);
    }

    /*-------------------------Create if doesnt exist----------------------------*/

    public void create_SpeedValue_IfDoesntExist(){
        if(!speed_isExist())
            MSP.getMe().putString(MSP_Manager.SPEED_KEY, MSP_Manager.LOW_VAL);
    }

    public void create_ControlsType_IfDoesntExist(){
        if(!controlsType_isExist())
            MSP.getMe().putString(MSP_Manager.CONTROL_TYPE_KEY, MSP_Manager.BUTTONS_VAL);
    }

}
