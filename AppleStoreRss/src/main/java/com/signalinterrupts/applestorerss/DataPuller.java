package com.signalinterrupts.applestorerss;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonToken;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class DataPuller {
	private static final String TAG = "DataPuller";

	private static final String APPLE_RSS_URL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topgrossingapplications/sf=143441/limit=25/json";

	byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			InputStream inputStream = connection.getInputStream();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}

			int bytesRead = 0;
			byte[] buffer = new byte[2048];
			while ((bytesRead = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			return outputStream.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}

	public ArrayList<AppleApp> fetchItems() {
		ArrayList<AppleApp> items = new ArrayList<>();
		try {
			String jsonString = getUrl(APPLE_RSS_URL);
			Log.i(TAG, "Url:  " + APPLE_RSS_URL);
			Log.i(TAG, "Received JSON:  " + jsonString);

			items = parseApps(jsonString);
		} catch (IOException e) {
			Log.e(TAG, "Failed to fetch items", e);
		}

		return items;
	}

	private ArrayList<AppleApp> parseApps(String jsonOutput) {
		ArrayList<AppleApp> appleApps = new ArrayList<>(25); // update number if link changed
		try {
			JSONObject jsonObject = new JSONObject(jsonOutput);
			JSONObject appObject = jsonObject.getJSONObject("feed");
			JSONArray jsonArray = appObject.getJSONArray("entry");
			for (int i = 0; i < jsonArray.length(); i++) {
				appleApps.add(new AppleApp(jsonArray.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		return appleApps;
	}

}
