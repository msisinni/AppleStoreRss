package com.signalinterrupts.applestorerss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
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
		}

		Log.i(TAG, "inRssMode = " + inRssMode);
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
					DataOrganizer.get(getActivity()).addBitmapToCache(imageUrl, bitmap);
				}
			}
		});
		mImageThread.start();
		mImageThread.getLooper();


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
				if (inRssMode) {
					refreshList();
				} else {
					switchListMode();
				}
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
		updateUi();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		AppleApp appleApp = ((RssAdapter) getListAdapter()).getItem(position);
		mRssCallbacks.onAppSelected(appleApp, inRssMode);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUi();
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
				switchListMode();
				return true;
			case R.id.menu_item_refresh:
				refreshList();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	public void switchListMode() {
		mImageThread.clearQueue();
		mImageThread.quit();
		DataOrganizer.get(getActivity()).updateFavoriteAppList();
		if (mRssAdapter != null) {
			mRssAdapter.clear();
		}
		mRssCallbacks.onFavoritesSelected(inRssMode);
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

	public void updateUi() {
		if (mAppleAppList != null && !mAppleAppList.isEmpty()) {
			((RssAdapter) getListAdapter()).notifyDataSetChanged();
		}
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

	public interface RssCallbacks {
		void onAppSelected(AppleApp appleApp, boolean fromListToFavorites);

		void onListItemUpdated(AppleApp appleApp);

		void onFavoritesSelected(boolean fromListToFavorites);
	}

	private class RssAdapter extends ArrayAdapter<AppleApp> {
		public RssAdapter(ArrayList<AppleApp> appleAppList) {
			super(getActivity(), 0, appleAppList);
		}

		@SuppressLint("InflateParams")
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
			//if (appImageSmall.getVisibility() == View.VISIBLE) {
			final Bitmap bitmap = DataOrganizer.get(getActivity()).getBitmapFromCache(appleApp.getImageUrlSmall());
			if (bitmap == null) { // download if not in cache;
				appImageSmall.setImageResource(R.drawable.loading_image_small);
				mImageThread.queueImage(appImageSmall, appleApp.getImageUrlSmall());
			} else { // grab from cache;
				appImageSmall.setImageBitmap(bitmap);
			}
			//}

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
