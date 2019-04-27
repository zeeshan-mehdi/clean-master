package com.alim.cleanmaster;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alim.cleanmaster.Services.FlagService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.text.DecimalFormat;


public class Boosted extends AppCompatActivity {
    ImageView cleanedImage;
    TextView cleaned;
    public static int boostFlagService=5;
    //Ads
    InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private AdView mAdView2;
    private String sizeUnit= "KB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        setContentView(R.layout.activity_boosted);
        cleaned = findViewById(R.id.txtRamCleared);
        cleanedImage = findViewById(R.id.imgCleaned);

        //ads
        mInterstitialAd = new InterstitialAd(this);


        MobileAds.initialize(getApplicationContext(),"ca-app-pub-2958463332087114~5829629490");
        mInterstitialAd.setAdUnitId("ca-app-pub-2958463332087114/7532519946");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if(BoostDevice.ramAvailableToClean>0&&getIntent().getBooleanExtra("animate",true)) {
            animateTextView(0, (int) (BoostDevice.ramAvailableToClean/1024f), cleaned);
            boostFlagService = 5;
            if(!isMyServiceRunning(FlagService.class)){
                startService(new Intent(this,FlagService.class));
            }
          //  colorChangeAnimation();
        }
        else {
            cleanedImage.setVisibility(View.VISIBLE);
            cleaned.setText("Device already Optimized");
        }

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
        if(BoostDevice.flag) {
            Intent intent = new Intent(getApplicationContext(), BoostDevice.class);
            intent.putExtra("flag",false);
            startActivity(intent);
        }
        else
            startActivity(new Intent(getApplicationContext(),Home.class));
        finish();
    }
    public void animateTextView(int initialValue, final int finalValue, final TextView  textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(1500);
        convertSize(finalValue);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                switch (sizeUnit) {
                    case "GB":
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue()).toString() + "TB");
                        break;
                    case "MB":
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue()).toString() + "GB");
                        break;
                    case "KB":
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue()).toString() + "MB");
                        break;
                    case "B":
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue()).toString() + "KB");
                        break;
                    default:
                        textview.setText(convertSize((int)valueAnimator.getAnimatedValue()).toString() + "KB");
                        break;
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

        cleanedImage.setVisibility(View.VISIBLE);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    // Stopping animation:- stop the animation on onPause.
    @Override
    protected void onPause() {
        super.onPause();
    }
    public String convertSize(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.0");
            float k = cacheSize / 1024;
            float m = k / 1024;
            float g = m / 1024;
            float t = g / 1024;

            if (t >= 1) {
                sizeUnit = "TB";
                return dec.format(t);
            } else if (g >= 1) {
                sizeUnit = "GB";
                return dec.format(g);
            } else if (m >= 1) {
                sizeUnit = "MB";
                return dec.format(m);
            } else if (k >= 1) {
                sizeUnit = "KB";
                return dec.format(k);
            } else {
                sizeUnit = "B";
                return dec.format(cacheSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
