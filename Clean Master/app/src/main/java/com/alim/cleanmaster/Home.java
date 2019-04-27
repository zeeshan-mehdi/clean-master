package com.alim.cleanmaster;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import eu.faircode.netguard.ActivityMain;

public class Home extends AppCompatActivity implements View.OnClickListener {
    TextView txtInternalPercentage;
    TextView txtRamPercentage;
    ConstraintLayout images;
    ConstraintLayout apps;
    ConstraintLayout boost;
    ConstraintLayout notification;
    ConstraintLayout junk;
    Button search;
    ConstraintLayout netGuardFirewall;
    ProgressBar totalfreeRam;
    ProgressBar totalfreeInternalMemory;

    TextView txtJunkAlreadyFound;

    LinearLayout mediaRow;
    LinearLayout junkRow;
    LinearLayout cpuRow;


    /************************************************************/
    //variables
    static long freeRam;
    static long totalRam;
    long usedRam;
    int height = 0;

    long totalInternalMemory;
    long usedInternalMemory;
    long freeInternalMemory;


    long totalExternalMemory;
    long usedExternalMemory;
    long freeExternalMemory;

    static int percentag;

    static boolean isCleaned = false;

    static final int PERMISSION_REQUEST_CODE = 0;
    static Context context;

    public static Boolean SCAN_FLAG = false;
    public static Boolean SCAN_FLAG2 =false;

    //tasks

    static ArrayList<Task> tasks;
    static ArrayList<Task> tasks2;

    public static boolean cleanFlag = false;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            images = findViewById(R.id.imgImages);
            apps = findViewById(R.id.imgAppLock);
            boost = findViewById(R.id.imgBoost);
            notification = findViewById(R.id.imgCpuCooler);
            totalfreeRam = findViewById(R.id.totalRam);
            totalfreeInternalMemory = findViewById(R.id.internalFreeMem);
            netGuardFirewall = findViewById(R.id.imgFirewall);
            junk = findViewById(R.id.imgJunk);
            progressBar = findViewById(R.id.homeProgressBar);

            Sprite doubleBounce = new Wave();
            doubleBounce.setColor(getResources().getColor(R.color.defaultGreen2));
            progressBar.setIndeterminateDrawable(doubleBounce);

            txtJunkAlreadyFound = findViewById(R.id.txtFoundJunk2);

            txtInternalPercentage = findViewById(R.id.txtMemory);
            txtRamPercentage = findViewById(R.id.txtRam);

            mediaRow = findViewById(R.id.firstRow);
            cpuRow = findViewById(R.id.secondRow);
            junkRow = findViewById(R.id.threeRow);

            search = findViewById(R.id.scanButton);
            tasks = new ArrayList<>();
            tasks2 = new ArrayList<>();

            context = getApplicationContext();

            setTitle("Clean Master");

            /***************************************************************/

            final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

            // Use bounce interpolator with amplitude 0.2 and frequency 20
            MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
            myAnim.setInterpolator(interpolator);


            search.startAnimation(myAnim);
            //listeners

            PushDownAnim.setPushDownAnimTo(images);

            images.setOnClickListener(this);
            images.startAnimation(myAnim);

            PushDownAnim.setPushDownAnimTo(apps);

            apps.setOnClickListener(this);
            apps.startAnimation(myAnim);

            PushDownAnim.setPushDownAnimTo(boost);

            boost.setOnClickListener(this);
            boost.startAnimation(myAnim);

            PushDownAnim.setPushDownAnimTo(notification);

            notification.setOnClickListener(this);
            notification.startAnimation(myAnim);

            PushDownAnim.setPushDownAnimTo(netGuardFirewall);

            netGuardFirewall.setOnClickListener(this);
            netGuardFirewall.startAnimation(myAnim);

            PushDownAnim.setPushDownAnimTo(junk);

            junk.setOnClickListener(this);
            junk.startAnimation(myAnim);

            PushDownAnim.setPushDownAnimTo(mediaRow);
            mediaRow.setOnClickListener(this);

            PushDownAnim.setPushDownAnimTo(cpuRow);
            cpuRow.setOnClickListener(this);
            PushDownAnim.setPushDownAnimTo(junkRow);
            junkRow.setOnClickListener(this);


            //

            PushDownAnim.setPushDownAnimTo(search);

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startJunkActivity(v);
                }
            });

            /**************************************************************/
            //functions

            //Ram

            freeRam = freeRamMemorySize();
            totalRam = totalRamMemorySize();
            usedRam = totalRam - freeRam;

            double res = (freeRam / (float) totalRam) * 100;
            percentag = (int) res;

            totalfreeRam.setMax(100);
            totalfreeRam.setProgress(percentag);
            txtRamPercentage.setText(Integer.toString(percentag) + "%");

            //External Memory
            freeExternalMemory = getAvailableExternalMemorySize();

            if(SplashScreen.totalDataSize+SplashScreen.totalCacheSize>0&&Home.cleanFlag){
                String result = MainActivity.convert(SplashScreen.totalDataSize+SplashScreen.totalCacheSize);
                txtJunkAlreadyFound.setText("junk found " +result );
            }else if(!Home.cleanFlag){
                txtJunkAlreadyFound.setText("No junk found ");
            }
            else
                txtJunkAlreadyFound.setText("total estimated junk found " + MainActivity.convert(freeExternalMemory));


            totalExternalMemory = getTotalExternalMemorySize();
            usedExternalMemory = totalExternalMemory - freeExternalMemory;
            System.out.println("INTERNAL AND EXTERNAL STORAGE FREE AND TOTOL : " + freeExternalMemory + " " + totalExternalMemory);
            percentag = 0;
            res = (freeExternalMemory / (float) totalExternalMemory) * 100;
            percentag = (int) res;



            percentag = 0;
            //Internal Memory
            freeInternalMemory = getAvailableInternalMemorySize();
            totalInternalMemory = getTotalInternalMemorySize();
            usedInternalMemory = totalInternalMemory - freeInternalMemory;

            res = (freeInternalMemory / (float) totalInternalMemory) * 100;
            percentag = (int) res;

            totalfreeInternalMemory.setMax(100);
            totalfreeInternalMemory.setProgress(percentag);
            txtInternalPercentage.setText(Integer.toString(percentag)+"%");


            //permissions
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    //permission granted
                } else {
                    requestPermission(); // Code for permission
                }
            }
            if (!isMyServiceRunning(notificationService.class)) {
                startService(new Intent(getApplicationContext(), notificationService.class));
            }

           //found junk



        } catch (Exception e) {
            e.printStackTrace();
            Messages.sendMessage(getApplicationContext(),"Exception");
        }
    }

    public void startJunkActivity(View view) {
        if (cleanFlag) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), CleanedActivity.class);
            intent.putExtra("total", 0);
            intent.putExtra("flag", true);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        progressBar.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        try {
            int id = v.getId();
            progressBar.setVisibility(View.VISIBLE);
            switch (id) {
                case R.id.imgImages: {
                    Intent intent = new Intent(this, MediaActivity.class);
                    startActivity(intent);
                    break;
                }

                case R.id.imgAppLock: {
                    LockService.homeContext = this;
                    if (!isMyServiceRunning(LockService.class))
                        startService(new Intent(this, LockService.class));


                    Intent intent = new Intent(this, SetPattern.class);
                    intent.putExtra("flag", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);

                    break;
                }
                case R.id.imgBoost: {
                    Intent intent = new Intent(this, BoostDevice.class);
                    intent.putExtra("flag", SCAN_FLAG);
                    startActivity(intent);
                    break;
                }
                case R.id.imgCpuCooler: {
                    Intent intent = new Intent(this, CpuCoolerActivity.class);
                    intent.putExtra("flag", SCAN_FLAG2);
                    startActivity(intent);
                    break;
                }
                case R.id.imgFirewall: {
                    Intent intent = new Intent(this, ActivityMain.class);
                    startActivity(intent);
                    break;
                }
                case R.id.imgJunk: {
                    if (cleanFlag) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CleanedActivity.class);
                        intent.putExtra("total", 0);
                        intent.putExtra("flag", true);
                        startActivity(intent);
                    }

                    break;
                }
                case R.id.firstRow: {
                    Intent intent = new Intent(this, MediaActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.secondRow: {
                    Intent intent = new Intent(this, CpuCoolerActivity.class);
                    intent.putExtra("flag", SCAN_FLAG2);
                    startActivity(intent);
                    break;
                }
                case R.id.threeRow: {
                    if (cleanFlag) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CleanedActivity.class);
                        intent.putExtra("total", 0);
                        intent.putExtra("flag", true);
                        startActivity(intent);
                    }
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Messages.sendMessage(getApplicationContext(), e.toString());
        }
    }

    public static void calculateRam() {
        try {
            freeRam = freeRamMemorySize();
            totalRam = totalRamMemorySize();

            double res = (freeRam / (float) totalRam) * 100;
            percentag = (int) res;
        } catch (Exception e) {

        }
    }

    private static long freeRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        return availableMegs;
    }

    public static Activity getInstance() {
        return Home.getInstance();
    }

    private static long totalRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.totalMem / 1048576L;
        return availableMegs;
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = " KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = " MB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    private String returnToDecimalPlaces(long values) {
        DecimalFormat df = new DecimalFormat("#");
        String angleFormated = df.format(values);
        return angleFormated;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Home.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                    Messages.sendMessage(getApplicationContext(), "Please allow these permissions these are necessary");
                    requestPermission();
                }
                break;
        }
    }

    public static long convert(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.0");
            long k = cacheSize / 1024;
            long m = k / 1024;
            long g = m / 1024;
            long t = g / 1024;
            if (m > 1) {
                return m;
            } else if (k > 1) {
                return k;
            } else {
                return cacheSize;
            }
        } catch (Exception e) {
            //ignore;
        }
        return 0;

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

    public void updateScanFlag() {
        // Some time when you want to run
        Date when = new Date(System.currentTimeMillis());

        try {
            Intent someIntent = new Intent(this, UpdateFlags.class); // intent to be launched

            // note this could be getActivity if you want to launch an activity
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0, // id, optional
                    someIntent, // intent to launch
                    PendingIntent.FLAG_CANCEL_CURRENT); // PendintIntent flag

            AlarmManager alarms = (AlarmManager) context.getSystemService(
                    Context.ALARM_SERVICE);

            alarms.setRepeating(AlarmManager.RTC_WAKEUP,
                    when.getTime(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getScreenSize() {
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            height = metrics.heightPixels;
//            //1080 * 1776
//
//            //480 800
            return width;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onResume() {
        if(isCleaned){
            txtJunkAlreadyFound.setText("No junk Found");
        }
        super.onResume();
    }

}
