package com.esstudio.gallary;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.AndroidCharacter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdaptor extends BaseAdapter {

	private Context c;
	private ArrayList<String> mItems;

	public SearchAdaptor(Context c, ArrayList<String> items) {
		this.c = c;
		this.mItems = items;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		// String item = mItems.get(position);

		TextView tv = null;
		if (arg1 == null) { // if it's not recycled, initialize some
			
		 
			

			// tv = new TextView(context);
			// tv.setGravity(Gravity.CENTER_VERTICAL |
			// Gravity.CENTER_HORIZONTAL);
			// tv.setHeight(Calc.getDP(context, 60));
			// tv.setPadding(16, 0, 16, 0);
			// tv.setText(item);
			// tv.setTextSize(20);
			// tv.setTextColor(Color.DKGRAY);
			// tv.setBackgroundColor(Color.WHITE);

			// tv.setBackground(context.getResources().getDrawable(
			// R.drawable.));

		} else {
			tv = (TextView) arg1;
			// tv.setText(item);

		}

		return tv;
	}

}
