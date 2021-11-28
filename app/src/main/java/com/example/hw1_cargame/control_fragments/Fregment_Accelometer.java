package com.example.hw1_cargame.control_fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hw1_cargame.R;

public class Fregment_Accelometer extends Fragment_Controls {
    private CallBack_Controll controllCallBack;
    private SensorManager sensorManager;
    private Sensor accSensor;
    private AppCompatActivity activity;
    private SensorEventListener accSensorEventListener;
    private final float NO_TILT_LIMIT = 1.8f; //NO_TILT_LIMIT higher -> lower sensor sensitivity

    public Fregment_Accelometer(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelometer, container, false);
        initSensor();

        accSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                if(x<=-NO_TILT_LIMIT)
                    controllCallBack.moveRight();
                else if(x>=NO_TILT_LIMIT)
                    controllCallBack.moveLeft();

                float y = event.values[1];
                if(y<=-NO_TILT_LIMIT)
                    controllCallBack.speedUp();
                else if(y>=NO_TILT_LIMIT)
                    controllCallBack.speedDown();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        enable();
        return view;
    }

    private void initSensor() {
        sensorManager = (SensorManager) (activity.getSystemService(Context.SENSOR_SERVICE));
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setControllCallBack(CallBack_Controll cb){
        controllCallBack = cb;
    }


    @Override
    public void enable(){
        sensorManager.registerListener(accSensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void disable(){
        sensorManager.unregisterListener(accSensorEventListener);
    }
}
