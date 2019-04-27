package com.alim.cleanmaster;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class collectContext {
    static ArrayList<Context> contexts;
    collectContext(){
        contexts = new ArrayList<>();
    }
    public void generateContext(Context context){
        List<PackageInfo> apps;
        apps = context.getPackageManager().getInstalledPackages(0);
        String packageName=null;
        for (int i=0;i<apps.size();i++){
            try {
                PackageInfo p = apps.get(i);
                packageName =p.packageName;
                Context context2 =context.createPackageContext(p.packageName,Context.CONTEXT_IGNORE_SECURITY|Context.CONTEXT_INCLUDE_CODE);
                contexts.add(context2);
            } catch (Exception e) {
                Toast.makeText(context,packageName,Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}
