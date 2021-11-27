package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import com.example.hw1_cargame.msp_objects.MSP_Manager;

public class Activity_Splash extends AppCompatActivity {

    private final int ANIM_DURATION = 1200;
    private ImageView splash_IMG_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViews();
        showViewSlideUpAnimation();
    }

    private void findViews() {
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
    }

    public void showViewSlideUpAnimation() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        splash_IMG_logo.setY((float)height / 2);
        splash_IMG_logo.setScaleY(0.0f);
        splash_IMG_logo.setScaleX(0.0f);
        splash_IMG_logo.animate()
                .scaleY(1.0f).scaleX(1.0f).translationY(0)
                .setDuration(ANIM_DURATION).setInterpolator(new AccelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) { }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        openGame();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) { }

                    @Override
                    public void onAnimationRepeat(Animator animation) { }
                });
    }


    private void openGame(){
        MSP_Manager msp_manager = new MSP_Manager();
        msp_manager.save_ScoreDB_changes();
        msp_manager.create_SpeedValue_IfDoesntExist();
        msp_manager.create_ControlsType_IfDoesntExist();

        Intent myIntent = new Intent(this, Activity_Entry.class);

        Bundle bundle = new Bundle();
        String speed_setting = msp_manager.getSpeed();
        String controlsType_setting = msp_manager.getControlsType();
        bundle.putString(MSP_Manager.SPEED_KEY,speed_setting);
        bundle.putString(MSP_Manager.CONTROL_TYPE_KEY,controlsType_setting);
        bundle.putInt(Activity_GameOver.SCORE_KEY,-1);
        myIntent.putExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE, bundle);

        startActivity(myIntent);
        finish();
    }
}