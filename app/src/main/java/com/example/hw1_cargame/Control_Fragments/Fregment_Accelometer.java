package com.example.hw1_cargame.Control_Fragments;

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
    private ControllCallBack controllCallBack;
    private SensorManager sensorManager;
    private Sensor accSensor;
    private AppCompatActivity activity;
    private SensorEventListener accSensorEventListener;

    public Fregment_Accelometer(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accelometer, container, false);
        sensorManager = (SensorManager) (activity.getSystemService(Context.SENSOR_SERVICE));
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        accSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float limit = 1.5f;
                if(x<=-limit)
                    controllCallBack.moveRight();
                else if(x>=limit)
                    controllCallBack.moveLeft();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        enable();
        return view;
    }

//    private void initSensor() {
//
//    }

    @Override
    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setControllCallBack(ControllCallBack cb){
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
