package com.example.hw1_cargame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.example.hw1_cargame.R;
import com.example.hw1_cargame.msp_objects.MSP_Manager;

public class Activity_Settings extends AppCompatActivity {
    private String controlsType_setting;
    private String speed_setting;

    private RadioGroup rb_controls;
    private RadioGroup rb_speed;
    private Button exitBtn;
    private Bundle bundle;
    private MSP_Manager msp_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fixBackground();
        findViews();
        
        bundle = getIntent().getBundleExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE);
        msp_manager = new MSP_Manager();

        getDataFromBundle();
        initViews();
    }

    private void getDataFromBundle(){
        speed_setting = bundle.getString(MSP_Manager.SPEED_KEY);
        controlsType_setting = bundle.getString(MSP_Manager.CONTROL_TYPE_KEY);
    }

    private void initViews() {
        int id;
        rb_controls.clearCheck();
        if(controlsType_setting.compareTo(MSP_Manager.BUTTONS_VAL)==0)
            id = R.id.radio_buttons;
        else
            id = R.id.radio_accelometer;
        rb_controls.check(id);

        rb_speed.clearCheck();
        if(speed_setting.compareTo(MSP_Manager.LOW_VAL)==0)
            id = R.id.radio_low;
        else
            id = R.id.radio_high;
        rb_speed.check(id);

        rb_controls.setOnCheckedChangeListener( (v,e) -> {
            int selection_id = rb_controls.getCheckedRadioButtonId();
            if(selection_id==R.id.radio_buttons)
                controlsType_setting = MSP_Manager.BUTTONS_VAL;
            else if(selection_id==R.id.radio_accelometer)
                controlsType_setting = MSP_Manager.ACCELOMETER_VAL;
        });

        rb_speed.setOnCheckedChangeListener( (v,e) -> {
            int selection_id = rb_speed.getCheckedRadioButtonId();
            if(selection_id==R.id.radio_low)
                speed_setting = MSP_Manager.LOW_VAL;
            else if(selection_id==R.id.radio_high)
                speed_setting = MSP_Manager.HIGH_VAL;
        });

        exitBtn.setOnClickListener( v -> {
            updateMSP();
            gotoActivityEntry();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        updateMSP();
        gotoActivityEntry();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateMSP();
    }

    private void updateMSP() {
        msp_manager.updateSpeed(speed_setting);
        msp_manager.updateControlsType(controlsType_setting);
    }

    private void findViews() {
        rb_speed = findViewById(R.id.radiobuttons_speed);
        rb_controls = findViewById(R.id.radiobuttons_controls);
        exitBtn = findViewById(R.id.settings_exit_btn);
    }

    //Prevent errors in old phones
    private void fixBackground(){
        Glide.with(this).load(R.drawable.settings_background).into((ImageView)findViewById(R.id.settings_back));
    }

    private void gotoActivityEntry(){
        Intent myIntent = new Intent(this, Activity_Entry.class);

        Bundle bundle = new Bundle();

        bundle.putString(MSP_Manager.SPEED_KEY,speed_setting);
        bundle.putString(MSP_Manager.CONTROL_TYPE_KEY,controlsType_setting);

        myIntent.putExtra(Activity_GameOver.SPEED_AND_CONTROL_AND_SCORE_BUNDLE, bundle);
        startActivity(myIntent);
        finish();
    }
}