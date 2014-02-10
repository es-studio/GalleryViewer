package com.esstudio.gallery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fedorvlasov.lazylist.ImageLoader;

import android.R.array;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class WorkerImageScrap extends AsyncTask<Object, String, Void> {

	GridView mGview;
	ArrayList<ElementItem> mItems;
	Context context;
	int mIndex = 0;
	int mMaxIndex = 0;
	boolean isDone = false;
	boolean isMaxLimited = false;

	public WorkerImageScrap(Context c, GridView g, ArrayList<ElementItem> items) {
		this.mGview = g;
		this.mItems = items;
		this.context = c;

	}

	protected Void doInBackground(Object... parameter) {
		// .....get some data from internets
		// mYourItemsfromInternetSoruce = something you got from internet;

		// SystemClock.sleep(1000 * thCount);

		String baseUrl = parameter[0].toString();
		mMaxIndex = (Integer) parameter[1];
		mIndex = (Integer) parameter[2];
		int limit = (Integer) parameter[3];

		boolean reverse = Settings.getReverseMode();
		int r = reverse ? -1 : 1;
		int idx = 0;

		MainActivity.getInstance().setIndex(mIndex);

		log.out("Running..." + mIndex);
		int cnt = 0;
		StringBuffer sb = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		while (true) {
			sb.setLength(0);
			cnt = MainActivity.getCount();

			if (cnt < 0 || isCancelled()) {
				log.out("Cacnel : " + cnt + " " + isCancelled());
				break;
			}

			idx = (mIndex - (limit - cnt) * r);
			String url = baseUrl + idx;
			log.out("index " + idx + " cnt " + cnt);

			if (mMaxIndex - idx < 0) {
				log.out("Max Limit : " + cnt);
				isMaxLimited = true;

				break;
			}

			HttpGet get = new HttpGet(url);
			get.addHeader("Accept-Encoding", "gzip");
			get.addHeader("User-Agent", Settings.USER_AGENT);

			HttpResponse response;
			try {
				response = client.execute(get);
				GZIPInputStream gin = new GZIPInputStream(response.getEntity()
						.getContent());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						gin));

				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				get.abort();
			} catch (IOException e) {
				log.out("Fail : " + cnt);
			}

			WorkerLinkFinder.find(url, sb.toString());


			publishProgress(String.format("P:%d, M:%d, D:%d ...",
					mItems.size(), mMaxIndex, mMaxIndex - idx));

		}

		publishProgress(String.format("P:%d, M:%d, D:%d", mItems.size(),
				mMaxIndex, mMaxIndex - idx));

		isDone = true;
		// this.cancel(true);

		// if (mGview.getChildCount() > 0) {
		// MyImageView view = (MyImageView) mGview.getChildAt(0);
		// int h = view.getHeight();
		// int g = mGview.getHeight();
		// int c = mGview.getNumColumns();
		// int i = mItems.size();
		//
		// log.out("h " + h + " g " + g + " c " + c + " i " + i);
		//
		// if ((i / c + 1) * h < g) {
		// MainActivity.getInstance().nextImageScrap();
		// }
		//
		// }

		return null;
	}

	@Override
	protected void onCancelled(Void result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		isDone = true;
		log.out("Task Cancel");
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		ImageAdaptor adt = (ImageAdaptor) mGview.getAdapter();

		adt.notifyDataSetChanged();

		// adt.notifyDataSetInvalidated();

		MainActivity.getInstance().getActionBar().setSubtitle(values[0]);

	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		log.out("Task Done");
	}
	
}
