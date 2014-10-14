package com.signalinterrupts.applestorerss;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class JsonDataPuller {
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

			int bytesRead;
			byte[] buffer = new byte[1024];
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
		ArrayList<AppleApp> appArrayList = new ArrayList<>();
		try {
			String jsonString = getUrl(APPLE_RSS_URL);
			Log.i(TAG, "Url:  " + APPLE_RSS_URL);
			appArrayList = parseApps(jsonString);
		} catch (IOException e) {
			Log.e(TAG, "Failed to fetch items", e);
		}

		return appArrayList;
	}

	private ArrayList<AppleApp> parseApps(String jsonOutput) {
		ArrayList<AppleApp> appleAppList = new ArrayList<>(25); // update number if link changed
		// could parse number from JSON url, but that might get messy if the link is changing anyway;
		try {
			JSONObject jsonObject = new JSONObject(jsonOutput);
			jsonObject = jsonObject.getJSONObject("feed");
			JSONArray jsonArray = jsonObject.getJSONArray("entry");
			for (int i = 0; i < jsonArray.length(); i++) {
				appleAppList.add(jsonToAppleApp(jsonArray.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return appleAppList;
	}

	private AppleApp jsonToAppleApp(JSONObject jsonObject) throws JSONException {
		final String label = "label";
		final String attributes = "attributes";

		JSONObject jsonElement;
		jsonElement = jsonObject.getJSONObject("im:name");
		AppleApp.Builder builder = new AppleApp.Builder(jsonElement.getString(label)); // appTitle

		JSONArray jsonArray = jsonObject.getJSONArray("im:image");
		jsonElement = (JSONObject) jsonArray.get(0);
		builder.imageUrlSmall(jsonElement.getString(label));

		jsonElement = (JSONObject) jsonArray.get(2);
		builder.imageUrlBig(jsonElement.getString(label));

		jsonElement = jsonObject.getJSONObject("summary");
		builder.summary(jsonElement.getString(label));

		jsonElement = jsonObject.getJSONObject("im:price");
		builder.appPrice(jsonElement.getString(label));

		jsonElement = jsonObject.getJSONObject("rights");
		builder.copyright(jsonElement.getString(label));

		jsonElement = jsonObject.getJSONObject("id");
		builder.storeLink(jsonElement.getString(label));

		jsonElement = jsonObject.getJSONObject("im:artist");
		builder.companyName(jsonElement.getString(label));

		jsonElement = jsonElement.getJSONObject(attributes);
		builder.companyLink(jsonElement.getString("href"));

		jsonElement = jsonObject.getJSONObject("category");
		jsonElement = jsonElement.getJSONObject(attributes);
		builder.genre(jsonElement.getString("term"));

		builder.genreLink(jsonElement.getString("scheme"));

		jsonElement = jsonObject.getJSONObject("im:releaseDate");
		jsonElement = jsonElement.getJSONObject(attributes);
		builder.date(jsonElement.getString(label));

		return builder.build();
	}

}
