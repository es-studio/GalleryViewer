package com.esstudio.gallery.gridview;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.esstudio.gallery.MainActivity;
import com.esstudio.gallery.Settings;
import com.esstudio.gallery.util.log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ImageClickListener implements OnItemClickListener {

	private Context c;
	private ArrayList<ElementItem> mAllItems;
	private ElementItem mItems;

	public ImageClickListener(Context c, ArrayList<ElementItem> item) {
		this.c = c;
		this.mAllItems = item;
	}

	public void openImage() {
		Context c = MainActivity.getInstance();

		File file = new File(
                Settings.getCacheDirectory(c), ""
				// + String.valueOf(AeSimpleSHA1.SHA1(mItems
				// + String.valueOf(Utils.getMD5(mItems.getImageUrl())));
						+ new HashCodeFileNameGenerator().generate(mItems
								.getImageUrl()));

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

	String imageFileName;
	String imageFileUrl;

	public void downloadImage() {

		final String imageUrl = mItems.getImageUrl();
		final String compareString = imageUrl.substring(
				imageUrl.indexOf("no=") + 3, imageUrl.length());

		imageFileName = mItems.getUrl().substring(
				mItems.getUrl().indexOf("no=") + 3, mItems.getUrl().length())
				+ ".jpg";
		imageFileUrl = mItems.getImageUrl();

		// get origin image file and url
		if (imageUrl.toLowerCase().matches(".*jpg.*|.*gif.*|.*png.*") == false) {

			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Accept-Encoding", "gzip");
			client.addHeader("User-Agent", Settings.USER_AGENT_SAFARI);
			client.get(
					"http://gall.dcinside.com/board/view/?id="
							+ mItems.getGalName() + "&no=" + mItems.getNum(),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {

							Document doc = Jsoup.parse(response);

							int s = imageUrl.indexOf("no=") + 3;
							int e = imageUrl.contains("&f_no") ? imageUrl
									.indexOf("&f_no") : imageUrl.length();

							String compareString = imageUrl.substring(s, e);

							Elements viewImage = doc.select("div.s_write img");

							for (Iterator iterator = viewImage.iterator(); iterator
									.hasNext();) {
								Element element = (Element) iterator.next();

								String viewUrl = element.attr("src");

								String comString = viewUrl.substring(
										viewUrl.indexOf("no=") + 3,
										viewUrl.length());

								log.out(compareString);
								log.out(comString);

								if (compareString.equals(comString)) {

									// find view image from img onclick

									// find windo.open
									String onclickString = element
											.attr("onClick");

									// on img
									if (onclickString.length() > 0) {

										// onclickString.indexOf("window.open('")
										int start = onclickString
												.indexOf("no=") + 13;
										int end = onclickString.indexOf("'",
												start);
										log.out(onclickString);
										log.out(start + " " + end);

										compareString = onclickString
												.substring(start, end);
									}

									// on a
									else {

										onclickString = element.parent().attr(
												"onclick");
										if (onclickString.length() > 0) {

											// onclickString.indexOf("window.open('")
											int start = onclickString
													.indexOf("no=") + 13;
											int end = onclickString.indexOf(
													"&f_no", start);
											log.out(onclickString);
											log.out(start + " " + end);

											compareString = onclickString
													.substring(start, end);
										}

									}
								}

							}
							log.out(compareString);

							Elements imageFileBox = doc
									.select("div.box_file li a");

							for (Iterator iterator = imageFileBox.iterator(); iterator
									.hasNext();) {
								Element element = (Element) iterator.next();
								if (element.attr("href")
										.contains(compareString)) {
									imageFileName = element.text();
									imageFileUrl = element.attr("href");
									log.out(imageFileName);
									log.out(imageFileUrl);
									
									imageFileUrl = imageFileUrl.replace("download.php",
											"viewimageM.php");

									break;
								}
							}

							download(imageFileUrl, imageFileName);


						}
					});

		} else {
			download(imageFileUrl, imageFileName);
		}

	}

	private void download(String url, String name) {

		log.out("download : " + url + "  " + name);

		DownloadManager dnManager = (DownloadManager) MainActivity
				.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);

		Uri Download_Uri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(
				Download_Uri);

		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
				| DownloadManager.Request.NETWORK_MOBILE);
		request.setAllowedOverRoaming(false);
		request.setTitle(mItems.getTitle());
		request.setDescription(mItems.getImageUrl());
		request.setNotificationVisibility(View.VISIBLE);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd_HH-mm-ss");
		String now = simpleDateFormat.format(new Date(System
				.currentTimeMillis()));

		File downDir = null;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			downDir = new File(Settings.getDownloadDirectory(c), now + "_" + name);

		request.setDestinationUri(Uri.fromFile(downDir));

		IntentFilter completeFilter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		MainActivity.getInstance().registerReceiver(new DownloadCompleteBroadcastReceiver(c, name),
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
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			logFile = Settings.getLogDirectory(c);

			try {
				FileWriter fw = new FileWriter(logFile, true);
				fw.write(url + "\n");
				fw.flush();
				fw.close();

				MainActivity.getInstance().showToast(
						"URL : \"" + url + "\" Added for debug");

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
		alert.setTitle(mItems.getNum()); // Set Alert dialog title
		alert.setItems(new String[] { "Open Browser", "Open Image",
				"Download Image", "Link...", "Add URL for Debug" },
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

        log.out(MainActivity.getInstance().getGalleyName());
        if(MainActivity.getInstance().getGalleyName().equals("My Gallery")){
            openImage();
        }else{
            showDialog();
        }

	}
}
