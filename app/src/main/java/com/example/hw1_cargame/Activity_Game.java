package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hw1_cargame.Control_Fragments.ControllCallBack;
import com.example.hw1_cargame.Control_Fragments.Fragment_Controls;
import com.example.hw1_cargame.Control_Fragments.Fregment_Accelometer;
import com.example.hw1_cargame.Control_Fragments.Fregment_Buttons;

import java.util.Objects;
import java.util.Random;

public class Activity_Game extends AppCompatActivity {

    public static enum ControlsType {BUTTONS, ACCELOMETER};
    public static final String CONTROL_TYPE_KEY = "CONTROL_TYPE_KEY";

    /*Game fields*/
    //Views
    private ImageView[][] game_grid;
    private ImageView[] game_hearts;
    private Fragment_Controls fragmentControls; // private ImageButton game_left_btn, game_right_btn;
    private ControllCallBack controllCallBack = new ControllCallBack() {

        @Override
        public void moveLeft() {
            if(car_col>0)
                move(-1);
            vibrate(GAME_BTN_VIBE_TIME);
        }

        @Override
        public void moveRight() {
            if(car_col<cols-1)
                move(+1);
            vibrate(GAME_BTN_VIBE_TIME);
        }
    };
    private ImageButton game_btn_pause;
    private TextView game_score_label;
    private RelativeLayout game_layout;
    //Vars
    private int score;
    final private int COLLISION_SCORE_SUB = 1000;
    final private int COIN_SCORE_ADD = 1000;
    final private int FORWARD_SCORE_ADD = 200;
    private int cols,rows;
    private int car_col;
    private Random rand;
    private boolean random_row0;
    //Vibrate vars
    Vibrator vib;
    final int COLLISION_VIBE_TIME=200;
    final int COINPICKUP_VIBE_TIME=50;
    final int GAME_BTN_VIBE_TIME=10;
    //Sound
    MediaPlayer coin_mp;
    MediaPlayer colision_mp;
    //Timer vars
    private final Handler handler = new Handler();
    private final int START_DELAY = 1000;
    private int delay;
    final private int SPEEDUP_SCORE_STEP = 1000;
    final private double DELAY_REDUCE_FACTOR = 0.98;
    private boolean resume;
    final private Runnable forward_run = () -> {
        forwardGrid();
        if(resume)
            startTimer();
    };

    /*Pause fields*/
    private Button pause_resume_btn;
    private RelativeLayout pause_layout;
    final private View.OnClickListener pause_resume_btn_handler = v -> {
        pause_layout.setVisibility(View.INVISIBLE);
        enableControls();
        initGamePausebtn();
        startTimer();
    };
    int back_clicks_count = 0;
    private final Handler handler_back = new Handler();
    /*Game Over*/
    final private int GAMEOVER_DELAY = 2000;

    /*-----------------Android time circle funtions-----------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_panel);
//        Objects.requireNonNull(getSupportActionBar()).hide(); //Hide app header
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //ignore night mode
        fixBackground();
        findViews();
        initVibAndSound();
        initPauseResumeBtn();
        setupGame();
        startTimer();
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
                new ImageView[]{findViewById(R.id.game_rock00), findViewById(R.id.game_rock01), findViewById(R.id.game_rock02) , findViewById(R.id.game_rock03), findViewById(R.id.game_rock04) },
                new ImageView[]{findViewById(R.id.game_rock10), findViewById(R.id.game_rock11), findViewById(R.id.game_rock12) , findViewById(R.id.game_rock13), findViewById(R.id.game_rock14) },
                new ImageView[]{findViewById(R.id.game_rock20), findViewById(R.id.game_rock21), findViewById(R.id.game_rock22), findViewById(R.id.game_rock23),  findViewById(R.id.game_rock24) },
                new ImageView[]{findViewById(R.id.game_rock30), findViewById(R.id.game_rock31), findViewById(R.id.game_rock32), findViewById(R.id.game_rock33),  findViewById(R.id.game_rock34) },
                new ImageView[]{findViewById(R.id.game_rock40), findViewById(R.id.game_rock41), findViewById(R.id.game_rock42) , findViewById(R.id.game_rock43), findViewById(R.id.game_rock44) },
                new ImageView[]{findViewById(R.id.game_rock50), findViewById(R.id.game_rock51), findViewById(R.id.game_rock52) , findViewById(R.id.game_rock53), findViewById(R.id.game_rock54) },
                new ImageView[]{findViewById(R.id.game_rock60), findViewById(R.id.game_rock61), findViewById(R.id.game_rock62), findViewById(R.id.game_rock63),  findViewById(R.id.game_rock64) },
                new ImageView[]{findViewById(R.id.game_rock70), findViewById(R.id.game_rock71), findViewById(R.id.game_rock72), findViewById(R.id.game_rock73),  findViewById(R.id.game_rock74) }
        };

        game_hearts = new ImageView[] {
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3)
        };

        game_layout = findViewById(R.id.game_layout);
        game_btn_pause = findViewById(R.id.game_btn_pause);
        game_score_label = findViewById(R.id.game_score_label);

        //Pause
        pause_resume_btn = findViewById(R.id.pause_resume_btn);
        pause_layout = findViewById(R.id.pause_layout);
    }


    /*-----------------INITS-----------------*/
    //Prevent errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.game_background).into((ImageView)findViewById(R.id.game_back));
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

    private void initGameControls(ControlsType controlsType){
        if(controlsType==ControlsType.BUTTONS)
            fragmentControls = new Fregment_Buttons();
        else
            fragmentControls = new Fregment_Accelometer();

        fragmentControls.setActivity(this);
        fragmentControls.setControllCallBack(controllCallBack);
        getSupportFragmentManager().beginTransaction().add(R.id.controls_frame, fragmentControls).commit();
    }

    private void enableControls(){
        fragmentControls.enable();
    }

    private void disableGameControls(){
        fragmentControls.disable();
    }

    private void initGamePausebtn() {
        game_btn_pause.setOnClickListener(v -> {
            pauseGame();
            vibrate(GAME_BTN_VIBE_TIME);
        });
    }

    private void initScore(){
        score = 0;
        addScore(0);
    }

    private void initHearts(){
        for (ImageView heart : game_hearts)
            heart.setVisibility(View.VISIBLE);
    }

    private void initVibAndSound(){
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        coin_mp = MediaPlayer.create(this, R.raw.coin);
        colision_mp = MediaPlayer.create(this, R.raw.crash);
    }

    private void initPauseResumeBtn(){
        pause_resume_btn.setOnClickListener(pause_resume_btn_handler);
    }


    /*-----------------Timer-----------------*/
    private void startTimer(){
        resume = true;
        delay = (int) (START_DELAY *  Math.pow(DELAY_REDUCE_FACTOR, (double)score/SPEEDUP_SCORE_STEP+1));
        handler.postDelayed(forward_run, delay);
    }

    private void stopTimer(){
        resume = false;
        handler.removeCallbacks(forward_run);
    }

    @Override
    protected void onStart() {
        super.onStart();
        back_clicks_count = 0;
    }


    /*-----------------Setup and Run Game-----------------*/
    private void setupGame(){
        rows = game_grid.length;
        cols = game_grid[0].length;
        rand = new Random();
        random_row0 = false;
        delay = START_DELAY;
        initGrid();
        initHearts();
        initScore();

        int ctype_index = getIntent().getBundleExtra("Bundle").getInt(Activity_Game.CONTROL_TYPE_KEY, 0);
        ControlsType ctype = ControlsType.values()[ctype_index];
//        Log.d("pttt", "ctype: "+ctype+"  ctype_index: "+ctype_index);
        initGameControls(ctype);
        initGamePausebtn();
        game_layout.setVisibility(View.VISIBLE);
        pause_layout.setVisibility(View.INVISIBLE);
    }

    private void move(int direction){
        game_grid[rows-1][car_col].setVisibility(View.INVISIBLE);
        car_col = (car_col+cols+direction)%cols;
        if(game_grid[rows-1][car_col].getVisibility()==View.VISIBLE){
            Integer tag = (Integer)(game_grid[rows-1][car_col].getTag());
            if(tag!=null && tag.intValue()==(int)R.drawable.ic_rock)
                performCollision();
            else
                performCoinPickUp();
        }
        else{
            game_grid[rows-1][car_col].setImageResource(R.drawable.ic_car);
            game_grid[rows-1][car_col].setVisibility(View.VISIBLE);
        }
    }

    private void vibrate(int millisec){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vib.vibrate(VibrationEffect.createOneShot(millisec, VibrationEffect.DEFAULT_AMPLITUDE));
        else
            vib.vibrate(millisec);  //deprecated in API 26
    }

    private void addScore(int addition){
        score+=addition;
        score = Math.max(score, 0);
        game_score_label.setText((""+score));
    }

    private void forwardGrid(){
        for (int i = rows-1; i>0; i--)
            copyRow(i-1,i);
        if(random_row0)
            randomRow0();
        else
            setRowInvisible(0);
        random_row0=!random_row0;

        if(game_grid[rows-1][car_col].getVisibility()==View.VISIBLE){
            Integer tag = (Integer)(game_grid[rows-1][car_col].getTag());
            if(tag!=null && tag.intValue()==(int)R.drawable.ic_rock)
                performCollision();
            else
                performCoinPickUp();
        }
        else {
            move(0);
            addScore(FORWARD_SCORE_ADD);
        }
    }

    private void copyRow(int from_row, int to_row){
        for (int j = 0; j < cols; j++) {
            game_grid[to_row][j].setVisibility(game_grid[from_row][j].getVisibility());
            Integer tag = (Integer)(game_grid[from_row][j].getTag());
            game_grid[to_row][j].setTag(tag);
            if(tag!=null && tag.intValue()==(int)R.drawable.ic_coin)
                game_grid[to_row][j].setImageResource(R.drawable.ic_coin);
            else
                game_grid[to_row][j].setImageResource(R.drawable.ic_rock);
        }
    }

    private void randomRow0(){
        setRowInvisible(0);
        int rand_col = rand.nextInt(cols);
        boolean is_coin = rand.nextInt(10)>6? true: false;
        game_grid[0][rand_col].setVisibility(View.VISIBLE);
        if(is_coin) {
            game_grid[0][rand_col].setImageResource(R.drawable.ic_coin);
            game_grid[0][rand_col].setTag((Integer) R.drawable.ic_coin);
        }
        else{
            game_grid[0][rand_col].setImageResource(R.drawable.ic_rock);
            game_grid[0][rand_col].setTag((Integer) R.drawable.ic_rock);
        }
    }

    private void setRowInvisible(int row){
        for (int j = 0; j < cols; j++)
            game_grid[row][j].setVisibility(View.INVISIBLE);
    }

    private void performCollision(){
        Toast.makeText(Activity_Game.this, "BOOM!", Toast.LENGTH_SHORT).show();
        game_grid[rows-1][car_col].setImageResource(R.drawable.ic_explosion);
        //sound
        colision_mp.start();
        //vibrate
        vibrate(COLLISION_VIBE_TIME);
        //lives
        for (int i = game_hearts.length-1; i >= 0; i--) {
            if(game_hearts[i].getVisibility()==View.VISIBLE){
                game_hearts[i].setVisibility(View.INVISIBLE);
                break;
            }
        }
        //score update
        addScore(-COLLISION_SCORE_SUB);
        //gameover
        if(game_hearts[0].getVisibility()==View.INVISIBLE)
            gameOver();
    }

    private void performCoinPickUp(){
        Toast.makeText(Activity_Game.this, "Coin!", Toast.LENGTH_SHORT).show();
        game_grid[rows-1][car_col].setImageResource(R.drawable.ic_coinpickup);
        //sound
        coin_mp.start();
        //vibrate
        vibrate(COINPICKUP_VIBE_TIME);
        //score update
        addScore(COIN_SCORE_ADD);
    }


    /*-----------------Setup Pause screen-----------------*/
    private void pauseGame(){
        if(resume) {
            stopTimer();
            disableGameControls();
            disableGamePausebtn();
            pause_layout.setVisibility(View.VISIBLE);
        }
    }

    private void disableGamePausebtn(){
        game_btn_pause.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(resume){
            pauseGame();
        }
        else{
            if(back_clicks_count==0){
                handler_back.postDelayed(() -> {
                    back_clicks_count = 0;
                }, 1000);
                Toast.makeText(Activity_Game.this, "Push 'back' again for EXIT.", Toast.LENGTH_SHORT).show();
                back_clicks_count++;
            }
            else if(back_clicks_count==1){
                finish();
            }
        }
    }


    /*-----------------End Game (Game Over)-----------------*/
    private void gameOver(){
        stopTimer();
        disableGameControls();
        disableGamePausebtn();
        Log.d("Activity_Game", "setupGameOver() ");
        handler.postDelayed(()->{
            Bundle bundle = getIntent().getBundleExtra("Bundle");
            bundle.putInt(Activity_GameOver.SCORE_KEY, score);
            Intent myIntent = new Intent(this, Activity_GameOver.class);
            myIntent.putExtra("Bundle", bundle);
            startActivity(myIntent);
            finish();
        }, GAMEOVER_DELAY);
    }
}