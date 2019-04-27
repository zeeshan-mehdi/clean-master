package com.alim.cleanmaster;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.jaredrummler.android.processes.models.Statm;
import com.ram.speed.booster.RAMBooster;
import com.ram.speed.booster.interfaces.CleanListener;
import com.ram.speed.booster.interfaces.ScanListener;
import com.ram.speed.booster.utils.ProcessInfo;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BoostDevice extends AppCompatActivity {

    ListView processList;
    Context context=this;
    ProcessAdapter processAdapter;

    TextView txtSize;
    TextView txtUnit;
    TextView txtRam;
    Button button ;

    /**********************************/

    private static final String TAG = "BAttery INFO";
    RAMBooster booster;
    long beforeScanRamUsed;
    long afterScanRamUsed;
    static long ramCleaned;
    boolean isUpdated=false;
    static ArrayList<Boolean> isCleaned;
    static boolean flag =false;

    static  Context context2;
    public final  int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS=1;

    static long size=0;

    ProgressBar boostProgress;

    static long ramAvailableToClean = 1234;

    Boolean scanFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            int width = getScreenSize();
//            if (width == 480||width==720||width==768)
//                setContentView(R.layout.activity_boost_device_small);
//            else if (width == 800 || width == 1200 || width == 2048 || width == 2560 || width == 1440)
//                setContentView(R.layout.activity_boost_device_large);
//            else
                setContentView(R.layout.activity_boost_device);
            processList = findViewById(R.id.processList);
            /*************************************************/
            isCleaned = new ArrayList<>();
            context2 = getApplicationContext();
            txtSize = findViewById(R.id.txtSize);
            txtUnit = findViewById(R.id.txtUnit);
            txtRam = findViewById(R.id.txtRamSize);
            button = findViewById(R.id.btnBoost);
            boostProgress = findViewById(R.id.boostProgress);

            Sprite doubleBounce = new Wave();
            doubleBounce.setColor(getResources().getColor(R.color.defaultGreen2));
            boostProgress.setIndeterminateDrawable(doubleBounce);
            boostProgress.setVisibility(View.GONE);

            scanFlag = getIntent().getBooleanExtra("flag", true);

            // collect process info
            if (Build.VERSION.SDK_INT < 17) {
                if (scanFlag)
                    getProcessesBelowFive();
                Home.SCAN_FLAG = false;
                if (Home.tasks.size() == 0) {
                    Intent intent = new Intent(getApplicationContext(), Boosted.class);
                    intent.putExtra("animate", false);
                    startActivity(intent);
                    finish();
                    //Messages.sendMessage(getApplicationContext(),"No running  tasks found");
                }
            } else if (Build.VERSION.SDK_INT >= 17 && Build.VERSION.SDK_INT < 24) {
                if (scanFlag)
                    getProcessFivePlus();
                Home.SCAN_FLAG = false;
                if (Home.tasks.size() == 0) {
                    Intent intent = new Intent(getApplicationContext(), Boosted.class);
                    intent.putExtra("animate", false);
                    startActivity(intent);
                    finish();
                    //Messages.sendMessage(getApplicationContext(),"No running  tasks found");
                }
            } else if (Build.VERSION.SDK_INT >= 24) {

                if (!scanFlag) {
                    Intent intent = new Intent(getApplicationContext(), Boosted.class);
                    intent.putExtra("animate", false);
                    startActivity(intent);
                    finish();
                    flag=false;
                }
                if (scanFlag && hasPermission()) {
                    flag=false;
                    getTaskTopAppPackageName(getApplicationContext(), (ActivityManager) getSystemService(ACTIVITY_SERVICE));
                    if (Home.tasks.size() == 0) {
                        Messages.sendMessage(getApplicationContext(), "Size zero");
                        Intent intent = new Intent(getApplicationContext(), Boosted.class);
                        intent.putExtra("animate", false);
                        startActivity(intent);
                        finish();
                        //Messages.sendMessage(getApplicationContext(),"No running  tasks found");
                    }
                } else if (!hasPermission()) {
                    startActivityForResult(
                            new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
                    flag=true;
                    Messages.sendMessage(getApplicationContext(),"Please Give Usage Access to Clean Master Just enable it");

                }
                Home.SCAN_FLAG = false;
            }
            //is task list empty


            /***********************************************************************/
            booster = new RAMBooster(context);
            booster.startScan(true);
            processAdapter = new ProcessAdapter(getApplicationContext(), Home.tasks);
            processList.setAdapter(processAdapter);


            booster.setScanListener(new ScanListener() {
                @Override
                public void onStarted() {
                }

                @Override
                public void onFinished(long availableRam, long totalRam, List<ProcessInfo> appsToClean) {
                    try {
                        long beforeRamUsed = totalRam - availableRam;
                        setBeforeScanRamUsed(beforeRamUsed);
                        Log.e(TAG, "Scan Completed Now cleaning...");
                        //  booster.startClean();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            booster.setCleanListener(new CleanListener() {
                @Override
                public void onStarted() {
                }

                @Override
                public void onFinished(long availableRam, long totalRam) {
                    long afterRamUsed = totalRam - availableRam;
                    setAfterScanRamUsed(afterRamUsed);
                    setRamCleaned(getBeforeScanRamUsed() - afterRamUsed);
                    Log.e(TAG, String.valueOf(getBeforeScanRamUsed() - afterRamUsed) + " MB freed");
                    Log.i(TAG, String.valueOf(getBeforeScanRamUsed() - afterRamUsed) + " MB freed");
                }
            });
            //progress bars

            // apps running
            PackageManager pm = getPackageManager();

            /*****************************************************************/
            fillIsChecked();
            getBatteryPowerRemaining();


            MainActivity.convert(getAvailableRam());
            txtSize.setText(MainActivity.size);
            txtUnit.setText(MainActivity.sizeUnit);

            PushDownAnim.setPushDownAnimTo(button);

            button.setText("Boost "+MainActivity.size+MainActivity.sizeUnit);

            ramAvailableToClean = getAvailableRam();

            txtRam.setText( MainActivity.convert(ramAvailableToClean)+"/"+MainActivity.convert(getTotalDeviceRam()));
            setTitle("Boost Device");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }catch (Exception e){
            //ignore
        }
    }

    public static long getRamCleaned() {
        return ramCleaned;
    }

    public static void setRamCleaned(long ramCleaned) {
        BoostDevice.ramCleaned = ramCleaned;
    }

    public long getBeforeScanRamUsed() {
        return beforeScanRamUsed;
    }

    public void setBeforeScanRamUsed(long beforeScanRamUsed) {
        this.beforeScanRamUsed = beforeScanRamUsed;
    }

    public long getAfterScanRamUsed() {
        return afterScanRamUsed;
    }

    public void setAfterScanRamUsed(long afterScanRamUsed) {
        this.afterScanRamUsed = afterScanRamUsed;
    }

    public void getProcessFivePlus(){
        try {
            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
            PackageManager pm= getPackageManager();
            for (AndroidAppProcess process : processes) {
                // Get some information about the process
                String processName = process.name;

                Stat stat = process.stat();
                int pid = stat.getPid();
                int parentProcessId = stat.ppid();
                long startTime = stat.stime();
                int policy = stat.policy();
                char state = stat.state();

                Statm statm = process.statm();
                long totalSizeOfProcess = statm.getSize();
                long residentSetSize = statm.getResidentSetSize();

                PackageInfo packageInfo = process.getPackageInfo(context, 0);
                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                if(!isSystemApp(packageInfo.packageName)&&!packageInfo.packageName.equals(getPackageName()))
                    Home.tasks.add(new Task(pid,packageInfo.packageName,startTime,policy,state,totalSizeOfProcess,residentSetSize,appName,packageInfo.applicationInfo.loadIcon(pm)));
            }
        }catch (Exception e){
            Log.e(TAG, "getProcessFivePlus: "+e.toString());
        }
    }
    public void getProcessesBelowFive(){
        PackageManager pm = getPackageManager();
        try {
            ActivityManager am1 = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> processes = am1.getRunningTasks(Integer.MAX_VALUE);

            if (processes != null) {
                for (int k = 0; k < processes.size(); k++) {
                    // String pkgName = app.getPackageName();
                    String packageName = processes.get(k).topActivity.getPackageName();
                    Log.e("packageName-->", "" + packageName);
                    Drawable ico = null;

                    String pName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                    ApplicationInfo a = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                    ico = getPackageManager().getApplicationIcon(processes.get(k).topActivity.getPackageName());
                    getPackageManager();
                    Log.e("ico-->", "" + ico);
                    File file = new File(a.publicSourceDir);

                    // icons.put(processes.get(k).topActivity.getPackageName(),ico);
                    Task task = new Task(processes.get(k).id, a.packageName, 0, 0, 'c', file.length(), 0, a.name, ico);
                   if(!isSystemApp(a.packageName)&&!a.packageName.equals(getPackageName()))
                       Home.tasks.add(task);

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getProcessesBelowFive: "+e.toString());
        }
    }
    public void boostDevice(View view) {
        boostProgress.setVisibility(View.VISIBLE);

      //  booster.startClean();
        long c=0;
        try {
            ArrayList<Task> tempArr = new ArrayList<>();
            int length= Home.tasks.size();
            for (int i = 0; i < Home.tasks.size(); i++) {
                if (!isCleaned.get(i)) {
                    tempArr.add(Home.tasks.get(i));
                }else{
                    long size = Home.tasks.size();
                    android.os.Process.killProcess(Home.tasks.get(i).getPid());
                   // killApp(Home.tasks.get(i));
                    c+=size;
                }

            }
            setRamCleaned(c);
            Home.tasks=tempArr;
          //  processAdapter.updateList(context, Home.tasks);
            SharedPreferences sharedPreferences = getSharedPreferences("tasks",MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        }catch (Exception e){
            e.printStackTrace();
        }

       startActivity(new Intent(context, Boosted.class));
            finish();
    }
    public void killApp(Task task){
        try
        {
            Process suProcess = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());

            os.writeBytes("adb shell" + "\n");
            os.flush();
            os.writeBytes("am force-stop "+task.getAppName()+ "\n");

            os.flush();
            os.close();
            suProcess.waitFor();

        }

        catch (IOException ex)
        {
            ex.getMessage();
            Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        catch (SecurityException ex)
        {
            Toast.makeText(getApplicationContext(), "Can't get root access2",
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Can't get root access3",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public static boolean isSystemApp(String packageName) {
        try {
            // Get packageinfo for target application
            PackageInfo targetPkgInfo =context2.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            // Get packageinfo for system package
            PackageInfo sys =context2.getPackageManager().getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            // Match both packageinfo for there signatures
            return (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public float getCurrentBatteryPercentage(){
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            return level / (float) scale;
        }catch (Exception e){
         return 50.0f;
        }
    }
    public long getBatteryPowerRemaining(){
        try {
            BatteryManager mBatteryManager =
                    (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            Long energy =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                    ) {
                energy = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
            }
            Log.i(TAG, "Remaining energy = " + energy + "nWh");

            return energy;
        }catch (Exception e){
            return 10;
        }
    }
    @Override
    public void onBackPressed() {
        try {
            Home.SCAN_FLAG=true;
            super.onBackPressed();
        }catch (Exception e){

        }
    }
    public void fillIsChecked(){
        for(int i=0;i<Home.tasks.size();i++){
            isCleaned.add(true);
        }
    }
    private boolean hasPermission() {
        AppOpsManager appOps = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            appOps = (AppOpsManager)
                    getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }

        return false;
    }
    public static String getTaskTopAppPackageName(Context context, ActivityManager am) {
        try {
            //Home.tasks= new ArrayList<>();
            String packageName = "";
            // if the sdk >= 21. It can only use getRunningAppProcesses to get task top package name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : stats) {
                        runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    for (int i = 0; i < stats.size(); i++) {
                        //packages.add(stats.get(i).getPackageName());
                        final PackageManager pm = context.getPackageManager();
                        ApplicationInfo ai;
                        try {
                            ai = pm.getApplicationInfo(stats.get(i).getPackageName(), 0);
                            String name = ai.loadLabel(pm).toString();
                            if (!isSystemApp(ai.packageName) && !context.getPackageName().equals(ai.packageName))
                                Home.tasks.add(new Task(0, stats.get(i).getPackageName(), 0, 0, 'a', getAppSize(ai.packageName), 0, name, ai.loadIcon(pm)));
                        } catch (Exception e) {
                            Log.e("Error", e.toString());
                        }
                    }
                    if (runningTask.isEmpty()) {
                        return null;
                    }
                    packageName = runningTask.get(runningTask.lastKey()).getPackageName();
                }
            } else {// if sdk <= 20, can use getRunningTasks
                List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
                packageName = infos.get(0).topActivity.getPackageName();
            }

            return packageName;
        }catch (Exception e){
            return "com.cashecaleener.cachecleaner";
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                if (hasPermission()){
                    getTaskTopAppPackageName(getApplicationContext(), (ActivityManager) getSystemService(ACTIVITY_SERVICE));
                    if(Home.tasks.size()==0){
                        Messages.sendMessage(getApplicationContext(),"Size zero");
                        Intent intent=new Intent(getApplicationContext(),Boosted.class);
                        intent.putExtra("animate",false);
                        startActivity(intent);
                        finish();
                        //Messages.sendMessage(getApplicationContext(),"No running  tasks found");
                    }else{
                        processAdapter = new ProcessAdapter(getApplicationContext(), Home.tasks);
                        processList.setAdapter(processAdapter);
                    }
                }else{
                    Messages.sendMessage(getApplicationContext(),"Permission was not granted");
                }
                break;
        }
    }

    public static long getAppSize(String packageName) {
        PackageManager pm = context2.getPackageManager();
        try {
            Method getPackageSizeInfo = pm.getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            getPackageSizeInfo.invoke(pm, packageName,
                    new IPackageStatsObserver.Stub() {

                        @Override
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                                throws RemoteException {
                            setSize(pStats.codeSize);
                            Log.i(TAG, "codeSize: " + pStats.codeSize);
                        }
                    });
        } catch (Exception e) {
            return getSize();
        }
        return getSize();
    }

    public static long getSize() {
        return size;
    }

    public static void setSize(long size) {
        BoostDevice.size = size;
    }
    public int getScreenSize(){
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            //1080 * 1776

            //480 800

            return width;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public long getTotalDeviceRam(){
        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        return totalMemory;
    }

    public long calculateSize(){
        long c=0;
        for(int i=0;i<Home.tasks.size();i++){

                long size = getAppSize(Home.tasks.get(i).getPackageName());
                // killApp(Home.tasks.get(i));
                c+=size;
        }
        return c;
    }
    public long getAvailableRam(){
        try{
            return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        }catch (Exception e){
            e.printStackTrace();
            return 1024*1024;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        try{
            boostProgress.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }
}
