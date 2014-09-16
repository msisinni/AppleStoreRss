package com.signalinterrupts.applestorerss;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageDownloader<Token> extends HandlerThread {
	private static final String TAG = "ImageDownloader";
	private static final int MESSAGE_DOWNLOAD = 0;

	Handler mHandler;
	Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
	Handler mResponseHandler;
	Listener<Token> mListener;

	public ImageDownloader(Handler responseHandler) {
		super(TAG);
		mResponseHandler = responseHandler;
	}

	@SuppressLint("HandlerLeak")
	@Override
	protected void onLooperPrepared() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MESSAGE_DOWNLOAD) {
					@SuppressWarnings("unchecked")
					Token token = (Token) msg.obj;
					Log.i(TAG, "Got a request for url:  " + requestMap.get(token));
					handleRequest(token);
				}
			}
		};
	}

	public void queueImage(Token token, String url) {
		Log.i(TAG, "Got a URL:  " + url);
		requestMap.put(token, url);

		mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
	}

	private void handleRequest(final Token token) {
		try {
			final String imageUrl = requestMap.get(token);
			if (imageUrl == null) {
				return;
			}

			byte[] bitmapBytes = new JsonDataPuller().getUrlBytes(imageUrl);
			final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			bitmap.setDensity(DisplayMetrics.DENSITY_HIGH);
			Log.i(TAG, "Bitmap created");

			mResponseHandler.post(new Runnable() {
				@Override
				public void run() {
					if (requestMap.get(token) != imageUrl) {
						return;
					}
					requestMap.remove(token);
					mListener.onImageDownloaded(token, imageUrl, bitmap);
				}
			});

		} catch (IOException e) {
			Log.e(TAG, "Error downloading image", e);
		}
	}

	public void clearQueue() {
		mHandler.removeMessages(MESSAGE_DOWNLOAD);
		requestMap.clear();
	}

	public void setListener(Listener<Token> listener) {
		mListener = listener;
	}

	public interface Listener<Token> {
		void onImageDownloaded(Token token, String imageUrl, Bitmap bitmap);
	}

}
