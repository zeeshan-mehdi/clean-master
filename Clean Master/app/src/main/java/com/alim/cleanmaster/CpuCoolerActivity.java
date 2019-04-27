package com.alim.cleanmaster;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.HardwarePropertiesManager;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.jaredrummler.android.processes.models.Statm;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CpuCoolerActivity extends AppCompatActivity {

    private static final String TAG = " ";
    TextView txtTemp;
    ArrayList<Task> tasks2;
    ListView appList;
    Context context = this;
    static Context context2;
    public final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;
    static long size=0;
    Boolean scanFlag;
    public static float cpuTemp;
    public static float cpuTemp2;
    private float temperature = 0;
    ConstraintLayout constraintLayout;
    TextView cpuTempDesc;

    Button btnCool;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            cpuTemp2 = cpuTemperature();

            if((int)cpuTemp2>60){
                setTheme(R.style.RedTheme);
            }else if((int)cpuTemp2>50){
                setTheme(R.style.lightRedTheme);
            }else if((int)cpuTemp2>40){
                setTheme(R.style.brownTheme);
            }
            setContentView(R.layout.activity_cpu_cooler);
            appList = findViewById(R.id.hotApps);
            txtTemp = findViewById(R.id.txtTemperature);
            context2 = getApplicationContext();
            scanFlag = getIntent().getBooleanExtra("flag", false);
            constraintLayout = findViewById(R.id.cooler_background);
            cpuTempDesc = findViewById(R.id.txtCpuTemp);
            progressBar = findViewById(R.id.cpuProgress);
            Sprite doubleBounce = new Wave();
            doubleBounce.setColor(getResources().getColor(R.color.defaultGreen2));
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.GONE);

            btnCool = findViewById(R.id.button8);
            // collect process info
            if (Build.VERSION.SDK_INT < 17) {
                if (scanFlag)
                    getProcessesBelowFive();
                else {
                    Intent intent = new Intent(getApplicationContext(),CpuCooled.class);
                    intent.putExtra("cool","Already");
                    startActivity(intent);
                    Home.SCAN_FLAG2 = false;
                    scanFlag = false;
                    finish();
                }

            } else if (Build.VERSION.SDK_INT >= 17 && Build.VERSION.SDK_INT < 24) {
                if (scanFlag)
                    getProcessFivePlus();
                else {
                    Intent intent = new Intent(getApplicationContext(),CpuCooled.class);
                    intent.putExtra("cool","Already");
                    startActivity(intent);
                    Home.SCAN_FLAG2 = false;
                    scanFlag = false;
                    finish();
                }
            } else if (Build.VERSION.SDK_INT >= 24) {
                if(!scanFlag) {
                    Intent intent = new Intent(getApplicationContext(),CpuCooled.class);
                    intent.putExtra("cool","Already");
                    startActivity(intent);
                    finish();
                }
                else if (hasPermission() && scanFlag) {
                    getRunningTasks(getApplicationContext(), (ActivityManager) getSystemService(ACTIVITY_SERVICE));
                    Home.SCAN_FLAG2 = false;
                    scanFlag = false;
                } else {
                    startActivityForResult(
                            new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
                }

            }
            tasks2 = Home.tasks2;
            if (tasks2.size() > 0) {
                CpuCoolerAdapter cpuCoolerAdapter = new CpuCoolerAdapter(getApplicationContext(), tasks2);
                appList.setAdapter(cpuCoolerAdapter);
            } else {
                Messages.sendMessage(getApplicationContext(), "No running Tasks found");
            }
            txtTemp.setText(Float.toString(cpuTemperature()));

            setTitle("CPU Cooler");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



            if((int)cpuTemp2>60){
                constraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                cpuTempDesc.setText("Cpu Temperature very hot cool now");
            }else if((int)cpuTemp2>50){
                constraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                cpuTempDesc.setText("Cpu Temperature is hot");
            }else if((int)cpuTemp2>40){
                constraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                cpuTempDesc.setText("Cpu Temperature is little hot");
            }else{
                cpuTempDesc.setText("Cpu Temperature is normal");
            }

            PushDownAnim.setPushDownAnimTo(btnCool);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), resourseColor));
        }

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(resourseColor)));

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

    public void getRunningTasks(Context context, ActivityManager am) {
        // if the sdk >= 21. It can only use getRunningAppProcesses to get task top package name
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : stats) {
                        runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    final PackageManager pm = context.getPackageManager();
                    for (int i = 0; i < stats.size(); i++) {
                        //packages.add(stats.get(i).getPackageName());

                        ApplicationInfo ai;
                        try {
                            ai = pm.getApplicationInfo(stats.get(i).getPackageName(), 0);
                            String name = ai.loadLabel(pm).toString();
                            if (!isSystemApp(ai.packageName) && !context.getPackageName().equals(ai.packageName))
                                Home.tasks2.add(new Task(0, stats.get(i).getPackageName(), 0, 0, 'a', getAppSize(ai.packageName), 0, name, ai.loadIcon(pm)));
                        } catch (Exception e) {
                            Log.e("Error", e.toString());
                        }
                    }
                    if (runningTask.isEmpty()) {
                        return;
                    }
                }
            }
        }catch (Exception e){
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                if (hasPermission()) {
                    getRunningTasks(getApplicationContext(), (ActivityManager) getSystemService(ACTIVITY_SERVICE));
                    if (Home.tasks2.size() > 0) {
                        CpuCoolerAdapter cpuCoolerAdapter = new CpuCoolerAdapter(getApplicationContext(), Home.tasks2);
                        appList.setAdapter(cpuCoolerAdapter);
                    } else {
                        Messages.sendMessage(getApplicationContext(), "No running Tasks found");
                    }
                } else {
                    Messages.sendMessage(getApplicationContext(), "Permission was not granted");
                }
                break;
        }
    }

    public void getProcessFivePlus() {
        try {
            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
            PackageManager pm = getPackageManager();
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
                if (!isSystemApp(packageInfo.packageName) && !packageInfo.packageName.equals(getPackageName()))
                    Home.tasks2.add(new Task(pid, packageInfo.packageName, startTime, policy, state, totalSizeOfProcess, residentSetSize, appName, packageInfo.applicationInfo.loadIcon(pm)));
            }
        } catch (Exception e) {
            Log.e(TAG, "getProcessFivePlus: " + e.toString());
        }
    }

    public void getProcessesBelowFive() {
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
                    if (!isSystemApp(a.packageName) && !a.packageName.equals(getPackageName()))
                        Home.tasks2.add(task);

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getProcessesBelowFive: " + e.toString());
        }
    }
    public static long getSize() {
        return size;
    }

    public static void setSize(long size) {
        BoostDevice.size = size;
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
    private long getCurrentCPUFrequency()
    {
        String file = readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", '\n');
        if (file != null) {
            return Long.parseLong(file);
        }
        return 0;
    }

    private long getCurrentCPULoad()
    {
        String file = readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpu_utilization", '\n');
        if (file != null) {
            return Long.parseLong(file);
        }
        return 0;
    }

    private long getCurrentCPUTemperature()
    {
        String file = readFile("/sys/devices/virtual/thermal/thermal_zone0/temp", '\n');
        if (file != null) {
            return Long.parseLong(file);
        }
        return 0;
    }

    private long mMaxCpuSpeed = 0;
    private byte[] mBuffer = new byte[4096];
    @SuppressLint("NewApi")
    private String readFile(String file, char endChar) {
        // Permit disk reads here, as /proc/meminfo isn't really "on
        // disk" and should be fast.  TODO: make BlockGuard ignore
        // /proc/ and /sys/ files perhaps?
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            int len = is.read(mBuffer);
            is.close();

            if (len > 0) {
                int i;
                for (i=0; i<len; i++) {
                    if (mBuffer[i] == endChar) {
                        break;
                    }
                }
                return new String(mBuffer, 0, i);
            }
        } catch (java.io.FileNotFoundException e) {
        } catch (java.io.IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (java.io.IOException e) {
                }
            }
            StrictMode.setThreadPolicy(savedPolicy);
        }
        return null;
    }
    public static float cpuTemperature()
    {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if(line!=null) {
                float temp = Float.parseFloat(line);
                cpuTemp = temp/1000.0f;
                return temp / 1000.0f;
            }else{
                cpuTemp=51.0f;
                return 51.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            cpuTemp = 23.0f;
            return 23.0f;
        }
    }

    public void coolCpu(View view) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            Intent intent = new Intent(getApplicationContext(), CpuCooled.class);
            intent.putExtra("cool", " ");
            intent.putExtra("flag",true);
            startActivity(intent);
            Home.SCAN_FLAG2 = false;
            scanFlag = false;
            finish();
        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        Home.SCAN_FLAG2=true;
        super.onBackPressed();
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
            progressBar.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }
}
