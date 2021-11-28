package com.example.hw1_cargame.control_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hw1_cargame.R;

public class Fregment_Buttons extends Fragment_Controls {

    private ImageButton game_left_btn, game_right_btn;
    private CallBack_Controll controllCallBack;
    private CallBack_Controll enabledCallBack;
    private CallBack_Controll disabledCallBack = new CallBack_Controll() {
        @Override
        public void moveLeft() { }
        @Override
        public void moveRight() { }

        @Override
        public void speedUp() { }

        @Override
        public void speedDown() { }
    };
    private AppCompatActivity activity;

    public Fregment_Buttons(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buttons, container, false);
        findViews(view);
        initViews();
        enable();
        return view;
    }

    private void findViews(View view) {
        game_left_btn = view.findViewById(R.id.game_btn_left);
        game_right_btn = view.findViewById(R.id.game_btn_right);
    }

    @Override
    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setControllCallBack(CallBack_Controll cb){
        enabledCallBack = cb;
    }

    private void initViews() {
        game_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllCallBack.moveLeft();
            }
        });
        game_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllCallBack.moveRight();
            }
        });
    }

    @Override
    public void enable(){
        controllCallBack = enabledCallBack;
    }

    @Override
    public void disable(){
        controllCallBack = disabledCallBack;
    }

}
