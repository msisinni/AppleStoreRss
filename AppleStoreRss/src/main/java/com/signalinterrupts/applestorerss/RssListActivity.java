package com.signalinterrupts.applestorerss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;


public class RssListActivity extends ActionBarActivity implements RssListFragment.RssCallbacks, ExpandedAppFragment.ExpandedCallbacks {

	///////////////////////////////Need to update ExpandedAppFragment when favorites checkbox clicked in list

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_masterdetail);
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
		if (fragment == null) {
			fragment = new RssListFragment();
			fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
	}

	@Override
	public void onAppSelected(AppleApp appleApp) {
		if (findViewById(R.id.detailFragmentContainer) == null) {    // Start an instance of ExpandedAppActivity;
			Intent intent = new Intent(this, ExpandedAppActivity.class);
			intent.putExtra(ExpandedAppFragment.EXTRA_APP_TITLE, appleApp.getAppTitle());
			startActivity(intent);
		} else { // Replace the current ExpandedAppFragment on tablets;
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
}
