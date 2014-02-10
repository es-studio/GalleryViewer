package com.fedorvlasov.lazylist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esstudio.gallery.MainActivity;
import com.esstudio.gallery.Settings;

import android.R;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class ImageLoader {

	private MemoryCache memoryCache = new MemoryCache();
	private FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private Handler handler = new Handler();// handler to display images in UI thread

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// final int stub_id = R.drawable.stub;

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);

		} else {
			queuePhoto(url, imageView);
			imageView.setImageBitmap(null);
			// imageView.setImageResource(R.drawable.ic_dialog_alert);
		}

	}

	// public static void ImageViewAnimatedChange(Context c, final ImageView v,
	// final Bitmap new_image) {
	// final Animation anim_out = AnimationUtils.loadAnimation(c,
	// android.R.anim.fade_out);
	// final Animation anim_in = AnimationUtils.loadAnimation(c,
	// android.R.anim.fade_in);
	// anim_out.setAnimationListener(new AnimationListener()
	// {
	// @Override public void onAnimationStart(Animation animation) {}
	// @Override public void onAnimationRepeat(Animation animation) {}
	// @Override public void onAnimationEnd(Animation animation)
	// {
	// v.setImageBitmap(new_image);
	// anim_in.setAnimationListener(new AnimationListener() {
	// @Override public void onAnimationStart(Animation animation) {}
	// @Override public void onAnimationRepeat(Animation animation) {}
	// @Override public void onAnimationEnd(Animation animation) {}
	// });
	// v.startAnimation(anim_in);
	// }
	// });
	// v.startAnimation(anim_out);
	// }

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web

			try {
				Bitmap bitmap = null;

				URL imageUrl = new URL(url);

				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				conn.addRequestProperty("Connection", "close");
				conn.addRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) "
								+ "AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");

				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
				OutputStream os = new FileOutputStream(f);
				Utils.CopyStream(is, os);

				os.close();
				conn.disconnect();
				bitmap = decodeFile(f);
				return bitmap;
			} catch (Throwable ex) {
				// ex.printStackTrace();
				if (ex instanceof OutOfMemoryError)
					memoryCache.clear();
				// return null;
			}
		return null;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.

			int tWidth = MainActivity.getInstance().getThumbnailWidth();

			double calc = (Math.log(tWidth) / Math.log(2));
			int REQUIRED_SIZE = (int) Math.pow(2, calc);

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;
				Bitmap bmp = getBitmap(photoToLoad.url);
				memoryCache.put(photoToLoad.url, bmp);
				if (imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			// else
			// photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
