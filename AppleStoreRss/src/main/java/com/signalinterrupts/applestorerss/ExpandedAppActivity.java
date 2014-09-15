package com.signalinterrupts.applestorerss;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import java.util.ArrayList;

public class ExpandedAppActivity extends ActionBarActivity implements ExpandedAppFragment.ExpandedCallbacks {

	private ArrayList<AppleApp> mAppleApps;
	private ImageDownloader<ImageView> mExpandedImageThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewPager mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);

		mAppleApps = DataOrganizer.get(this).getAppleApps();

		FragmentManager fragmentManager = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
			@Override
			public Fragment getItem(int i) {
				AppleApp appleApp = mAppleApps.get(i);
				return ExpandedAppFragment.newInstance(appleApp.getAppTitle());
			}

			@Override
			public int getCount() {
				return mAppleApps.size();
			}


		});
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {
			}

			@Override
			public void onPageSelected(int i) {
				AppleApp appleApp = mAppleApps.get(i);
				if (appleApp.getAppTitle() != null) {
					setTitle(appleApp.getAppTitle());
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});

		mExpandedImageThread = new ImageDownloader<>(new Handler());
		mExpandedImageThread.setListener(new ImageDownloader.Listener<ImageView>() {
			@Override
			public void onImageDownloaded(ImageView imageView, String imageUrl, Bitmap bitmap) {
				imageView.setImageBitmap(bitmap);
			}
		});
		mExpandedImageThread.start();
		mExpandedImageThread.getLooper();

		String appTitle = getIntent().getStringExtra(ExpandedAppFragment.EXTRA_APP_TITLE);
		for (int i = 0; i < mAppleApps.size(); i++) {
			if (mAppleApps.get(i).getAppTitle().equals(appTitle)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mExpandedImageThread.clearQueue();
		mExpandedImageThread.quit();
	}

	@Override
	public void onAppUpdated(AppleApp appleApp) {
		// method only useful in RssListActivity
	}

	@Override
	public void onExpandedAppUpdated(AppleApp appleApp) {
		// method only useful in RssListActivity
	}

	@Override
	public void loadBigImage(AppleApp appleApp, ImageView imageView) {
		mExpandedImageThread.queueImage(imageView, appleApp.getImageUrlBig());
	}
}
