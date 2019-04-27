package com.alim.cleanmaster;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.util.Log;

import com.alim.cleanmaster.JunkInfo;
import com.alim.cleanmaster.MainActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.alim.cleanmaster.*;
/**
 * This class will perform data operation
 */
public class StorageInformation {

    long packageSize = 0;
    AppDetails cAppDetails;
    public ArrayList<AppDetails.PackageInfoStruct> res;
    Context context;

    public StorageInformation(Context context){
        this.context=context;
    }

    public void getpackageSize() {
        cAppDetails = new AppDetails((Activity) context);
        res = cAppDetails.getPackages();

        if (res == null)
            return;
        for (int m = 0; m < res.size(); m++) {
            // Create object to access Package Manager
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> appsInfo= pm.getInstalledApplications(0);
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, res.get(m).pname,
                        new cachePackState()); //Call the inner class
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    /*
     * Inner class which will get the data size for application
     * */
    private class cachePackState extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {

            Log.w("Package Name", pStats.packageName + "");
            Log.i("Cache Size", pStats.cacheSize + "");
            Log.v("Data Size", pStats.dataSize + "");
            packageSize = pStats.dataSize + pStats.cacheSize;
          //  MainActivity.junckInfo.add(new JunkInfo(pStats.cacheSize,pStats.dataSize,packageSize));
            SplashScreen.junckInfo.add(new JunkInfo(pStats.cacheSize,pStats.dataSize,packageSize));
            Log.v("Total Cache Size", " " + packageSize);
            Log.v("APK Size",pStats.codeSize+"");
        }
    }
}