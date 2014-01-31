package com.esstudio.gallary;

import android.content.Context;

public class Calc {

	public static int getDP(Context c, float px) {
		final float scale = c.getResources().getDisplayMetrics().density;
		int pixels = (int) (px * scale + 0.5f);
		return pixels;
	}

}
