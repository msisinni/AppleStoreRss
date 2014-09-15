package com.signalinterrupts.applestorerss;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RssListFragment extends ListFragment {

	private RssCallbacks mRssCallbacks;
	private ArrayList<AppleApp> mAppleApps;
	private ImageDownloader<ImageView> mImageThread;
	private LruCache<String, Bitmap> mMemoryCache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActivity().setTitle(getString(R.string.list_fragment_title));

		setRetainInstance(true);
		mAppleApps = DataOrganizer.get(getActivity()).getAppleApps();
		if (mAppleApps == null) {
			new DownloadAppsTask().execute();
		} else {
			RssAdapter adapter = new RssAdapter(mAppleApps);
			setListAdapter(adapter);
		}

		mImageThread = new ImageDownloader<ImageView>(new Handler());
		mImageThread.setListener(new ImageDownloader.Listener<ImageView>() {
			@Override
			public void onImageDownloaded(ImageView imageView, String imageUrl, Bitmap bitmap) {
				if (isVisible()) { // make sure the Fragment shows the ImageView in question;
					imageView.setImageBitmap(bitmap);
					addBitmapToCache(imageUrl, bitmap);
				}
			}
		});
		mImageThread.start();
		mImageThread.getLooper();

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); // Memory in KB;
		final int cacheSize = Math.min(maxMemory / 8, 350);
		// 350 KB generously chosen as 25 images * ~12KB / image
		mMemoryCache = new LruCache<>(cacheSize);

	}

	private void addBitmapToCache(String imageUrl, Bitmap bitmap) {
		if (getBitmapFromCache(imageUrl) == null) {
			mMemoryCache.put(imageUrl, bitmap);
		}
	}

	private Bitmap getBitmapFromCache(String imageUrl) {
		if (imageUrl == null) {
			return null;
		}
		return mMemoryCache.get(imageUrl);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_rss_list, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		AppleApp appleApp = ((RssAdapter) getListAdapter()).getItem(position);
		mRssCallbacks.onAppSelected(appleApp);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAppleApps != null) {
			((RssAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_rss_list_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_favorites:
				////////////////////////////////////////////////////////////////////////////////////////
				// switch to favorites
				return true;
			case R.id.menu_item_refresh:
				///////////////////////////////////////////////////////////////////////////////////////
				// refresh list if internet connected
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mImageThread.clearQueue();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageThread.quit();
	}

	/**
	 * Interface and methods for clean phone && tablet UIs
	 */

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mRssCallbacks = (RssCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mRssCallbacks = null;
	}

	public void updateUi() {
		((RssAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public interface RssCallbacks {
		void onAppSelected(AppleApp appleApp);

		void onListItemUpdated(AppleApp appleApp);
	}

	private class RssAdapter extends ArrayAdapter<AppleApp> {
		public RssAdapter(ArrayList<AppleApp> appList) {
			super(getActivity(), 0, appList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_app, null);
			}

			final AppleApp appleApp = getItem(position);

			TextView appNameTextView = (TextView) convertView.findViewById(R.id.list_item_app_name);
			appNameTextView.setText(appleApp.getAppTitle());

			TextView appPriceTextView = (TextView) convertView.findViewById(R.id.list_item_app_price);
			appPriceTextView.setText(appleApp.getAppPrice());

			CheckBox favoriteCheckBox = (CheckBox) convertView.findViewById(R.id.expanded_app_favoriteCheckBox);
			favoriteCheckBox.setChecked(appleApp.isFavorite());
			favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					appleApp.setFavorite(isChecked);
					mRssCallbacks.onListItemUpdated(appleApp);
				}
			});

			ImageView appImageSmall = (ImageView) convertView.findViewById(R.id.list_item_app_picture);
			final Bitmap bitmap = getBitmapFromCache(appleApp.getImageUrlSmall());
			if (bitmap == null) { // download if not in cache;
				appImageSmall.setImageResource(R.drawable.loading_image_small);
				mImageThread.queueImage(appImageSmall, appleApp.getImageUrlSmall());
			} else { // grab from cache;
				appImageSmall.setImageBitmap(bitmap);
			}
			return convertView;
		}
	}

	private class DownloadAppsTask extends AsyncTask<Void, Void, ArrayList<AppleApp>> {
		@Override
		protected ArrayList<AppleApp> doInBackground(Void... params) {
			return new JsonDataPuller().fetchItems();
		}

		@Override
		protected void onPostExecute(ArrayList<AppleApp> appleApps) {
			mAppleApps = appleApps;
			RssAdapter adapter = new RssAdapter(mAppleApps);
			setListAdapter(adapter);
			DataOrganizer.get(getActivity()).setAppleApps(mAppleApps);
		}
	}
}
