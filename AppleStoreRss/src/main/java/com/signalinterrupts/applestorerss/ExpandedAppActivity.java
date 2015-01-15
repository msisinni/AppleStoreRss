package com.signalinterrupts.applestorerss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

public class ExpandedAppActivity extends ActionBarActivity implements ExpandedAppFragment.ExpandedCallbacks {

	protected static final String RSS_OR_FAVORITE = "RorF";
	private ArrayList<AppleApp> mAppleAppList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewPager mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);

		// Following lines done so that paging is possible through either RSS or Favorite List
		if (getIntent().getBooleanExtra(RSS_OR_FAVORITE, true)) {
			mAppleAppList = DataOrganizer.get().getAppleAppList();
		} else {
			mAppleAppList = DataOrganizer.get().getFavoriteAppList();
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
			@Override
			public Fragment getItem(int i) {
				AppleApp appleApp = mAppleAppList.get(i);
				return ExpandedAppFragment.newInstance(appleApp.getAppTitle());
			}

			@Override
			public int getCount() {
				return mAppleAppList.size();
			}


		});
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {
			}

			@Override
			public void onPageSelected(int i) {
				AppleApp appleApp = mAppleAppList.get(i);
				if (appleApp.getAppTitle() != null) {
					setTitle(appleApp.getAppTitle());
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});

		String appTitle = getIntent().getStringExtra(ExpandedAppFragment.EXTRA_APP_TITLE);
		for (int i = 0; i < mAppleAppList.size(); i++) {
			if (mAppleAppList.get(i).getAppTitle().equals(appTitle)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}

	}

	@Override
	public void onAppUpdated() {
		// method only useful in RssListActivity
	}

}
