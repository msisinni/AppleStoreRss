package com.signalinterrupts.applestorerss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class RssListActivity extends ActionBarActivity implements RssListFragment.RssCallbacks, ExpandedAppFragment.ExpandedCallbacks {

	private static final String FAVORITES_SAVED = "favorites";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		boolean favoritesSet = preferences.getBoolean(FAVORITES_SAVED, false);
		if (favoritesSet) {
			new LoadFavoritesTask().execute();
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
		List<AppleApp> favoriteAppList = DataOrganizer.get(getApplicationContext()).getFavoriteAppList();
		if (favoriteAppList != null) {
			new SaveFavoritesTask().execute();
		}
	}

	@Override
	public void onAppSelected(AppleApp appleApp) {
		if (findViewById(R.id.detailFragmentContainer) == null) {    // Start an instance of ExpandedAppActivity;
			Intent intent = new Intent(this, ExpandedAppActivity.class);
			intent.putExtra(ExpandedAppActivity.JSON_OR_FAVORITE, true); // True == appSelected was from the JSON list, not favorites;
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

	@Override
	public void onAppUpdated(AppleApp appleApp) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		RssListFragment listFragment = (RssListFragment) fragmentManager.findFragmentById(R.id.fragmentContainer);
		listFragment.updateUi();
	}

	@Override
	public void onExpandedAppUpdated(AppleApp appleApp) {
		coordinateCheckBoxes(appleApp);
	}

	private void coordinateCheckBoxes(AppleApp appleApp) {
		if (findViewById(R.id.detailFragmentContainer) != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			RssListFragment listFragment = (RssListFragment) fragmentManager.findFragmentById(R.id.fragmentContainer);
			listFragment.updateUi();
		}
	}

	private class LoadFavoritesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			FavoritesDatabase favoritesDatabase = new FavoritesDatabase(getApplicationContext());
			DataOrganizer.get(getApplicationContext()).setFavoriteAppSet(favoritesDatabase.loadFavorites());
			return null;
		}
	}

	private class SaveFavoritesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			DataOrganizer.get(getApplicationContext()).updateFavoriteAppList();
			FavoritesDatabase favoritesDatabase = new FavoritesDatabase(getApplicationContext());
			favoritesDatabase.saveFavorites(DataOrganizer.get(getApplicationContext()).getFavoriteAppList());
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(FAVORITES_SAVED, true);
			editor.commit();
		}
	}
}
