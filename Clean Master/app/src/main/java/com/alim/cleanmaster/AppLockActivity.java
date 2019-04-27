package com.alim.cleanmaster;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.ads.AdRequest.LOGTAG;

public class AppLockActivity extends AppCompatActivity {

    private static  ListView appsList;
    private WindowChangeDetectingService detectingService;
    public static List<PackageInfo> packageInfos;
    static ArrayList<PackageInfo> arr;
    static  AppsAdapter appsAdapter;
    static String topPackage;
    static SplashScreen context;
    static ArrayList<Boolean> isLocked;
    static String TAG="INFO";
    static  ProgressBar progressBar;
    static  PackageManager packageManager;
    static boolean locked=false;
    static  AppLockActivity context2;

    Timer timer;

    static boolean scanned = false;

    //Ads
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_app_lock);
            appsList = findViewById(R.id.appsList);
            progressBar= findViewById(R.id.appsProgress);
            progressBar.setVisibility(View.VISIBLE);

            Sprite doubleBounce = new Wave();
            doubleBounce.setColor(getResources().getColor(R.color.defaultGreen2));
            progressBar.setIndeterminateDrawable(doubleBounce);

            context2 =this;
            timer = new Timer();
//            if(!scanned)
//                new CollectApps().execute();

            if(!isAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                Messages.sendMessage(getApplicationContext(),"please enable access usage stats permission");
            }
            final Handler handler2 = new Handler();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler2.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         Log.e("timer", "timer task is running");
                                         if(isAccessGranted()){
                                             //Messages.sendMessage(getApplicationContext(),"Permission granted ");
                                             Log.e("tag","permission granted for usage access");
                                         }
                                     }
                    });
                }
            }, 0, 1000);



//            AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
//            try {
//                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
//                    if(!isAccessibilityServiceEnabled(getApplicationContext(), WindowChangeDetectingService.class) ){
//                        Intent intent3 = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent3);
//                        Messages.sendMessage(getApplicationContext(), "Please Enable Accessability permission for AppLock");
//                    }
//                }else {
//                    if (!accessibilityManager.isEnabled()) {
//                        Intent intent3 = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent3);
//                        Messages.sendMessage(getApplicationContext(), "Please Enable Accessability permission for AppLock");
//                    }
//                }
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//                accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
//                    @Override
//                    public void onAccessibilityStateChanged(boolean b) {
//                        if (!b)
//                            Toast.makeText(getApplicationContext(), "permission not Granted this app may not work properly ", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            Intent intent3 = new Intent(getApplicationContext(), WindowChangeDetectingService.class);
//            startService(intent3);
          //  Messages.sendMessage(getApplicationContext(),"Please Enable Accessability permission status :" +Boolean.toString(isAccessibilitySettingsOn(context)));
            //ads
            MobileAds.initialize(getApplicationContext(),"ca-app-pub-2958463332087114~5829629490");
            mInterstitialAd = new InterstitialAd(this);

            mInterstitialAd.setAdUnitId("ca-app-pub-2958463332087114/7532519946");

            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            final Handler handler = new Handler();
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(SplashScreen.scanned){
                                Log.e("run","true");
                                if(scanned){
                                    appsAdapter = new AppsAdapter(context, arr);
                                    appsList.setAdapter(appsAdapter);
                                    progressBar.setVisibility(View.GONE);
                                }
                                timer.cancel();
                            }
                        }
                    });
                }
            },0,1000);


        }catch (Exception e){
            Log.i(TAG,e.toString());
            e.printStackTrace();
        }
    }
    // To check if service is enabled
    public boolean isAccessibilitySettingsOn(Context context){
        int accessibilityEnabled = 0;
        final String service = "<apppackage>/<servicepackage>";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
           Log.v(TAG,"accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.v(TAG,"Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG,"***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v(TAG,"-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG,"We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG,"***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    public static AppLockActivity getInstance(){
        return context2;
    }

    public List<PackageInfo> getPackageInfos() {
        return packageInfos;
    }

    public static void setPackageInfos(ArrayList<PackageInfo> packageInfo) {
        packageInfos = packageInfo;
    }
    public void lauchLockActivity(String pName,String thisActivity){
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.prefrencesName),Context.MODE_PRIVATE);
        topPackage = pName;
        if(preferences.contains(pName))
        {
            try {
            WindowChangeDetectingService.lastActivity=pName;
            Log.i(TAG, "lauchLockActivity: matched");
            Intent intent2 = new Intent(context,SetPattern.class);
            intent2.putExtra("flag",false);
            startActivity(intent2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Log.i(TAG, "lauchLockActivity: mismatch");
        }
    }
    public static void fillLockedArray(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.prefrencesName),Context.MODE_PRIVATE);
        for(int i=0;i<packageInfos.size();i++){
            if(sharedPreferences.contains(packageInfos.get(i).packageName))
                isLocked.add(true);
            else
                isLocked.add(false);
        }
    }
    static class CollectApps extends AsyncTask<Void, String,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            packageInfos = packageManager.getInstalledPackages(0);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            scanned = true;
            sortApps();
            isLocked = new ArrayList<>();
            fillLockedArray();
            arr= (ArrayList<PackageInfo>) packageInfos;
        }
    }

    @Override
    public void onBackPressed() {
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        super.onBackPressed();
    }
    public static void sortApps (){
        List<PackageInfo> temp =new ArrayList<>();
        if(temp!=null){
            for(int i=0;i<packageInfos.size();i++){
                if(!isSystemApp(packageInfos.get(i).packageName)){
                    temp.add(packageInfos.get(i));
                }
            }
        }
        packageInfos= temp;
    }
    public static boolean isSystemApp(String packageName) {
        try {
            // Get packageinfo for target application
            PackageInfo targetPkgInfo =packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            // Get packageinfo for system package
            PackageInfo sys =packageManager.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            // Match both packageinfo for there signatures
            return (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            }
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.applock_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.change_pattern){
            Intent intent = new Intent(this,SetPattern.class);
            intent.putExtra("change",true);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
