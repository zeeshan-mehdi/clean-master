package com.alim.cleanmaster;

import static android.content.pm.PackageManager.GET_CONFIGURATIONS;
import static android.content.pm.PackageManager.GET_GIDS;
import static android.content.pm.PackageManager.GET_PERMISSIONS;
import static android.content.pm.PackageManager.GET_SIGNATURES;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;

import com.alim.cleanmaster.AppInfo;
import com.alim.cleanmaster.MalwareScan;

import com.alim.cleanmaster.*;
public class ScanService extends IntentService {
    private static final String TAG = "ScanService";

    private static final int FLAGS = GET_GIDS | GET_CONFIGURATIONS | GET_PERMISSIONS
            | GET_SIGNATURES;

    private PackageManager packageManager;

    private CertificateFactory certificateFactory;

    public ScanService() {
        super("ScanService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.packageManager = super.getPackageManager();
        try {
            this.certificateFactory = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            Log.wtf(TAG, "Failed to get X509 certificate factory");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String packageName = intent.getStringExtra("packageName");
        if (packageName != null) {
            try {
                PackageInfo packageInfo = this.packageManager.getPackageInfo(packageName, FLAGS);
                this.scan(packageInfo);
            } catch (NameNotFoundException e) {
                Log.wtf(TAG, "No such package: " + packageName);
            }
        } else {
            // probably want to do this from activity
            for (PackageInfo packageInfo : this.packageManager.getInstalledPackages(FLAGS)) {
                this.scan(packageInfo);
            }
            MalwareScan.sendInfo();
        }
    }

    private void scan(PackageInfo packageInfo) {
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            Log.d(TAG, "Ignoring system app: " + packageInfo.packageName);
        } else {
            Log.d(TAG, "Scanning package: " + packageInfo.packageName);
            if (this.isMalware(packageInfo)) {
                Log.d(TAG, "Removing " + packageInfo.packageName);
                String packageLabel = (String) packageInfo.applicationInfo
                        .loadLabel(this.packageManager);
//                this.startActivity(new Intent(this, ReportMalwareActivity.class)
//                        .putExtra("packageName", packageInfo.packageName)
//                        .putExtra("packageLabel", packageLabel)
//                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                MalwareScan.malwareApps.add(new AppInfo(packageLabel,packageInfo.packageName,packageInfo.applicationInfo.loadIcon(this.getPackageManager())));
                Log.d(TAG, " app added " + packageInfo.packageName);
            } else {
                Log.d(TAG, "Not malware: " + packageInfo.packageName);
            }
        }
    }

    private boolean isMalware(PackageInfo packageInfo) {
        return this.isMalwareByPackageName(packageInfo.packageName)
                || this.isMalwareByUsedPermissions(packageInfo.requestedPermissions)
                || this.isMalwareBySignatures(packageInfo.signatures);
    }

    private boolean isMalwareByPackageName(String packageName) {
        if (packageName != null) {
            // check in a local database (sync'd from the network)
            if (packageName.startsWith("com.malware")) {
                Log.d(TAG, "Found malware by package name: " + packageName);
                return true;
            }
        }
        return false;
    }

    private boolean isMalwareByUsedPermissions(String[] usedPermissions) {
        if (usedPermissions != null) {
            for (String usedPermission : usedPermissions) {
                if (android.Manifest.permission.SEND_SMS.equals(usedPermission)
                        || android.Manifest.permission.CALL_PHONE.equals(usedPermission)
                        || android.Manifest.permission.PROCESS_OUTGOING_CALLS
                        .equals(usedPermission)) {
                    Log.d(TAG, "Found malware by permission: " + usedPermission);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMalwareBySignatures(Signature[] signatures) {
        if (signatures != null) {
            for (Signature signature : signatures) {
                try {
                    X509Certificate cert = toX509Certificate(signature);

                    try {
                        cert.checkValidity();
                    } catch (CertificateExpiredException e) {
                        Log.d(TAG, "Found malware by expired cert: "
                                + cert.getSubjectDN().getName());
                        return true;
                    } catch (CertificateNotYetValidException e) {
                        Log.d(TAG, "Found malware by not yet valid cert: "
                                + cert.getSubjectDN().getName());
                        return true;
                    }

                    // TODO: call cert.verify(PublicKey) for some trusted key
                    if ("CN=Android Debug,O=Android,C=US".equals(cert.getIssuerDN().getName())) {
                        Log.d(TAG, "Found malware by untrusted issuer: "
                                + cert.getIssuerDN().getName());
                        return true;
                    }
                } catch (CertificateException e) {
                    Log.wtf(TAG, "Found malware by invalid certificate (should never happen)");
                    return true;
                }
            }
        }
        return false;
    }

    private X509Certificate toX509Certificate(Signature signature) throws CertificateException {
        byte[] cert = signature.toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        return (X509Certificate)this.certificateFactory.generateCertificate(input);
    }
}
