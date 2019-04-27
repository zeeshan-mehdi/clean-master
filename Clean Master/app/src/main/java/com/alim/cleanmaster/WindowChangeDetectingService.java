package com.alim.cleanmaster;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import static android.content.ContentValues.TAG;

public class WindowChangeDetectingService extends AccessibilityService {
    static String lastActivity="";
    public static String getName(){
       return WindowChangeDetectingService.getName();
    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (event.getPackageName() != null && event.getClassName() != null) {
                    ComponentName componentName = new ComponentName(
                            event.getPackageName().toString(),
                            event.getClassName().toString()
                    );

                    ActivityInfo activityInfo = tryGetActivity(componentName);
                    if(activityInfo==null)
                        return;
                    if(activityInfo.packageName.equals("com.android.systemui")||activityInfo.packageName.equals("com.android.launcher3")){
                        lastActivity="";
                    }
                    boolean isActivity = activityInfo != null;
                    if (isActivity) {
                        String thisActivity = activityInfo.packageName;
                        if (!thisActivity.equals(lastActivity)) {
                            AppLockActivity.getInstance().lauchLockActivity(activityInfo.packageName, componentName.flattenToShortString());
                        }

                        Log.i("CurrentActivity", componentName.flattenToShortString());
                    }
                }
            }
        }catch (Exception e){
            Log.i(TAG,"exception inside window service");
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "service interrupted");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service destroyed");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "service started");
        return super.onStartCommand(intent, flags, startId);
    }

    public String getLauncherPackage(){
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher =getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
        return defaultLauncherStr;
    }

}
