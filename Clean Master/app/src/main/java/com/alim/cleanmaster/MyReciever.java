package com.alim.cleanmaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alim.cleanmaster.Services.FlagService;

public class MyReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,LockService.class);
        context.startService(intent1);
//        intent1 =  new Intent(context,FlagService.class);
//        context.startService(intent1);
    }

}
