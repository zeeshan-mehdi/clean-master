package com.alim.cleanmaster;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alim.cleanmaster.Database.DBHelper;
import com.alim.cleanmaster.Database.DatabaseHelper;
import com.alim.cleanmaster.Services.FlagService;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.os.Build.VERSION.SDK;
import static java.lang.Thread.sleep;


public class SplashScreen extends AppCompatActivity {

    private static final String TAG = " ";
    private static final String PACKAGE_NAME = "com.android.vending";
    static String size;
    static String sizeUnit;
    static long totalCacheSize;
    static long totalDataSize;
    static boolean scanned = false;

    static DatabaseHelper databaseHelper;
    static DBHelper dbHelper;

    static ArrayList<JunkInfo> junckInfo;
    static ArrayList<AppDetails.PackageInfoStruct> packageInfo;
    static ArrayList<CacheItem> cacheItems;
    static ArrayList<CacheItem> appDataItems;
    static ArrayList<String> header;

    static boolean shouldLaunch = true;
    ProgressBar splashProgressBar;
    int progress = 0;

    Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        junckInfo = new ArrayList<>();
        packageInfo = new ArrayList<>();
        cacheItems = new ArrayList<>();
        appDataItems = new ArrayList<>();
        header = new ArrayList<>();
        AppLockActivity.context = this;
        splashProgressBar = findViewById(R.id.splashProgressBar);

        if(databaseHelper==null)
            databaseHelper = new DatabaseHelper(this);
        else if(dbHelper==null)
            dbHelper= new DBHelper(this);
        ctx = this;

//        if(!isMyServiceRunning(FlagService.class)){
//            startService(new Intent(SplashScreen.this,FlagService.class));
//        }

        SharedPreferences sharedPreferences = getSharedPreferences("flags",MODE_PRIVATE);
        boolean flag = sharedPreferences.getBoolean("scan_flag",true);
        if(flag) {
            Home.SCAN_FLAG = true;
            Home.SCAN_FLAG2 = true;
            Home.cleanFlag = true;
            sharedPreferences.edit().putBoolean("scan_flag",false).apply();
        }
        if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if (!hasUsageStatsPermission(getApplicationContext())) {
                    startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), 1);
                    Log.e("android 8permission", "not granted");
                }
            }
            waitAndStart();
        }
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    public void waitAndStart() {
        if (!getDataFromDB()) {
            LongOperation longOperation = new LongOperation();
            longOperation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

      } else {
            scanned = true;
            LongOperation longOperation = new LongOperation();
            longOperation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      }

        //.execute();


        AppLockActivity.packageManager = getPackageManager();
        AppLockActivity.context = this;
        new ImagesDataModel.asynchronousLoading((Activity) this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new VideoDataModel.CollectVideosData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getApplicationContext());

        new AppLockActivity.CollectApps().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new SplashScreen.filesThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        final Timer timer = new Timer();
        final Handler handler2 = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("timer", "timer task is running");
//                        if (scanned) {
////                            splashProgressBar.setVisibility(View.GONE);
////                            timer.cancel();
////                        } else
                        splashProgressBar.setProgress(progress);
                        progress += 10;
                    }
                });
            }
        }, 0, 1000);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (hasUsageStatsPermission(getApplicationContext())) {

                        Intent homeIntent = new Intent(ctx, Home.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        startTimerForActivityCall();
                    }
                } else {
                    Intent homeIntent = new Intent(ctx, Home.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                    finish();
                }
            }
        }, 10000);
    }

    public void startTimerForActivityCall() {
        final Handler handler2 = new Handler();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        if (hasUsageStatsPermission(getApplicationContext())) {
                            Intent homeIntent = new Intent(getApplicationContext(), Home.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(homeIntent);
                            finish();
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 1000);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    waitAndStart();
                } else {
                    Toast.makeText(getApplicationContext(), "Wed don't have permissions", Toast.LENGTH_LONG).show();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    waitAndStart();
                } else {
                    Toast.makeText(getApplicationContext(), "Wed don't have permissions", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
            return true;
        }
    }


    private class LongOperation extends AsyncTask<Void, String, Void> {
        int index = 0;
        int size = 0;
        int idCopy = 0;
        String tempString = "";

        @Override
        protected Void doInBackground(Void... voids) {
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    if (!hasUsageStatsPermission(getApplicationContext())) {
                        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), 1);
                        Log.e("android 8permission", "not granted");
                    } else {
                        PackageManager packageManager = getPackageManager();

                        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                        size = packageInfos.size();
                        for (int i = 0; i < packageInfos.size(); i++) {
                            final String PACKAGE_NAME2 = packageInfos.get(i).packageName;
                            getInformation(getApplicationContext());

                            publishProgress(PACKAGE_NAME2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                StorageInformation storageInformation =
                        new StorageInformation(SplashScreen.this);
                Log.e("object", "object created");
                storageInformation.getpackageSize();

                Log.e("object", "storage information accessed");
                loadArray2();
                size = packageInfo.size();
                Log.e("size2", "size2" + Integer.toString(size));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            scanned = true;
            addData();
            Log.e("Cache size : ", "size" + Integer.toString(cacheItems.size()));
            Toast.makeText(getApplicationContext(), "Scan Completed", Toast.LENGTH_LONG).show();
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getInformation(Context context) {
        try {
            totalCacheSize = 0;
            totalDataSize=0;
            final StorageStatsManager storageStatsManager = (StorageStatsManager) getSystemService(Context.STORAGE_STATS_SERVICE);
            final StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            final List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
            final UserHandle user = android.os.Process.myUserHandle();
            for (StorageVolume storageVolume : storageVolumes) {
                final String uuidStr = storageVolume.getUuid();
                final UUID uuid = uuidStr == null ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);
                try {
                    Log.e("cache", "storage:" + uuid + " : " + storageVolume.getDescription(context) + " : " + storageVolume.getState());
                    Log.e("cache", "getFreeBytes:" + Formatter.formatShortFileSize(context, storageStatsManager.getFreeBytes(uuid)));
                    Log.e("cache", "getTotalBytes:" + Formatter.formatShortFileSize(context, storageStatsManager.getTotalBytes(uuid)));
                    Log.e("cache", "storage stats for app of package name:" + PACKAGE_NAME + " : ");

                    final StorageStats storageStats = storageStatsManager.queryStatsForPackage(uuid, PACKAGE_NAME, user);
                    Log.e("cache", "getAppBytes:" + Formatter.formatShortFileSize(context, storageStats.getAppBytes()) +
                            " getCacheBytes:" + Formatter.formatShortFileSize(context, storageStats.getCacheBytes()) +
                            " getDataBytes:" + Formatter.formatShortFileSize(context, storageStats.getDataBytes()));
                    PackageManager pm = getPackageManager();
                    ApplicationInfo ai = pm.getApplicationInfo(PACKAGE_NAME, 0);
                    if (storageStats.getCacheBytes() > 1024)
                        cacheItems.add(new CacheItem(ai.loadLabel(pm).toString(), "Advice Clean ", storageStats.getCacheBytes(), ai.loadIcon(pm), true, PACKAGE_NAME));
                    if (storageStats.getDataBytes() > 1024)
                        appDataItems.add(new CacheItem(ai.loadLabel(pm).toString(), "Advice Clean ", storageStats.getDataBytes(), ai.loadIcon(pm), true, PACKAGE_NAME));
                    totalCacheSize += storageStats.getCacheBytes();
                    totalDataSize += storageStats.getDataBytes();
                    addData();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadArray2() {
        try {
            Log.e("size", "junk info size " + Integer.toString(junckInfo.size()));
            totalDataSize =0;
            totalCacheSize=0;
            // Messages.sendMessage(getApplicationContext(),"junk info size "+Integer.toString(junckInfo.size()));
            for (int i = 0; i < junckInfo.size(); i++) {
                JunkInfo junkItem = junckInfo.get(i);
                AppDetails.PackageInfoStruct itemInfo = packageInfo.get(i);
                if (junkItem.cacheSize > 0) {
                    totalCacheSize += junkItem.cacheSize;
                    String size = convert(junkItem.cacheSize);
                    cacheItems.add(new CacheItem(itemInfo.appname, "Advice : clean", junkItem.cacheSize, itemInfo.icon, true, itemInfo.pname));
                    Log.e("pname",itemInfo.pname);
                }
                if (junkItem.dataSize > 0) {
                    totalDataSize += junkItem.dataSize;
                    String size = convert(junkItem.dataSize);
                    appDataItems.add(new CacheItem(itemInfo.appname, "Advice : clean", junkItem.dataSize, itemInfo.icon, true, itemInfo.pname));
                    Log.e("pname",itemInfo.pname);
                }

            }
            SharedPreferences sharedPreferences = getSharedPreferences("junk_size",MODE_PRIVATE);
            sharedPreferences.edit().clear();
            sharedPreferences.edit().putLong("cacheSize",totalCacheSize).apply();
            sharedPreferences.edit().putLong("dataSize",totalDataSize).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convert(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.0");
            float k = cacheSize / 1024;
            float m = k / 1024;
            float g = m / 1024;
            float t = g / 1024;

            if (t > 1) {
                size = dec.format(t);
                sizeUnit = "TB";
                return size + " TB";
            } else if (g > 1) {
                size = dec.format(g);
                sizeUnit = "GB";
                return size + " GB";
            } else if (m > 1) {
                size = dec.format(m);
                sizeUnit = "MB";
                return size + " MB";
            } else if (k > 1) {
                size = dec.format(k);
                sizeUnit = "KB";
                return size + " KB";
            } else {
                size = dec.format(cacheSize);
                sizeUnit = "B";
                return size + " B";
            }
        } catch (Exception e) {
            //ignore;
        }
        return "0 B";

    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasUsageStatsPermission(Context context) {
        try {
            //http://stackoverflow.com/a/42390614/878126
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                return false;
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
            boolean granted = false;
            if (mode == AppOpsManager.MODE_DEFAULT)
                granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
            else
                granted = (mode == AppOpsManager.MODE_ALLOWED);
            return granted;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    class filesThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            FileClass fileClass = new FileClass(getApplicationContext());
            fileClass.findAllFiles(Environment.getExternalStorageDirectory());

            FilesFragment.paths = fileClass.loadFilesDate(getApplicationContext(), FilesFragment.paths);
            return null;
        }
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    private void addData() {
//        databaseHelper = new DatabaseHelper(this);
        databaseHelper.truncateTable();
        dbHelper.truncateTable();

        for (int i = 0; i < cacheItems.size(); i++) {
            CacheItem item = cacheItems.get(i);
            boolean isInserted = databaseHelper.insertData(item.getTitle(), item.getDescripton(), item.getSize(), true, item.getPackageName());
            Log.i("insertion status", Boolean.toString(isInserted));
        }

        for (int i = 0; i < appDataItems.size(); i++) {
            CacheItem item = appDataItems.get(i);
            boolean isInserted = dbHelper.insertData(item.getTitle(), item.getDescripton(), item.getSize(), true, item.getPackageName());
            Log.i("insertion status", Boolean.toString(isInserted));
        }

    }

    public boolean getDataFromDB() {
        if(databaseHelper==null)
            databaseHelper= new DatabaseHelper(this);
        Cursor res = databaseHelper.getAllData();
        if (res==null||res.getCount() == 0) {
            // show message
            Log.e("Error", "Nothing found");
            return false;
        }
        cacheItems.clear();
        try {
            PackageManager pm = getPackageManager();

            while (res.moveToNext()) {
                try {
                   // ApplicationInfo ai = pm.getApplicationInfo(res.getString(5), 0);
                    String packageName = res.getString(res.getColumnIndex("PACKAGE_NAME"));
                    //Log.e("package_name",packageName);
                    if(packageName!=null)
                        cacheItems.add(new CacheItem(res.getString(res.getColumnIndex("APP_NAME")), res.getString(res.getColumnIndex("DESCRIPTION")), Long.parseLong(res.getString(res.getColumnIndex("SIZE"))),  pm.getApplicationIcon(packageName), true, packageName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(dbHelper ==null)
                dbHelper = new DBHelper(this);
            res = dbHelper.getAllData();

            if(res==null||res.getCount()==0)
                return false;

            while (res.moveToNext()) {
                try {
                  //  ApplicationInfo ai = pm.getApplicationInfo(res.getString(5), 0);

                    String packageName = res.getString(5);

                    if(packageName!=null)
                        appDataItems.add(new CacheItem(res.getString(1), res.getString(2), res.getLong(3), pm.getApplicationIcon(packageName), true, res.getString(5)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences("junk_size",MODE_PRIVATE);
            totalCacheSize =  sharedPreferences.getLong("cacheSize",totalCacheSize);
            totalDataSize = sharedPreferences.getLong("dataSize",totalDataSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
