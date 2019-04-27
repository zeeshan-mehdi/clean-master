package com.alim.cleanmaster;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import io.paperdb.Paper;

import static android.os.Build.VERSION.SDK;
import static com.alim.cleanmaster.AppLockActivity.TAG;
import static com.alim.cleanmaster.AppLockActivity.context;
import static com.alim.cleanmaster.AppLockActivity.context2;
import static com.alim.cleanmaster.AppLockActivity.getInstance;

public class LockService extends Service {


    String lastPackage = "";

    String save_pattern_key = "pattern";
    PatternLockView patternLockView;
    String final_pattern;
    static Home homeContext;

    Button createPattern;
    final Timer timer = new Timer();
    static boolean finishFlag = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            //Messages.sendMessage(getApplicationContext(), "start command");
            Log.e("started ", "started");

            final Handler handler = new Handler();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("service", "service is running");
                            String pName= "com.android.vending";
                            SharedPreferences preferences = getSharedPreferences(String.valueOf(R.string.prefrencesName), Context.MODE_PRIVATE);




                            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                Log.e("lollipop", String.valueOf(Build.VERSION.SDK_INT));
                                pName = getForegroundProcess(getApplicationContext());

                            }
                            else {
                                Log.e("android 4", String.valueOf(Build.VERSION.SDK_INT));
                                pName = getForegroundAppPackageNameBeforeLollipop();

                            }

                            if(pName==null)
                                return;

                            if (pName.equals("com.android.systemui") || pName.equals("com.android.launcher3")||pName.equals(getAllLaunchersList())) {
                                lastPackage = "";
                            }

                            if (preferences.contains(pName)) {
                                try {
                                    if (!pName.equals(lastPackage)) {
                                        Log.i(TAG, "lauchLockActivity: matched");
                                        Intent intent2;
//                                        if (context == null)
//                                            intent2 = new Intent(LockService.this, SetPattern.class);
//                                        else
                                            intent2 = new Intent(LockService.this, SetPattern.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent2.putExtra("flag", false);
                                        startActivity(intent2);
                                        // loadLockWindow();
                                        lastPackage = pName;
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Messages.sendMessage(getApplicationContext(),"inner"+e.toString());
                                }
                            } else {
                                Log.i(TAG, "lauchLockActivity: mismatch");
                            }
                           // Log.d("foreground", "foreground" + getForegroundProcess(getApplicationContext()));
                        }
                    });
                }
            }, 0, 1000);
        } catch (Exception e) {
            Messages.sendMessage(getApplicationContext(),"outer"+e.toString());
            e.printStackTrace();
        }

        return START_STICKY;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getForegroundProcess(Context context) {
        try {
            String topPackageName = null;
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


                UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : stats) {
                        runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (runningTask.isEmpty()) {
                        return null;
                    }

                    topPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
                }
                if (topPackageName == null) {
//                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                context.startActivity(intent);
                }
            }
            return topPackageName;
        } catch (Exception e) {
            e.printStackTrace();
            Messages.sendMessage(context,"functio");
            return null;
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("lock service", "service ended2");
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000,
//                restartServicePendingIntent);
        Intent broadcastIntent = new Intent(this, Restarter.class);

        sendBroadcast(broadcastIntent);
//        if (timer != null) {
//            timer.cancel();
//        }

        super.onDestroy();

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.e("lock service", "service ended");
//        Messages.sendMessage(getBaseContext(),"service ended");
//        Intent broadcastIntent = new Intent(this, Restarter.class);
//
//        sendBroadcast(broadcastIntent);
//        if (timer != null) {
//            timer.cancel();
//        }

        super.onDestroy();
    }
    public String  getAllLaunchersList(){
        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;

        Log.e("Current launcher",str);
        return  str;
    }

    public String getForegroundAppPackageNameBeforeLollipop(){
        try {
            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
// The first in the list of RunningTasks is always the foreground task.
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
            PackageManager pm = getPackageManager();
            PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);

            Log.e("foreground 4",foregroundAppPackageInfo.packageName);
            return foregroundAppPackageInfo.packageName;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
