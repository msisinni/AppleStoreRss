package com.signalinterrupts.applestorerss;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class DataPuller extends AsyncTask<String, String, String> {
	private final static String appleRssUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topgrossingapplications/sf=143441/limit=25/json";

	@Override
	protected String doInBackground(String... args) {


		return null;
	}

	public InputStream getUrlData(String url) throws URISyntaxException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet method = new HttpGet(new URI(url));
		HttpResponse res = client.execute(method);

		return res.getEntity().getContent();
	}


}
