package com.example.hw1_cargame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

public class Activity_Settings extends AppCompatActivity {
    private String controlsType;
    private String speed;

    private RadioGroup rb_controls;
    private RadioGroup rb_speed;

    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fixBackground();
        findViews();
        getDataFromMSP();
        initViews();
    }

    private void getDataFromMSP(){
        speed = MSP.getMe().getString(Activity_Game.SPEED_KEY,Activity_Game.LOW_VAL);
        controlsType= MSP.getMe().getString(Activity_Game.CONTROL_TYPE_KEY,Activity_Game.BUTTONS_VAL);
    }

    private void initViews() {
        Log.d("SHAHARMSP2", speed+" "+controlsType);
        int id;
        rb_controls.clearCheck();
        if(controlsType.compareTo(Activity_Game.BUTTONS_VAL)==0)
            id = R.id.radio_buttons;
        else
            id = R.id.radio_accelometer;
        rb_controls.check(id);

        rb_speed.clearCheck();
        if(speed.compareTo(Activity_Game.LOW_VAL)==0)
            id = R.id.radio_low;
        else
            id = R.id.radio_high;
        rb_speed.check(id);

        rb_controls.setOnCheckedChangeListener( (v,e) -> {
            int selection_id = rb_controls.getCheckedRadioButtonId();
            if(selection_id==R.id.radio_buttons)
                controlsType = Activity_Game.BUTTONS_VAL;
            else if(selection_id==R.id.radio_accelometer)
                controlsType = Activity_Game.ACCELOMETER_VAL;
        });

        rb_speed.setOnCheckedChangeListener( (v,e) -> {
            int selection_id = rb_speed.getCheckedRadioButtonId();
            if(selection_id==R.id.radio_low)
                speed = Activity_Game.LOW_VAL;
            else if(selection_id==R.id.radio_high)
                speed = Activity_Game.HIGH_VAL;
        });

        exitBtn.setOnClickListener( v -> {
            updateMSP();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        updateMSP();
        finish();
    }

    private void updateMSP() {
        MSP.getMe().putString(Activity_Game.SPEED_KEY, speed);
        MSP.getMe().putString(Activity_Game.CONTROL_TYPE_KEY, controlsType);
        Log.d("SHAHARMSP3", speed+" "+controlsType);
    }

    private void findViews() {
        rb_speed = (RadioGroup) findViewById(R.id.radiobuttons_speed);
        rb_controls = (RadioGroup) findViewById(R.id.radiobuttons_controls);
        exitBtn = findViewById(R.id.settings_exit_btn);
    }

    //Prevent errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.settings_background).into((ImageView)findViewById(R.id.settings_back));
    }
}