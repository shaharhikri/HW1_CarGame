package com.example.hw1_cargame.control_fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class Fragment_Controls extends Fragment {

    public Fragment_Controls(){ }

    public abstract void setActivity(AppCompatActivity activity);
    public abstract void setControllCallBack(CallBack_Controll cb);
    public abstract void enable();
    public abstract void disable();
}
