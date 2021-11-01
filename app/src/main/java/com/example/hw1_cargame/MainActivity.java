package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    /*Game fields*/
    //Views
    private ImageView game_grid[][];
    private ImageView[] game_hearts;
    private ImageButton game_left_btn, game_right_btn, game_btn_pause;
    private TextView game_score_label;
    private RelativeLayout game_layout;
    //Vars
    private int score;
    private int COLLISION_SCORE_SUB = 1000;
    private int FORWARD_SCORE_ADD = 200;
    private int cols,rows;
    private int car_col;
    private Random rand;
    private boolean random_row0;
    //Timer vars
    private final Handler handler = new Handler();
    private final int START_DELAY = 1000;
    private int delay;
    private int speedup_score_step = 1000;
    private double delay_reduce_factor = 0.95;
    private Runnable forward_run = new Runnable() {
        @Override
        public void run() {
            forwardGrid();
            startTimer();
        }
    };
    //Vibrate vars
    Vibrator vib;
    final int COLLISION_VIBE_TIME=200;
    final int GAME_BTN_VIBE_TIME=10;

    /*Entry fields*/
    private Button entry_play_btn;
    private RelativeLayout entry_layout;
    private View.OnClickListener play_btn_handler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setupGame();
            startTimer();
        }
    };

    /*Pause fields*/
    private boolean resume;
    private Button pause_resume_btn;
    private RelativeLayout pause_layout;
    private View.OnClickListener pause_resume_btn_handler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resume = true;
            pause_layout.setVisibility(View.INVISIBLE);
            initGameBtns();
            startTimer();
        }
    };

    /*GameOver fields*/
    private Button gameover_play_btn;
    private RelativeLayout gameover_layout;
    private TextView gameover_score_label;


    /*-----------------Android time circle funtions-----------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_panel);
        getSupportActionBar().hide(); //Hide app header
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //ignore night mode
        fixBackground();
        findViews();
        initEntryPlayBtn();
        initVib();
        initPauseResumeBtn();
        initGameOverPlayBtn();

        setupEntry();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseGame();
    }

    /*-----------------Find all views-----------------*/
    private void findViews() {
        //Game
        game_grid = new ImageView[][]{
                new ImageView[]{findViewById(R.id.game_rock00),findViewById(R.id.game_rock01),findViewById(R.id.game_rock02)},
                new ImageView[]{findViewById(R.id.game_rock10),findViewById(R.id.game_rock11),findViewById(R.id.game_rock12)},
                new ImageView[]{findViewById(R.id.game_rock20), findViewById(R.id.game_rock21), findViewById(R.id.game_rock22)},
                new ImageView[]{findViewById(R.id.game_rock30), findViewById(R.id.game_rock31), findViewById(R.id.game_rock32)}
        };

        game_hearts = new ImageView[] {
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3)
        };

        game_layout = findViewById(R.id.game_layout);
        game_left_btn = findViewById(R.id.game_btn_left);
        game_right_btn = findViewById(R.id.game_btn_right);
        game_btn_pause = findViewById(R.id.game_btn_pause);
        game_score_label = findViewById(R.id.game_score_label);

        //Entry
        entry_play_btn = findViewById(R.id.entry_play_btn);
        entry_layout = findViewById(R.id.entry_layout);

        //GameOver
        gameover_play_btn = findViewById(R.id.gameover_play_btn);
        gameover_layout = findViewById(R.id.gameover_layout);
        gameover_score_label = findViewById(R.id.gameover_score_label);

        //Pause
        pause_resume_btn = findViewById(R.id.pause_resume_btn);
        pause_layout = findViewById(R.id.pause_layout);
    }


    /*-----------------INITS-----------------*/
    //Prevent errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.game_background2).into((ImageView)findViewById(R.id.game_back));
        Glide.with(this).load(R.drawable.entry_background).into((ImageView)findViewById(R.id.entry_back));
        Glide.with(this).load(R.drawable.gameover_background).into((ImageView)findViewById(R.id.gameover_back));
    }

    private void initGrid(){
        randomRow0();
        for (int i = 1; i < rows; i++)
            setRowInvisible(i);

        //Car
        car_col = cols/2;
        game_grid[rows-1][car_col].setImageResource(R.drawable.ic_car);
        game_grid[rows-1][car_col].setVisibility(View.VISIBLE);
    }

    private void initGameBtns(){
        game_right_btn.setOnClickListener(v -> {if(car_col<cols-1) move(+1);vibrate(GAME_BTN_VIBE_TIME);});
        game_left_btn.setOnClickListener(v -> {if(car_col>0) move(-1);vibrate(GAME_BTN_VIBE_TIME);});
        game_btn_pause.setOnClickListener(v -> {pauseGame();vibrate(GAME_BTN_VIBE_TIME);});
    }

    private void initScore(){
        score = 0;
        addScore(0);
    }

    private void initHearts(){
        for (int i = 0; i < game_hearts.length; i++)
            game_hearts[i].setVisibility(View.VISIBLE);
    }

    private void initVib(){
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initEntryPlayBtn(){
        entry_play_btn.setOnClickListener(play_btn_handler);
    }

    private void initPauseResumeBtn(){
        pause_resume_btn.setOnClickListener(pause_resume_btn_handler);
    }

    private void initGameOverPlayBtn(){
        gameover_play_btn.setOnClickListener(play_btn_handler);
    }


    /*-----------------Timer-----------------*/
    private void startTimer(){
        if(resume) {
            delay = (int) (START_DELAY *  Math.pow(delay_reduce_factor,score/speedup_score_step+1));
            handler.postDelayed(forward_run, delay);
        }
    }

    private void stopTimer(){
        handler.removeCallbacks(forward_run);
    }


    /*-----------------Setup and Run Game-----------------*/
    private void setupGame(){
        rows = game_grid.length;
        cols = game_grid[0].length;
        rand = new Random();
        random_row0 = false;
        resume = true;
        delay = START_DELAY;

        initGrid();
        initHearts();
        initScore();
        initGameBtns();

        game_layout.setVisibility(View.VISIBLE);
        entry_layout.setVisibility(View.INVISIBLE);
        pause_layout.setVisibility(View.INVISIBLE);
        gameover_layout.setVisibility(View.INVISIBLE);
    }

    private void move(int direction){
        game_grid[rows-1][car_col].setVisibility(View.INVISIBLE);

        car_col = (car_col+cols+direction)%cols;

        if(game_grid[rows-1][car_col].getVisibility()==View.VISIBLE){
            performCollision();
        }
        else{
            game_grid[rows-1][car_col].setImageResource(R.drawable.ic_car);
            game_grid[rows-1][car_col].setVisibility(View.VISIBLE);
        }
    }
    
    private void vibrate(int millisec){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(millisec, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26 
            vib.vibrate(millisec);
        }
    }

    private void addScore(int addition){
        score+=addition;
        score = score>0?score:0;
        game_score_label.setText(score+"");
    }

    private void forwardGrid(){
        for (int i = rows-1; i>0; i--)
            copyRow(i-1,i);
        if(random_row0)
            randomRow0();
        else
            setRowInvisible(0);
        random_row0=!random_row0;

        if(game_grid[rows-1][car_col].getVisibility()==View.VISIBLE)
            performCollision();
        else {
            move(0);
            addScore(FORWARD_SCORE_ADD);
        }
    }

    private void randomRow0(){
        setRowInvisible(0);
        game_grid[0][rand.nextInt(cols)].setVisibility(View.VISIBLE);
    }

    private void setRowInvisible(int row){
        for (int j = 0; j < cols; j++)
            game_grid[row][j].setVisibility(View.INVISIBLE);
    }

    private void copyRow(int from_row, int to_row){
        for (int j = 0; j < cols; j++) {
            game_grid[to_row][j].setVisibility(game_grid[from_row][j].getVisibility());
            game_grid[to_row][j].setImageResource(R.drawable.ic_rock);
        }
    }

    private void performCollision(){
        game_grid[rows-1][car_col].setImageResource(R.drawable.ic_explosion);
        //vibrate
        vibrate(COLLISION_VIBE_TIME);
        //lives
        for (int i = game_hearts.length-1; i >= 0; i--) {
            if(game_hearts[i].getVisibility()==View.VISIBLE){
                game_hearts[i].setVisibility(View.INVISIBLE);
                break;
            }
        }
        addScore(-COLLISION_SCORE_SUB);
        if(game_hearts[0].getVisibility()==View.INVISIBLE)
            setupGameOver();
    }


    /*-----------------Setup Entry screen-----------------*/
    private void setupEntry(){
        resume = false;
        stopTimer();
        entry_layout.setVisibility(View.VISIBLE);
        game_layout.setVisibility(View.INVISIBLE);
        pause_layout.setVisibility(View.INVISIBLE);
        gameover_layout.setVisibility(View.INVISIBLE);
    }


    /*-----------------Setup Pause screen-----------------*/
    private void pauseGame(){
        if(resume) {
            resume = false;
            stopTimer();
            disableGameBtns();
            pause_layout.setVisibility(View.VISIBLE);
        }
    }

    private void disableGameBtns(){
        game_left_btn.setOnClickListener(null);
        game_right_btn.setOnClickListener(null);
        game_btn_pause.setOnClickListener(null);
    }


    /*-----------------Setup GameOver screen-----------------*/
    private void setupGameOver(){
        resume = false;
        stopTimer();
        gameover_layout.setVisibility(View.VISIBLE);
        gameover_layout.setVisibility(View.VISIBLE);
        game_layout.setVisibility(View.INVISIBLE);
        pause_layout.setVisibility(View.INVISIBLE);
        gameover_score_label.setText("SCORE: "+score);
    }

}