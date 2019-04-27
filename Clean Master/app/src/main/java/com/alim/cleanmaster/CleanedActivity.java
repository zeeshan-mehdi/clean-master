package com.alim.cleanmaster;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alim.cleanmaster.Services.FlagService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.text.DecimalFormat;


public class CleanedActivity extends AppCompatActivity {

    TextView totalCleanedText;
    TextView textView;
    ConstraintLayout background;
    public static int cleanFlagService=5;
    String sizeUnit;
    static boolean comingBack= false;
    //Ads
    InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private AdView mAdView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaned);
        totalCleanedText= findViewById(R.id.totalCleaned);
        background= findViewById(R.id.cleanedBackground);
        long totalCleaned =(long) getIntent().getLongExtra("total",0);
        if(getIntent().getBooleanExtra("flag",false)) {
            totalCleanedText.setText(R.string.cleaned);
            Home.isCleaned = true;
        }
        else {
            Home.isCleaned = true;
            CleanedActivity.comingBack = true;

            SharedPreferences sharedPreferences = getSharedPreferences("junk_size",MODE_PRIVATE);
            long dataSize = sharedPreferences.getLong("cacheSize",10);
            long cacheSize = sharedPreferences.getLong("dataSize",10);
            totalCleaned = cacheSize+dataSize;

            totalCleanedText.setText(MainActivity.convert(totalCleaned));
            animateTextView(0,(int)(totalCleaned/1024),totalCleanedText);
            cleanFlagService = 5;

            if(!isMyServiceRunning(FlagService.class)){
                startService(new Intent(this,FlagService.class));
            }
        }

        //ads
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-2958463332087114~5829629490");
        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("ca-app-pub-2958463332087114/7532519946");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);


    }

    @Override
    public void onBackPressed() {
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        super.onBackPressed();
    }

    public void animateTextView(int initialValue, final int finalValue, final TextView  textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    convertSize((int)valueAnimator.getAnimatedValue());

                    if(sizeUnit.equals("GB")){
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue())+"TB");
                    }else if(sizeUnit.equals("MB")){
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue())+"GB");
                    }
                    else if(sizeUnit.equals("KB")){
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue())+"MB");
                    }else if(sizeUnit.equals("B")){
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue())+"KB");
                    }else {
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue())+"KB");
                    }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }
    public String convertSize(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.0");
            float k = cacheSize / 1024;
            float m = k / 1024;
            float g = m / 1024;
            float t = g / 1024;
            if (t >= 1) {
                sizeUnit ="TB";
                return dec.format(t);
            } else if (g >= 1) {
                sizeUnit ="GB";
                return dec.format(g);
            } else if (m >= 1) {
                sizeUnit ="MB";
                return dec.format(m);
            } else if (k >= 1) {
                sizeUnit ="KB";
                return dec.format(k);
            } else {
                sizeUnit ="B";
                return dec.format(cacheSize);
            }
        } catch (Exception e) {
            //ignore;
        }
        return "0";

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


}
