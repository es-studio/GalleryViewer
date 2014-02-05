package com.esstudio.gallary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fedorvlasov.lazylist.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ImageClickListener implements OnItemClickListener {

    private Context c;
    private ArrayList<ElementItem> mAllItems;
    private ElementItem mItems;
    private BroadcastReceiver completeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    c).setContentTitle("My notification").setContentText(
                    "Hello World!");
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(c, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) c
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(12345, mBuilder.build());

            Toast.makeText(context, "Download Done!", Toast.LENGTH_SHORT)
                    .show();
        }

    };

    public ImageClickListener(Context c, ArrayList<ElementItem> item) {
        this.c = c;
        this.mAllItems = item;
    }

    public void openImage() {
        Context c = MainActivity.getInstance();

        File file = new File(
                android.os.Environment.getExternalStorageDirectory(),
                "GalleryViewer/cache" + "/"
                        // + String.valueOf(AeSimpleSHA1.SHA1(mItems
//                        + String.valueOf(Utils.getMD5(mItems.getImageUrl())));
                        + new HashCodeFileNameGenerator().generate(mItems.getImageUrl()));

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
        intent.setDataAndType(Uri.parse("file://" + file.getPath()), "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);

    }

    public void openUrl(String url) {
        Context c = MainActivity.getInstance().getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    public void downloadImage() {

        DownloadManager dnManager = (DownloadManager) MainActivity
                .getInstance().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri Download_Uri = Uri.parse(mItems.getImageUrl());
        DownloadManager.Request request = new DownloadManager.Request(
                Download_Uri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle(mItems.getTitle());
        request.setDescription(mItems.getImageUrl());
        request.setNotificationVisibility(View.VISIBLE);

        String fileName = mItems.getUrl().substring(
                mItems.getUrl().indexOf("no=") + 3, mItems.getUrl().length());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd_HH-mm-ss");
        String now = simpleDateFormat.format(new Date(System
                .currentTimeMillis()));

        File downDir = null;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            downDir = new File(Environment.getExternalStorageDirectory(),
                    "GalleryViewer/download/" + now + "_" + fileName + ".jpg");

        request.setDestinationUri(Uri.fromFile(downDir));

        IntentFilter completeFilter = new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        MainActivity.getInstance().registerReceiver(completeReceiver,
                completeFilter);

        // Enqueue a new download and same the referenceId
        long ref = dnManager.enqueue(request);

    }

    private void showLink() {
        // TODO Auto-generated method stub
        Context c = MainActivity.getInstance();
        // Context context = getApplicationContext();
        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        String url = mItems.getUrl();
        String title = url.substring(url.indexOf("no=") + 3, url.length());
        alert.setTitle(title); // Set Alert dialog title

        final ArrayList<String> links = new ArrayList<String>();
        links.addAll(mItems.getmYoutube());
        links.addAll(mItems.getmMP4());
        links.addAll(mItems.getmTorrent());

        alert.setItems(

                links.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                openUrl(links.get(which));
            }

        });

        // View l = findViewById(R.layout.dialog);
        // alert.setView(l);
        alert.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); // End of alert.setPositiveButton

        AlertDialog alertDialog = alert.create();
        // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();

    }

    public void addUrlForDebug() {
        String url = mItems.getUrl();
        // write a log

        File logFile = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            logFile = new File(Environment.getExternalStorageDirectory(),
                    "GalleryViewer/debug.log");

            try {
                FileWriter fw = new FileWriter(logFile, true);
                fw.write(url + "\n");
                fw.flush();
                fw.close();

                MainActivity.getInstance().showToast("URL : \"" + url + "\" Added for debug");

            } catch (IOException e) {
                log.out("Debug File Output Error");
                e.printStackTrace();
            }
        }


    }

    public void showDialog() {
        Context c = MainActivity.getInstance();
        // Context context = getApplicationContext();
        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        String url = mItems.getUrl();
        String title = url.substring(url.indexOf("no=") + 3, url.length());
        alert.setTitle(title); // Set Alert dialog title

        alert.setItems(new String[]{
                "Open Browser",
                "Open Image",
                "Download Image",
                "Link...",
                "Add URL for Debug"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                openUrl(mItems.getUrl());
                                break;

                            case 1:
                                openImage();
                                break;

                            case 2:
                                downloadImage();
                                break;

                            case 3:
                                showLink();
                                break;

                            case 4:
                                addUrlForDebug();
                                break;

                            default:
                                break;
                        }

                    }

                });

        // View l = findViewById(R.layout.dialog);
        // alert.setView(l);
        alert.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); // End of alert.setPositiveButton

        AlertDialog alertDialog = alert.create();
        // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();

    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        mItems = mAllItems.get(arg2);
        showDialog();

    }
}
