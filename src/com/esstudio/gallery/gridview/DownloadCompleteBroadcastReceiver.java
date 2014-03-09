package com.esstudio.gallery.gridview;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.esstudio.gallery.MainActivity;

/**
 * Created by EunSung on 14. 2. 19.
 */
public class DownloadCompleteBroadcastReceiver extends BroadcastReceiver {

    Context c;
    String mFileName = null;

    public DownloadCompleteBroadcastReceiver(Context context, String fileName){
        this.mFileName = fileName;
        this.c = context;
    }

    private void showNotification(){


        // intent triggered, you can add other intent for other actions
//        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
//        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(MainActivity.getInstance())

                .setContentTitle(mFileName)
                .setContentText("Download Complete!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentIntent(pIntent)
//                .addAction(R.drawable.ninja, "View", pIntent)
//                .addAction(0, "Remind", pIntent)

                .build();

        NotificationManager notificationManager = (NotificationManager) MainActivity.getInstance()
                .getSystemService(c.NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, mNotification);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        showNotification();

        Toast.makeText(context, "\"" + mFileName + "\"\nDownload Complete!", Toast.LENGTH_SHORT)
                .show();

    }
}