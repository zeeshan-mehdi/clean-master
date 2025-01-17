package eu.faircode.netguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.alim.cleanmaster.*;
public class ApplicationInstallReceiver extends BroadcastReceiver {
    private static final String TAG = "AppInstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri packageUri = intent.getData();
        if (packageUri == null) {
            Log.w(TAG, "Package uri not available");
        } else {
            String packageName = packageUri.getEncodedSchemeSpecificPart();
            Intent scanServiceIntent = new Intent(context, ScanService.class);
            scanServiceIntent.putExtra("packageName", packageName);
            Log.d(TAG, "Scanning " + packageName);
            context.startService(scanServiceIntent);
        }
    }
}
