package com.alim.cleanmaster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alim.cleanmaster.Services.FlagService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class CpuCooled extends AppCompatActivity {
    TextView txtCooled;

    public static int coolFlagService=5;
    //Ads
    InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private AdView mAdView2;
    ConstraintLayout background;
    ImageView circle;

    ImageView thermometer;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_cooled);
        txtCooled = findViewById(R.id.txtCooled);
        progressBar = findViewById(R.id.progressBar2);
        background = findViewById(R.id.boostedBackground);
        thermometer = findViewById(R.id.imgThremometer);
        circle = findViewById(R.id.imageView5);


        String msg = getIntent().getStringExtra("cool");
        boolean flag = getIntent().getBooleanExtra("flag",false);

        if(flag){
            coolFlagService=5;
            if(!isMyServiceRunning(FlagService.class)){
                startService(new Intent(this,FlagService.class));
            }
            float percentageTemp =( 0.1f*CpuCoolerActivity.cpuTemp);
            animateTextView(0,(int)percentageTemp,txtCooled);
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 80.0f,20.0f);
            anim.setDuration(5000);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            progressBar.startAnimation(anim);
        }else {
            txtCooled.setText("Device " + msg + " Cooled");
            thermometer.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            circle.setVisibility(View.GONE);
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
                MainActivity.convert(finalValue);
                    textview.setText("Temp dropped "+valueAnimator.getAnimatedValue().toString()+" °C");
                    int val = (int) valueAnimator.getAnimatedValue();
            }
        }
        );
        valueAnimator.start();

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
