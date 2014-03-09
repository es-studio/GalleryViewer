package com.esstudio.gallery.navi;

import java.util.HashMap;


import com.esstudio.gallery.R;
import com.esstudio.gallery.util.log;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NaviDrawerAdaptor extends BaseAdapter {

	private Context context;
	private HashMap<String, String> mItems;
	private LayoutInflater mInflater;

	public NaviDrawerAdaptor(Context c, HashMap<String, String> items) {
		this.context = c;
		this.mItems = items;

		log.out(items.size() + " ");
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		Object[] k = mItems.keySet().toArray();
		Object[] v = mItems.values().toArray();

		return new String[] { k[position].toString(), v[position].toString() };

		// return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// String item = mItems.get(position);
		String[] item = (String[]) getItem(position);

		TextView tv1 = null;
		TextView tv2 = null;
		if (convertView == null) { // if it's not recycled, initialize some

			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			tv1 = (TextView) convertView.findViewById(R.id.textView1);
			tv2 = (TextView) convertView.findViewById(R.id.textView2);

		} else {

			tv1 = (TextView) convertView.findViewById(R.id.textView1);
			tv2 = (TextView) convertView.findViewById(R.id.textView2);
		}

		tv1.setTextColor(Color.LTGRAY);
		tv2.setTextColor(Color.LTGRAY);

		tv1.setText(item[1]);
		tv2.setText(item[0]);

		return convertView;
	}

	private void foo() {
		// TextView tv = null;
		// if (convertView == null) { // if it's not recycled, initialize some
		//
		// // tv = new TextView(context);
		// // tv = new TextView(context);
		// LayoutInflater layoutInflater = (LayoutInflater)
		// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//
		// tv = (TextView) layoutInflater.inflate(
		// android.R.layout.two_line_list_item,
		// null);
		// tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		// tv.setHeight(Calc.getDP(context, 60));
		// tv.setPadding(16, 0, 16, 0);
		// tv.setText(item);
		// tv.setTextSize(20);
		// tv.setTextColor(Color.DKGRAY);
		// // tv.setBackgroundColor(Color.WHITE);
		//
		// // tv.setBackground(context.getResources().getDrawable(
		// // R.drawable.));
		//
		// } else {
		// tv = (TextView) convertView;
		// tv.setText(item);
		//
		// }
		//
		// return tv;
	}
}
