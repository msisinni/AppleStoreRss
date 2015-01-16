package com.signalinterrupts.applestorerss;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum DataOrganizer {
	INSTANCE;

	private List<AppleApp> mAppleAppList;
	private Set<AppleApp> mFavoriteAppSet = new HashSet<>();

	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); // Memory in KB;
	final int cacheSize = Math.min(maxMemory / 8, 350);
	// 350 KB generously chosen as 25 images * ~12KB / image + some extra;
	private LruCache<String, Bitmap> mMemoryCache = new LruCache<>(cacheSize);

	private DataOrganizer() {
	}

	public static DataOrganizer get() {
		return INSTANCE;
	}

	public List<AppleApp> getAppleAppList() {
		return mAppleAppList;
	}

	public void setAppleAppList(List<AppleApp> appleAppList) {
		mAppleAppList = appleAppList;
	}

	public void initialCheckBoxes() {
		if (!mFavoriteAppSet.isEmpty()) {
			for (AppleApp appleApp : mAppleAppList) { // At setup, if newly downloaded app currently in favorite list, flags app as favorite too;
				if (mFavoriteAppSet.contains(appleApp)) {
					appleApp.setFavorite(true);
				}
			}
		}
	}

	public AppleApp getAppleApp(String appTitle) {
		for (AppleApp appleApp : mAppleAppList) {
			if (appleApp.getAppTitle().equals(appTitle)) {
				return appleApp;
			}
		}
		for (AppleApp appleApp : mFavoriteAppSet) {
			if (appleApp.getAppTitle().equals(appTitle)) {
				return appleApp;
			}
		}
		return null;
	}

	public void updateFavoriteAppList() {
		Set<AppleApp> removalSet = new HashSet<>();
		for (AppleApp appleApp : mFavoriteAppSet) { // get rid of unchecked favorites from favorites set
			if (!appleApp.isFavorite()) {
				removalSet.add(appleApp);
			}
		}
		mFavoriteAppSet.removeAll(removalSet);

		for (AppleApp appleApp : mAppleAppList) { // add new favorites / replace existing (in case update to app store) from main list to favorites set
			if (appleApp.isFavorite()) {
				mFavoriteAppSet.add(appleApp);
			} else if (mFavoriteAppSet.contains(appleApp)) {
				mFavoriteAppSet.remove(appleApp);
			}
		}
	}

	public ArrayList<AppleApp> getFavoriteAppList() {
		if (!mFavoriteAppSet.isEmpty()) {
			return new ArrayList<>(mFavoriteAppSet);
		} else {
			return null;
		}
	}

	public void setFavoriteAppSet(Set<AppleApp> favoriteAppSet) {
		mFavoriteAppSet = favoriteAppSet;
	}

	public void addBitmapToCache(String imageUrl, Bitmap bitmap) {
		if (getBitmapFromCache(imageUrl) == null) {
			mMemoryCache.put(imageUrl, bitmap);
		}
	}

	public Bitmap getBitmapFromCache(String imageUrl) {
		if (imageUrl == null) {
			return null;
		}
		return mMemoryCache.get(imageUrl);
	}

}
