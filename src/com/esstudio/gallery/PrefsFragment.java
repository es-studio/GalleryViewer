package com.esstudio.gallery;

import java.io.File;

import com.esstudio.gallery.R;
import com.fedorvlasov.lazylist.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class PrefsFragment extends PreferenceFragment implements
		OnPreferenceClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		getActivity().getActionBar().setTitle("Settings");

		Preference pref = findPreference("clear_cache");
		pref.setOnPreferenceClickListener(this);

		Preference reset = findPreference("pReset");
		reset.setOnPreferenceClickListener(this);

		calcCacheSize();

	}

	public void calcCacheSize() {

		File inCache = MainActivity.getInstance().getCacheDir();
		File extCache = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"GalleryViewer/cache");

		if (extCache.exists() == false)
			extCache.mkdirs();

		long in = getFolderSize(inCache);
		long ex = getFolderSize(extCache);

		String inStr = formatByte(in);
		String exStr = formatByte(ex);

		Preference pref = findPreference("clear_cache");
		pref.setSummary("SDCard : " + exStr);

	}

	public String formatByte(long i) {
		String inStr = null;
		float in = i;
		if (in < 1024) {
			inStr = String.format("%.2f Byte", in);
		} else if (in < (1024 * 1024)) {
			inStr = String.format("%.2f KByte", in / 1024);
		} else if (in < (1024 * 1024 * 1024)) {
			inStr = String.format("%.2f MByte", in / (1024 * 1024));
		} else {
			inStr = String.format("%.2f GByte", in / (1024 * 1024 * 1024));
		}

		return inStr;
	}

	public long getFolderSize(File dir) {
		long size = 0;
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				// System.out.println(file.getName() + " " + file.length());
				size += file.length();
			} else
				size += getFolderSize(file);
		}
		return size;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub

		if ("clear_cache".equals(preference.getKey())) {

			ImageLoader loader = MainActivity.getInstance().getImageLoader();
			loader.clearCache();
			MainActivity.getInstance().showToast("Clear Cache Done!");

		} else if ("pReset".equals(preference.getKey())) {
			Settings.setPrefsDefault(MainActivity.getInstance().getContext());
		}

		calcCacheSize();

		return false;
	}

}
