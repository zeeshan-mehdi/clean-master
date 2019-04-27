package com.alim.cleanmaster;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;



public class MyNotification {
    private NotificationCompat.Builder notification;
    public void sendNotification(Context context,String title,String description,int flag){
        try {
            notification = new NotificationCompat.Builder(context, "id");
            notification.setAutoCancel(true);
            notification.setContentTitle(title);
            notification.setContentText(description);
            notification.setWhen(System.currentTimeMillis());
            if(flag==0)
                notification.setSmallIcon(R.drawable.speed);
            else if(flag==1)
                notification.setSmallIcon(R.drawable.cpu_cooler);
            notification.setColor(ContextCompat.getColor(context, R.color.defaultGreen2));
            notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeResource(null, R.drawable.boost);
            notification.setLargeIcon(bitmap);
            Intent intent;
            if(flag==0) {
                Home.SCAN_FLAG = true;
                intent = new Intent(context, BoostDevice.class);
                intent.putExtra("flag",true);
            }
            else {
                intent = new Intent(context, CpuCoolerActivity.class);
                Home.SCAN_FLAG2 = true;
                intent.putExtra("flag",true);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            notification.setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify((int) System.currentTimeMillis(), notification.build());
        }catch (Exception e){
            e.printStackTrace();
            //ignore
        }
    }


}
