package com.esstudio.gallary;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.fedorvlasov.lazylist.ImageLoader;

public class ImageAdaptor extends BaseAdapter {

    private ArrayList<ElementItem> mItems;
    private Context context;
    private Paint p;
    private int width;
    private ImageLoader loader;

    public ImageAdaptor(Context context, ArrayList<ElementItem> items) {
        this.mItems = items;
        this.context = context;
        loader = new ImageLoader(context);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        MyImageView imageView = null;
        ElementItem item = mItems.get(position);
        width = MainActivity.getInstance().getThumbnailWidth();

        if (convertView == null) { // if it's not recycled, initialize some

            imageView = new MyImageView(context, item);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            imageView = (MyImageView) convertView;
            imageView.setElementItem(item);

        }

        imageView.setBackgroundColor(Color.LTGRAY);
        imageView.setLayoutParams(new GridView.LayoutParams(width, width));
        loader.DisplayImage(item.getImageUrl(), imageView);

        return imageView;
    }

}
