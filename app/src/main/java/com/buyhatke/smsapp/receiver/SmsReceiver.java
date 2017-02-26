package com.buyhatke.smsapp.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;

import com.buyhatke.smsapp.R;
import com.buyhatke.smsapp.activity.MainActivity;

/**
 * Created by adarsh on 26/02/17.
 */

public class SmsReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    private NotificationManager mNotificationManager;
    private int notificationID = 100;

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String from = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                from = smsMessage.getOriginatingAddress();

                smsMessageStr += smsBody + "\n";
            }
            CreateNotification(context, from, smsMessageStr.toString());
        }
    }

    private void CreateNotification(Context context, String from, String message){
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setContentTitle(from);
        nBuilder.setContentText(message);
        nBuilder.setTicker("New Message");
        nBuilder.setAutoCancel(true);
        nBuilder.setDefaults(Notification.DEFAULT_SOUND);
        nBuilder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, nBuilder.build());
    }
}
