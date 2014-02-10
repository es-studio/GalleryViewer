package com.esstudio.gallery;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.drawable;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.InputType;
import android.text.Layout;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyImageView extends ImageView
{

    private Context c;
    private Paint p;
    private Paint pp = new Paint();

    private ElementItem item;
    private Animation ani;
    private Rect r;

    private int fontSize = 10;

    public MyImageView(Context context, ElementItem item) {
        super(context);
        // TODO Auto-generated constructor stub
        this.c = context;
        this.item = item;

        //        this.setOnClickListener(this);
        //        this.setOnLongClickListener(this);
//        ani = AnimationUtils.loadAnimation(MainActivity.getInstance()
//            .getContext(), android.R.anim.fade_in);
////        setAnimation(ani);
//        ani.setFillEnabled(true);

        this.p = new Paint();
        p.setTextSize(Calc.getDP(context, fontSize));
        p.setAntiAlias(true);
        p.setHinting(Paint.HINTING_ON);
        p.setAlpha(200);
        // p.setStyle(Paint.Style.FILL);
        // p.setStrokeWidth(1);
        // p.setShadowLayer(0.5f, 0f, 0f, Color.BLACK);

        this.r = new Rect();
        pp.setColor(Color.BLACK);
        pp.setAlpha(100);
        log.out("--------------- creating ------------");

    }

    public void setElementItem(ElementItem item) {
        this.item = item;
    }

    public void startAnimation() {

//        ani = AnimationUtils.loadAnimation(MainActivity.getInstance()
//            .getContext(), android.R.anim.fade_in);
//        ani.setFillEnabled(true);
//        startAnimation(ani);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        r.set(0, (int) (getMeasuredHeight() - p.getTextSize() * 1.3),
            getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(r, pp);

        if (item.isOnKeyword())
            p.setColor(Color.YELLOW);
        else
            p.setColor(Color.WHITE);
        // p.setColor(Color.rgb(0xaa, 0x66, 0xcc));

        canvas.drawText(item.getTitle(), Calc.getDP(c, 3), getMeasuredHeight()
                - Calc.getDP(c, 3), p);

        int position = 13;
        p.setColor(Color.RED);

        if (item.getmTorrent().size() > 0) {
            canvas.drawText(
                "Magnet",
                Calc.getDP(c, 3),
                Calc.getDP(c, position),
                p);

            position *= 2;
        }

        if (item.getmYoutube().size() > 0) {
            canvas.drawText(
                "Youtube",
                Calc.getDP(c, 3),
                Calc.getDP(c, position),
                p);

            position *= 2;
        }

        if (item.getmMP4().size() > 0) {
            canvas.drawText(
                "MP4",
                Calc.getDP(c, 3),
                Calc.getDP(c, position),
                p);

            position *= 2;
        }
    }
}
