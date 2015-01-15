package com.signalinterrupts.applestorerss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.List;

public class RssListActivity extends ActionBarActivity implements RssListFragment.RssCallbacks, ExpandedAppFragment.ExpandedCallbacks {

	private static final String SHARED_PREFERENCES_STRING = "AppleRssPreferences";
	private static final String FAVORITES_SAVED = "favorites";
	private static final String TAG = "RssListActivity";
	private DataOrganizer mDataOrganizer = DataOrganizer.get();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = this.getSharedPreferences(SHARED_PREFERENCES_STRING, MODE_PRIVATE);
		boolean favoritesSet = preferences.getBoolean(FAVORITES_SAVED, false);
		if (favoritesSet) {
			new LoadFavoritesTask().execute();
			Log.i(TAG, "Loading favorites");
		} else {
			Log.i(TAG, "No favorites to load");
		}

		setContentView(R.layout.activity_masterdetail);
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
		if (fragment == null) {
			fragment = new RssListFragment();
			fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDataOrganizer.updateFavoriteAppList();
		List<AppleApp> favoriteAppList = mDataOrganizer.getFavoriteAppList();
		if (favoriteAppList != null && !favoriteAppList.isEmpty()) {
			new SaveFavoritesTask().execute();
			Log.i(TAG, "Saving favorites"); // Leaving these in;
			Log.i(TAG, favoriteAppList.toString());
		} else {
			Log.i(TAG, "No favorites to save");
			SharedPreferences preferences = this.getSharedPreferences(SHARED_PREFERENCES_STRING, MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(FAVORITES_SAVED, false);
			editor.apply();
		}
	}

	@Override
	public void onAppSelected(AppleApp appleApp, boolean fromRssToFavorites) {
		if (findViewById(R.id.detailFragmentContainer) == null) {    // Start an instance of ExpandedAppActivity;
			Intent intent = new Intent(this, ExpandedAppActivity.class);
			intent.putExtra(ExpandedAppActivity.RSS_OR_FAVORITE, fromRssToFavorites); // True == appSelected was from the RSS list, not favorites;
			intent.putExtra(ExpandedAppFragment.EXTRA_APP_TITLE, appleApp.getAppTitle());
			startActivity(intent);
		} else { // Replace the current (or add) ExpandedAppFragment on tablets;
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			Fragment oldDetail = fragmentManager.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = ExpandedAppFragment.newInstance(appleApp.getAppTitle());

			if (oldDetail != null) {
				fragmentTransaction.remove(oldDetail);
			}

			fragmentTransaction.add(R.id.detailFragmentContainer, newDetail);
			fragmentTransaction.commit();
		}
	}

	@Override
	public void onListItemUpdated(AppleApp appleApp) {
		if (findViewById(R.id.detailFragmentContainer) != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			ExpandedAppFragment appFragment = (ExpandedAppFragment) fragmentManager.findFragmentById(R.id.detailFragmentContainer);
			if (appFragment != null) {
				appFragment.updateFavorite(appleApp.getAppTitle());
			}
		}
	}

	private static Fragment storedFragment;

	@Override
	public void onFavoritesSelected(boolean fromRssToFavorites) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
		Fragment newFragment;
		if (storedFragment != null) {
			newFragment = storedFragment;
		} else {
			newFragment = new RssListFragment();
		}
		if (currentFragment != null) {
			storedFragment = currentFragment;
			fragmentTransaction.remove(currentFragment);
		}
		Bundle bundle = new Bundle();
		bundle.putBoolean(RssListFragment.bundleString, !fromRssToFavorites); // put opposite of boolean in bundle
		newFragment.setArguments(bundle);

		if (findViewById(R.id.detailFragmentContainer) != null) {
			Fragment expandedDetail = fragmentManager.findFragmentById(R.id.detailFragmentContainer);
			if (expandedDetail != null) {
				fragmentTransaction.remove(expandedDetail);
			}
		}
		fragmentTransaction.add(R.id.fragmentContainer, newFragment).commit();

	}

	@Override
	public void onAppUpdated() {
		coordinateUi();
	}

	private void coordinateUi() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		RssListFragment listFragment = (RssListFragment) fragmentManager.findFragmentById(R.id.fragmentContainer);
		listFragment.updateUi();
	}

	private class LoadFavoritesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			FavoritesDatabase favoritesDatabase = new FavoritesDatabase(getApplicationContext());
			mDataOrganizer.setFavoriteAppSet(favoritesDatabase.loadFavorites());
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			Log.i(TAG, "Favorites loaded");
			Log.i(TAG, mDataOrganizer.getFavoriteAppList().toString());
		}
	}

	private class SaveFavoritesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			FavoritesDatabase favoritesDatabase = new FavoritesDatabase(getApplicationContext());
			favoritesDatabase.saveFavorites(mDataOrganizer.getFavoriteAppList());
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			SharedPreferences preferences = RssListActivity.this.getSharedPreferences(SHARED_PREFERENCES_STRING, MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(FAVORITES_SAVED, true);
			editor.apply();
			Log.i(TAG, "Favorites saved");
		}
	}
}
