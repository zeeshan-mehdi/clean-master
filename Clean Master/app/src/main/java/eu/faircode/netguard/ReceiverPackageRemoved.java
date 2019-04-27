package eu.faircode.netguard;

/*
    This file is part of NetGuard.


*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.alim.cleanmaster.*;
public class ReceiverPackageRemoved extends BroadcastReceiver {
    private static final String TAG = "NetGuard.Receiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG, "Received " + intent);
        Util.logExtras(intent);

        int uid = intent.getIntExtra(Intent.EXTRA_UID, 0);
        if (uid > 0) {
            DatabaseHelper dh = DatabaseHelper.getInstance(context);
            dh.clearLog(uid);
            dh.clearAccess(uid, false);

            NotificationManagerCompat.from(context).cancel(uid); // installed notification
            NotificationManagerCompat.from(context).cancel(uid + 10000); // access notification
        }
    }
}
