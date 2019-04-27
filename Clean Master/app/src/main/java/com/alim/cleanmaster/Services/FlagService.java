package com.alim.cleanmaster.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alim.cleanmaster.CleanedActivity;
import com.alim.cleanmaster.Home;
import com.alim.cleanmaster.MainActivity;
import com.alim.cleanmaster.SplashScreen;
import com.alim.cleanmaster.reciever.Alarm;

import java.util.Timer;
import java.util.TimerTask;

import static com.alim.cleanmaster.Boosted.boostFlagService;
import static com.alim.cleanmaster.CleanedActivity.cleanFlagService;
import static com.alim.cleanmaster.CpuCooled.coolFlagService;

public class FlagService extends Service {

    Alarm alarm = new Alarm();
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        alarm.setAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        alarm.setAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
