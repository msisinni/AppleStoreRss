package com.signalinterrupts.applestorerss;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RssListFragment extends ListFragment {

	private static final String TAG = "RssListFragment";

	protected static final String bundleString = "ListFragmentBundle";
	private RssCallbacks mRssCallbacks;
	private ArrayList<AppleApp> mAppleAppList;
	private ImageDownloader<ImageView> mImageThread;
	private LruCache<String, Bitmap> mMemoryCache;
	private DownloadAppsTask mDownloadAppsTask;
	protected RssAdapter mRssAdapter;

	private boolean inRssMode = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		setRetainInstance(true);

		Bundle bundle = getArguments();
		if (bundle != null) {
			inRssMode = bundle.getBoolean(bundleString);
			Log.d(TAG, "inRssMode = " + inRssMode);
		}

		Log.d(TAG, "Second inRssMode = " + inRssMode);
		if (inRssMode) {
			getActivity().setTitle(getString(R.string.list_fragment_title));
			mAppleAppList = DataOrganizer.get(getActivity()).getAppleAppList();
			if (mAppleAppList == null || mAppleAppList.isEmpty()) {
				mDownloadAppsTask = new DownloadAppsTask();
				mDownloadAppsTask.execute();
			} else {
				mRssAdapter = new RssAdapter(mAppleAppList);
				setListAdapter(mRssAdapter);
			}
		} else {
			Log.d(TAG, "Favorites");
			getActivity().setTitle(getString(R.string.list_fragment_title_favorites));
			mAppleAppList = DataOrganizer.get(getActivity()).getFavoriteAppList();
			if (mAppleAppList != null && !mAppleAppList.isEmpty()) {
				mRssAdapter = new RssAdapter(mAppleAppList);
				setListAdapter(mRssAdapter);
			}
		}

		mImageThread = new ImageDownloader<>(new Handler());
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
		View view = inflater.inflate(R.layout.fragment_rss_list, container, false);

		TextView emptyListTextView = (TextView) view.findViewById(R.id.empty_list_textView);

		Button refreshListButton = (Button) view.findViewById(R.id.empty_list_refreshButton);
		if (inRssMode) {
			emptyListTextView.setText(R.string.empty_list);
			refreshListButton.setText(R.string.refresh);
		} else {
			emptyListTextView.setText(R.string.empty_list_favorite);
			refreshListButton.setText(R.string.refresh_favorite);
		}
		refreshListButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshList();
			}
		});

		return view;
	}

	private void refreshList() {
		if (mDownloadAppsTask != null && mDownloadAppsTask.getStatus() != AsyncTask.Status.FINISHED) {
			mDownloadAppsTask.cancel(true);
		}
		inRssMode = true;
		DataOrganizer.get(getActivity()).updateFavoriteAppList();
		mDownloadAppsTask = new DownloadAppsTask();
		mDownloadAppsTask.execute();
		if (mAppleAppList != null) {
			((RssAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		AppleApp appleApp = ((RssAdapter) getListAdapter()).getItem(position);
		mRssCallbacks.onAppSelected(appleApp, inRssMode);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAppleAppList != null && !mAppleAppList.isEmpty()) {
			((RssAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_rss_list_menu, menu);
		MenuItem favoriteItem = menu.findItem(R.id.menu_item_favorite);
		MenuItem refreshItem = menu.findItem(R.id.menu_item_refresh);
		if (inRssMode) {
			favoriteItem.setTitle(R.string.menu_item_favorites);
			refreshItem.setVisible(true);
		} else {
			favoriteItem.setTitle(R.string.menu_item_list);
			refreshItem.setVisible(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_favorite:
				/*
				DataOrganizer.get(getActivity()).updateFavoriteAppList();
				((RssAdapter) getListAdapter()).clear();
				mAppleAppList = DataOrganizer.get(getActivity()).getFavoriteAppList();
				mRssAdapter = new RssAdapter(mAppleAppList);
				setListAdapter(mRssAdapter);
				*/
				DataOrganizer.get(getActivity()).updateFavoriteAppList();
				mImageThread.clearQueue();
				mImageThread.quit();
				if (mAppleAppList != null && !mAppleAppList.isEmpty()) {
					((RssAdapter) getListAdapter()).clear();
				}
				mRssCallbacks.onFavoritesSelected(inRssMode);
				return true;
			case R.id.menu_item_refresh:
				refreshList();
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
		void onAppSelected(AppleApp appleApp, boolean fromListToFavorites);

		void onListItemUpdated(AppleApp appleApp);

		void onFavoritesSelected(boolean fromListToFavorites);
	}

	private class RssAdapter extends ArrayAdapter<AppleApp> {
		public RssAdapter(ArrayList<AppleApp> appleAppList) {
			super(getActivity(), 0, appleAppList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mAppleAppList.isEmpty()) {
				return null;
			}
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_app, null);
			}

			final AppleApp appleApp = getItem(position);

			TextView appNameTextView = (TextView) convertView.findViewById(R.id.list_item_app_name);
			appNameTextView.setText(appleApp.getAppTitle());

			TextView appPriceTextView = (TextView) convertView.findViewById(R.id.list_item_app_price);
			appPriceTextView.setText(appleApp.getAppPrice());

			final CheckBox favoriteCheckBox = (CheckBox) convertView.findViewById(R.id.list_item_favoriteCheckBox);
			favoriteCheckBox.setChecked(appleApp.isFavorite());
			favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isChecked = favoriteCheckBox.isChecked();
					favoriteCheckBox.setChecked(isChecked);
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
		protected void onPostExecute(ArrayList<AppleApp> appleAppList) {
			DataOrganizer.get(getActivity()).setAppleAppList(appleAppList);
			DataOrganizer.get(getActivity()).initialCheckBoxes();
			mAppleAppList = DataOrganizer.get(getActivity()).getAppleAppList();
			mRssAdapter = new RssAdapter(mAppleAppList);
			setListAdapter(mRssAdapter);
		}
	}
}
