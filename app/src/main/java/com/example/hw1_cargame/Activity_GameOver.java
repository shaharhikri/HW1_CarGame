package com.example.hw1_cargame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import com.example.hw1_cargame.msp_objects.MSP_Manager;
import com.example.hw1_cargame.msp_objects.ScoreRecord;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Activity_GameOver extends AppCompatActivity {
    public final static String SCORE_KEY = "SCORE_KEY";
    public final static String SPEED_AND_CONTROL_AND_SCORE_BUNDLE = "SPEED_AND_CONTROL_AND_SCORE_BUNDLE";


    /*GameOver fields*/
    private Button gameover_play_btn;
    private MaterialButton gameover_menu_btn;
    private MaterialButton gameover_scores_btn;
    private TextView gameover_score_label;
    private int score; //Score of last game.
    private FusedLocationProviderClient fusedLocationClient;
    private MSP_Manager msp_manager;
    private final float DEFAULT_LON = 0;
    private final float DEFAULT_LAT = 0;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fixBackground();
        findViews();
        bundle = getIntent().getBundleExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE);
        getScoreFromBundle();
        initViews();
        msp_manager = new MSP_Manager();
        addScoreToMSP();
    }

    //fix background bitmap - prevents errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.gameover_background).into((ImageView)findViewById(R.id.gameover_back));
    }

    private void findViews() {
        gameover_play_btn = findViewById(R.id.gameover_play_btn);
        gameover_menu_btn = findViewById(R.id.gameover_menu_btn);
        gameover_scores_btn = findViewById(R.id.gameover_scores_btn);
        gameover_score_label = findViewById(R.id.gameover_score_label);
    }

    private void getScoreFromBundle() {
        score = bundle.getInt(Activity_GameOver.SCORE_KEY, -1);
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
        bundle.putInt(Activity_GameOver.SCORE_KEY,-1);
        myIntent.putExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE, bundle);
        startActivity(myIntent);
    }

    private void addScoreToMSP() {
        Callback_EndGetLocation end_of_addScoreToMSP = (lon, lat)->{
            //Create NEW SCORERECORD
            ScoreRecord r = new ScoreRecord().setScore(score).setDate(getCurrentFormatedDate()).setLat(lat).setLon(lon);
//            Log.d("MyLocation", "latitude: "+lat+", longitude: "+lon);
            //UPDATE SCOREDB
            msp_manager.addScoreRecord_To_ScoreDB(r);
            msp_manager.save_ScoreDB_changes();
        };
        getLocation(end_of_addScoreToMSP);
    }

    private String getCurrentFormatedDate() {
        String currentTime = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(new Date());
        return currentTime;
    }

    private void getLocation(Callback_EndGetLocation cb_doItLater){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnCompleteListener( task->{
                Location location = task.getResult();
                if(location!=null)
                    cb_doItLater.afterLocationExtraction(location.getLongitude(), location.getLatitude());
                else
                    cb_doItLater.afterLocationExtraction(DEFAULT_LON, DEFAULT_LAT);
            });

        }
        else {
            cb_doItLater.afterLocationExtraction(DEFAULT_LON, DEFAULT_LAT);
        }
    }

    private static interface Callback_EndGetLocation{
        void afterLocationExtraction(double lon,  double lat);
    }
}