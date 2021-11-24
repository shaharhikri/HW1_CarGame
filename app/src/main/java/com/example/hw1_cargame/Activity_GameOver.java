package com.example.hw1_cargame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.hw1_cargame.score_objects.ScoreRecord;
import com.example.hw1_cargame.score_objects.ScoresDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Activity_GameOver extends AppCompatActivity {
    public final static String SCORE_KEY = "SCORE_KEY";

    /*GameOver fields*/
    private Button gameover_play_btn;
    private MaterialButton gameover_menu_btn;
    private MaterialButton gameover_scores_btn;
    private TextView gameover_score_label;
    private int score; //Score of last game.
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fixBackground();
        findViews();
        getScore();
        initViews();
        addScoreToMSP();
    }

    //Prevent errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.gameover_background).into((ImageView)findViewById(R.id.gameover_back));
    }

    private void findViews() {
        gameover_play_btn = findViewById(R.id.gameover_play_btn);
        gameover_menu_btn = findViewById(R.id.gameover_menu_btn);
        gameover_scores_btn = findViewById(R.id.gameover_scores_btn);
        gameover_score_label = findViewById(R.id.gameover_score_label);
    }

    private void getScore() {
        score = getIntent().getBundleExtra("Bundle").getInt(Activity_GameOver.SCORE_KEY, -1);
    }

    private void initViews(){
        gameover_score_label.setText("SCORE: "+score);
        gameover_play_btn.setOnClickListener(v -> {
            gotoActivity(Activity_Game.class);
            finish();
        });
        gameover_menu_btn.setOnClickListener(v -> {
            gotoActivity(Activity_Entry.class);
            finish();
        });
        gameover_scores_btn.setOnClickListener(v -> {
            gotoActivity(Activity_Scores.class);
        });
    }

    private void gotoActivity(Class activityClass){
        Intent myIntent = new Intent(this, activityClass);
        startActivity(myIntent);
    }

    private void addScoreToMSP() {
        String scoresDB_json = MSP.getMe().getString(ScoresDB.SCORES_DB_KEY,"none");
        ScoresDB scoresDB;
        //CREATE/GET SCOREDB
        if(scoresDB_json.compareTo("none")==0)
            scoresDB = new ScoresDB();
        else
            scoresDB = new Gson().fromJson(scoresDB_json, ScoresDB.class);
        //UPDATE SCOREDB

        ScoreRecord r = new ScoreRecord();
        r.setScore(score);
        r.setDate(getCurrentFormatedDate());
        getLocation((lon, lat)->{
            r.setLat(lat); r.setLon(lon); //TODO: CHANGE TO LOCATION
            Log.d("MyLocation", "latitude: "+lat+", longitude: "+lon);

            insertToTopTen(r,scoresDB.getRecords());

            //CONVERT TO JSON
            String json = new Gson().toJson(scoresDB);
            //SAVE IN MSP AS STRING
            MSP.getMe().putString(ScoresDB.SCORES_DB_KEY, json);
        });
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

    private String getCurrentFormatedDate() {
        String currentTime = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(new Date());
        return currentTime;
    }

    private void getLocation(addScoreToMSP_FinishAddScoreToMSP cb){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location!=null) {
                        Geocoder geocoder = new Geocoder(Activity_GameOver.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            double lon = addresses.get(0).getLongitude();
                            double lat = addresses.get(0).getLatitude();
                            Log.d("ShaharCll", "onComplete: do");
                            cb.doItLater(lon, lat); //onComplete needs a callback - because it's an async method!
                            Log.d("ShaharCll", "onComplete: did");
                        }
                        catch(IOException e){
                            cb.doItLater(0, 0); //onComplete needs a callback - because it's an async method!
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private static interface addScoreToMSP_FinishAddScoreToMSP{
        public void doItLater(double lon,  double lat);
    }
}