package com.alim.cleanmaster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alim.cleanmaster.Home;

import com.alim.cleanmaster.*;
public class UpdateFlags extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Home.SCAN_FLAG=true;
    }
}
