package com.esstudio.gallary;

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

public class ThreadTest extends AsyncTask<Object, String, Void> {

	GridView mGview;
	ArrayList<ElementItem> mItems;
	Context context;
	int mIndex = 0;
	int mMaxIndex = 0;
	boolean isDone = false;
	boolean isMaxLimited = false;

	public ThreadTest(Context c, GridView g, ArrayList<ElementItem> items) {
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

			Document doc = Jsoup.parse(sb.toString());
			Elements contentElement = doc.select("div.m_contents");
			Elements imageUrl = contentElement.select("img[src]");
			Elements titleEl = doc.select("p.contens_title");
			Elements bodyEl = doc.select("div#memo_img");
			Elements commentEl = doc.select("div.m_list_text");

			// String title = "";
			// if (titleEl.size() > 0) {
			// title = titleEl.text();
			// }

			// no contents
			if (contentElement.isEmpty()) {
				log.out("No Contents : " + url);
				continue;
			}

			String contentAll = titleEl.text() + " " + bodyEl.html() + " "
					+ commentEl.text();

			// match keyword check
			boolean keyword = Keyword(contentAll);
			// boolean keyword = Keyword(titleEl.text() + bodyEl.text()
			// + commentEl.text());

			// find magnet
			ArrayList<String> magnets = findMagnet(contentAll);
			ArrayList<String> youtube = findYoutube(contentAll);
			ArrayList<String> mp4 = findMp4(contentAll);

			if (magnets.size() + youtube.size() + mp4.size() > 0)
				keyword = true;

			if (imageUrl.isEmpty() && keyword == true) {

				ElementItem el = new ElementItem();
				el.setTitle(titleEl.text());
				el.setBody(bodyEl.text());
				el.setmComment(commentEl.text());
				el.setUrl(url);
				el.setOnKeyword(keyword);
				el.setImageUrl("0");
				el.setmTorrent(magnets);
				el.setmYoutube(youtube);
				el.setmMP4(mp4);
				MainActivity.getInstance().addElementItem(el);

			} else {

				for (Iterator iterator = imageUrl.iterator(); iterator
						.hasNext();) {
					Element element = (Element) iterator.next();
					String src = element.attr("src");

					log.out(src);
					src = src.replace("//dcimg1", "//image");

					ElementItem el = new ElementItem();
					el.setTitle(titleEl.text());
					el.setImageUrl(src);
					el.setBody(bodyEl.text());
					el.setmComment(commentEl.text());
					el.setUrl(url);
					el.setOnKeyword(keyword);
					el.setmTorrent(magnets);
					el.setmYoutube(youtube);
					el.setmMP4(mp4);

					MainActivity.getInstance().addElementItem(el);

				}
			}

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
//		adt.notifyDataSetInvalidated();

		MainActivity.getInstance().getActionBar().setSubtitle(values[0]);

	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		log.out("Task Done");
	}

	public boolean Keyword(String str) {
		// System.out.println(str);
		if (str == null) {
			str = "";
		}
		String tmp = "";
		Pattern p1 = Pattern.compile(
		// "(07|08|09|10|11|12|13|14|15).?(01|02|03|04|05|06|07|08|09|10|11|12).?[0-9]{2}[^0-9]|"
		// // 날짜 검
				"마그넷|자석|magnet|gnet:|urn|torrent|torr|rent|기차|직캠|픽짜|토렝|토랭|토렌트|토런트|토렌|토런|"
						+ "flvs.daum.net|www.youtube.com|"
						+ "스티큐브|꾸러미|\\.avi|\\.mkv|\\.tp|\\.ts|\\.mp4|\\.flv|\\.mov|\\.wmv|\\.swf");

		// magnet:?xt=urn:btih:
		// Pattern p1 =
		// Pattern.compile("마그넷|자석|magnet|torrent|torr|rent|기차|직캠|픽짜|토렝|토렌트|토런트|토렌|토런|스티큐브|꾸러미|avi|mkv|tp|ts|mp4");
		Matcher m1 = p1.matcher(str);

		StringBuffer stringBuffer = new StringBuffer();
		while (m1.find()) {
			tmp = m1.group();
			stringBuffer.append(tmp + ", ");
		}

		return !tmp.equals("");
	}

	public ArrayList<String> findMagnet(String str) {

		ArrayList<String> links = new ArrayList<String>();
		if (str == null) {
			str = "";
		}

		String tmp = "";
		Pattern p1 = Pattern.compile("urn:\\w*:\\w*\\s|urn:\\w*:\\w*\\&");

		Matcher m1 = p1.matcher(str);

		while (m1.find()) {
			tmp = m1.group();
			links.add("magnet:?xt=" + tmp.trim());
			log.out("magnet = " + tmp);
		}

		return links;
	}

	public ArrayList<String> findYoutube(String str) {

		ArrayList<String> links = new ArrayList<String>();
		if (str == null) {
			str = "";
		}

		String tmp = "";
		Pattern p1 = Pattern
				.compile(""
						+ "//www.youtube.com/v/.{11}" + "|"
						+ "//www.youtube.com/watch\\?v=[a-z0-9A-Z-_]*" + "|"
						+ "//youtube.com/v/[a-z0-9A-Z-_]*" + "|"
						+ "//www.youtube-nocookie.com/v/[a-z0-9A-Z-_]*" + "|"
						+ "//www.youtube.com/watch\\?list=[a-z0-9A-Z-_&=]*\\&v=[a-z0-9A-Z-_&=]*"
				);
		Matcher m1 = p1.matcher(str);

		while (m1.find()) {
			tmp = m1.group();
			links.add("http:" + tmp.trim());
			log.out("youtube = " + tmp);
		}

		return links;
	}

	public ArrayList<String> findMp4(String str) {

		ArrayList<String> links = new ArrayList<String>();
		if (str == null) {
			str = "";
		}

		String tmp = "";
		Pattern p1 = Pattern.compile(""
				+ "http://[^;|^\"]*\\.mp4|http://[^;|^\"]*\\.flv|http://mgnet.me/\\w*");
		Matcher m1 = p1.matcher(str);

		while (m1.find()) {
			tmp = m1.group();
			links.add(tmp.trim());

			System.out.println(tmp);
		}

		return links;
	}
}
