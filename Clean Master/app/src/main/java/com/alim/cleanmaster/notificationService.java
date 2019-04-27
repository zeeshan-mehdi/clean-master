package com.alim.cleanmaster;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class notificationService extends Service {
    int lastPercentage=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    static float lastTemp=0.0f;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stoptimertask();
        super.onDestroy();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 600000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                try {
                    Home.calculateRam();
                    int percentage = Home.percentag;
                    if (lastPercentage == 100) {
                        lastPercentage -= 40;
                    }
                    if (percentage <= 50 && percentage >= (lastPercentage + 10)) {
                        MyNotification notification = new MyNotification();
                        notification.sendNotification(getApplicationContext(), "Ram is being used more then " + (100 - percentage) + "%", "Boost Device Now to Speed Up", 0);
                        lastPercentage = percentage;
                    }
                    float temp = CpuCoolerActivity.cpuTemperature();
                    if (lastTemp == 100.0f) {
                        lastTemp -= 20;
                    }
                    if (temp >= 80 && temp >= (lastTemp + 5)) {
                        MyNotification notification = new MyNotification();
                        notification.sendNotification(getApplicationContext(), "Cpu Temperature is Getting high more then " + (100 - temp) + "%", "Cool Now", 1);
                        lastTemp = temp;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
