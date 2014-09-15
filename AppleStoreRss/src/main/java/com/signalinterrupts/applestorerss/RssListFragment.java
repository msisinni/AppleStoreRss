package com.signalinterrupts.applestorerss;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RssListFragment extends ListFragment {

	private RssCallbacks mRssCallbacks;
	ArrayList<AppleApp> mAppleApps;

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

			return convertView;
		}
	}

	private class DownloadAppsTask extends AsyncTask<Void, Void, ArrayList<AppleApp>> {
		@Override
		protected ArrayList<AppleApp> doInBackground(Void... params) {
			return new DataPuller().fetchItems();
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
