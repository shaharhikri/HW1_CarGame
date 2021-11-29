package com.example.hw1_cargame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.hw1_cargame.R;
import com.example.hw1_cargame.control_fragments.CallBack_Controll;
import com.example.hw1_cargame.control_fragments.Fragment_Controls;
import com.example.hw1_cargame.control_fragments.Fregment_Accelometer;
import com.example.hw1_cargame.control_fragments.Fregment_Buttons;
import com.example.hw1_cargame.msp_objects.MSP_Manager;

import java.text.DecimalFormat;
import java.util.Random;

public class Activity_Game extends AppCompatActivity {
    /*Game fields*/
    //Views
    private ImageView[][] game_grid;
    private ImageView[] game_hearts;
    private ImageButton game_btn_pause;
    private TextView game_score_label;
    private TextView game_odometer_label;
    private RelativeLayout game_layout;
    private Fragment_Controls fragmentControls; // private ImageButton game_left_btn, game_right_btn;
    private final CallBack_Controll controllCallBack = new CallBack_Controll() {

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

        private boolean speeded_up = false;

        @Override
        public void speedUp() {
                base_delay = (int) (base_delay * SPEEDUP_FACTOR);
                if (base_delay<BASE_DELAY_MIN)
                    base_delay = BASE_DELAY_MIN;
        }

        @Override
        public void speedDown() {
            base_delay = (int) (base_delay / SPEEDUP_FACTOR);
            if (base_delay>BASE_DELAY_MAX)
                base_delay = BASE_DELAY_MAX;
        }
    };
    //Vars
    private int distance;
    private int score;
    private final int COIN_SCORE_ADD = 1000; //How much scores will be increased by a coin picking up
    private final int FORWARD_SCORE_ADD = 200; //How much scores will be increased in the progress of the track in one stage
    private int cols,rows; //Num of cols&rows
    private int car_col; //Car current position
    private Random rand;
    private boolean random_row0;
    //Vibrate vars
    private Vibrator vib;
    private final int COLLISION_VIBE_TIME=200; //Vibrate time (in millisec) in collision.
    private final int COINPICKUP_VIBE_TIME=50; //Vibrate time (in millisec) in pick up a coin.
    private final int GAME_BTN_VIBE_TIME=10; //Vibrate time (in millisec) in push game button (right\left\pause in game mode).
    //Sound
    private MediaPlayer coin_mp; //plays coin pick up sound.
    private MediaPlayer colision_mp; //plays rock collision sound.
    //Timer vars
    private final Handler handler = new Handler();
    private int delay;
    private int base_delay = 1000; //starting delay
    private static double SPEEDUP_FACTOR = 0.1;
    private static int BASE_DELAY_MIN = 200;
    private static int BASE_DELAY_MAX = 1000;
    private final float LOW_SPEED_FACTOR = 1.5f;  //start_delay multiplies with this if user chose in low speed
    private final float HIGH_SPEED_FACTOR = 0.5f; //start_delay multiplies with this if user chose in high speed
    private final int SPEEDUP_SCORE_STEP = 1000;
    private final double DELAY_REDUCE_FACTOR = 0.97; //delay multiplies with this (reduces) every 'SPEEDUP_SCORE_STEP' scores.
    private boolean resume;
    private final Runnable forward_run = () -> {
        forwardGrid();
        if(resume)
            startTimer();
    };
    /*Pause fields*/
    private Button pause_resume_btn;
    private RelativeLayout pause_layout;
    private final View.OnClickListener pause_resume_btn_handler = v -> {
        pause_layout.setVisibility(View.INVISIBLE);
        enableControls();
        enablePausebtn();
        startTimer();
    };
    private int back_clicks_count = 0;
    private final Handler handler_back = new Handler();
    private final int DOUBLE_CLICK_BACK_DELAY = 1000;
    /*Game Over*/
    private final int GAMEOVER_DELAY = 2000;
    private String speed_setting, controlsType_setting;
    private Bundle bundle;


    /*-----------------Android time circle funtions-----------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        fixBackground();
        findViews();

        rows = game_grid.length;
        cols = game_grid[0].length;
        rand = new Random();
        random_row0 = false;
        delay = base_delay;
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        coin_mp = MediaPlayer.create(this, R.raw.coin);
        colision_mp = MediaPlayer.create(this, R.raw.crash);
        bundle = getIntent().getBundleExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE);
        speed_setting = bundle.getString(MSP_Manager.SPEED_KEY); //INSTEAD OF MSP.getMe().getString(MSP_Manager.SPEED_KEY,MSP_Manager.LOW_VAL);
        controlsType_setting = bundle.getString(MSP_Manager.CONTROL_TYPE_KEY); //INSTEAD OF MSP.getMe().getString(MSP_Manager.CONTROL_TYPE_KEY,MSP_Manager.BUTTONS_VAL);
        score = 0;
        distance = 0;

        initViews();
        enablePausebtn();
        game_layout.setVisibility(View.VISIBLE);
        pause_layout.setVisibility(View.INVISIBLE);
        startTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        back_clicks_count = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseGame();
    }

    //Find all views
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
        game_odometer_label = findViewById(R.id.game_odometer_label);

        //Pause
        pause_resume_btn = findViewById(R.id.pause_resume_btn);
        pause_layout = findViewById(R.id.pause_layout);
    }

    //fix background bitmap - prevents errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.game_background).into((ImageView)findViewById(R.id.game_back));
    }


    /*-----------------Timer-----------------*/
    //Continues the game.
    private void startTimer(){
        resume = true;
        delay = (int) (base_delay *  Math.pow(DELAY_REDUCE_FACTOR, (double)score/SPEEDUP_SCORE_STEP+1));
        handler.postDelayed(forward_run, delay);
    }

    //Freezes the game.
    private void stopTimer(){
        resume = false;
        handler.removeCallbacks(forward_run);
    }


    /*-----------------Setup and Run Game-----------------*/
    //Init Views
    private void initViews(){
        //init pause_resume_btn
        pause_resume_btn.setOnClickListener(pause_resume_btn_handler);

        //init Grid
        randomRow0();
        for (int i = 1; i < rows; i++)
            setRowInvisible(i);
        car_col = cols/2;
        game_grid[rows-1][car_col].setImageResource(R.drawable.ic_car);
        game_grid[rows-1][car_col].setVisibility(View.VISIBLE);

        //init Hearts
        for (ImageView heart : game_hearts)
            heart.setVisibility(View.VISIBLE);

        //init Score
        addScore(0);

        //init Game Speed
        if(speed_setting.compareTo(MSP_Manager.LOW_VAL)==0)
            base_delay = (int) ( LOW_SPEED_FACTOR * base_delay);
        else
            base_delay = (int) (HIGH_SPEED_FACTOR * base_delay);

        //init Game Controls
        if(controlsType_setting.compareTo(MSP_Manager.BUTTONS_VAL)==0)
            fragmentControls = new Fregment_Buttons();
        else
            fragmentControls = new Fregment_Accelometer();

        fragmentControls.setActivity(this);
        fragmentControls.setControllCallBack(controllCallBack);
        getSupportFragmentManager().beginTransaction().add(R.id.controls_frame, fragmentControls).commit();
    }

    //Move car left(negative direction) or right(positive direction).
    private void move(int direction){
        game_grid[rows-1][car_col].setVisibility(View.INVISIBLE);
        car_col = (car_col+cols+direction)%cols;
        if(game_grid[rows-1][car_col].getVisibility()==View.VISIBLE){
            Integer tag = (Integer)(game_grid[rows-1][car_col].getTag());
            if(tag!=null && tag == R.drawable.ic_rock)
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

    public void updateOdometer() {
        distance++;
        String odometer_str = "";
        if(distance>1000) {
            odometer_str += ((float) distance) / 1000;
            odometer_str += "k";
        }
        else{
            odometer_str+= distance;
        }
        odometer_str+="m";
        game_odometer_label.setText(odometer_str);
    }

    //Move game_grid forward.
    private void forwardGrid(){
        updateOdometer();

        for (int i = rows-1; i>0; i--)
            copyRow(i-1,i);
        if(random_row0)
            randomRow0();
        else
            setRowInvisible(0);
        random_row0=!random_row0;

        if(game_grid[rows-1][car_col].getVisibility()==View.VISIBLE){
            Integer tag = (Integer)(game_grid[rows-1][car_col].getTag());
            if(tag!=null && tag == R.drawable.ic_rock)
                performCollision();
            else
                performCoinPickUp();
        }
        else {
            move(0);
            addScore(FORWARD_SCORE_ADD);
        }
    }

    //Copy row data from from_row to to_row in game_grid.
    private void copyRow(int from_row, int to_row){
        for (int j = 0; j < cols; j++) {
            game_grid[to_row][j].setVisibility(game_grid[from_row][j].getVisibility());
            Integer tag = (Integer)(game_grid[from_row][j].getTag());
            game_grid[to_row][j].setTag(tag);
            if(tag!=null && tag == R.drawable.ic_coin)
                game_grid[to_row][j].setImageResource(R.drawable.ic_coin);
            else
                game_grid[to_row][j].setImageResource(R.drawable.ic_rock);
        }
    }

    //Randomize row 0 (car row) in game_grid.
    private void randomRow0(){
        setRowInvisible(0);
        int rand_col = rand.nextInt(cols);
        boolean is_coin = rand.nextInt(10) > 6;
        game_grid[0][rand_col].setVisibility(View.VISIBLE);
        if(is_coin) {
            game_grid[0][rand_col].setImageResource(R.drawable.ic_coin);
            game_grid[0][rand_col].setTag(R.drawable.ic_coin);
        }
        else{
            game_grid[0][rand_col].setImageResource(R.drawable.ic_rock);
            game_grid[0][rand_col].setTag(R.drawable.ic_rock);
        }
    }

    private void setRowInvisible(int row){
        for (int j = 0; j < cols; j++)
            game_grid[row][j].setVisibility(View.INVISIBLE);
    }

    //What happens when the car collides with a rock.
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
        //How much scores will be reduced by a collision with a rock
        int COLLISION_SCORE_SUB = 1000;
        addScore(-COLLISION_SCORE_SUB);
        //gameover
        if(game_hearts[0].getVisibility()==View.INVISIBLE)
            gameOver();
    }

    //What happens when the car meets a coin.
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


    /*-----------------Buttons and Controlls-----------------*/
    private void enableControls(){
        fragmentControls.enable();
    }

    private void disableControls(){
        fragmentControls.disable();
    }

    private void enablePausebtn() {
        game_btn_pause.setOnClickListener(v -> {
            pauseGame();
            vibrate(GAME_BTN_VIBE_TIME);
        });
    }

    private void disablePausebtn(){
        game_btn_pause.setOnClickListener(null);
    }

    //What happens when 'back' android integrated button is pushed.
    //1 push will bring user to 'pause' screen.
    //another double click will close the app.
    @Override
    public void onBackPressed() {
        if(resume){
            pauseGame();
        }
        else{
            if(back_clicks_count==0){
                handler_back.postDelayed(() -> {
                    back_clicks_count = 0;
                }, DOUBLE_CLICK_BACK_DELAY);
                Toast.makeText(Activity_Game.this, "Push 'back' again for EXIT.", Toast.LENGTH_SHORT).show();
                back_clicks_count++;
            }
            else if(back_clicks_count==1){
                finish();
            }
        }
    }


    /*-----------------Pause\GameOver-----------------*/
    //Stop game and show pause screen(layout).
    private void pauseGame(){
        if(resume) {
            stopTimer();
            disableControls();
            disablePausebtn();
            pause_layout.setVisibility(View.VISIBLE);
        }
    }

    //What happens when user loses.
    private void gameOver(){
        stopTimer();
        disableControls();
        disablePausebtn();
        handler.postDelayed(()->{
            Intent myIntent = new Intent(this, Activity_GameOver.class);
            bundle.putInt(Activity_GameOver.SCORE_KEY, score);
            myIntent.putExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE, bundle);
            startActivity(myIntent);
            finish();
        }, GAMEOVER_DELAY);
    }
}