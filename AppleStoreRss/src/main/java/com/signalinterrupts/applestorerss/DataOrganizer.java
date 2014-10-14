package com.signalinterrupts.applestorerss;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DataOrganizer {

	private static DataOrganizer sDataOrganizer;
	private static ArrayList<AppleApp> mAppleAppList;
	private static HashSet<AppleApp> mFavoriteAppSet = new HashSet<>();
	private Context mContext;

	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); // Memory in KB;
	final int cacheSize = Math.min(maxMemory / 8, 350);
	// 350 KB generously chosen as 25 images * ~12KB / image + some extra;
	private LruCache<String, Bitmap> mMemoryCache = new LruCache<>(cacheSize);

	private DataOrganizer(Context context) {
		mContext = context;
	}

	public static DataOrganizer get(Context context) {
		if (sDataOrganizer == null) {
			sDataOrganizer = new DataOrganizer(context.getApplicationContext());
		}
		return sDataOrganizer;
	}

	public ArrayList<AppleApp> getAppleAppList() {
		return mAppleAppList;
	}

	public void setAppleAppList(ArrayList<AppleApp> appleAppList) {
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
		for (AppleApp appleApp : mFavoriteAppSet) { // get rid of un-favorites
			if (!appleApp.isFavorite()) {
				removalSet.add(appleApp);
			}
		}
		mFavoriteAppSet.removeAll(removalSet);

		for (AppleApp appleApp : mAppleAppList) { // add new favorites / replace existing (in case update to app store)
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

	public void setFavoriteAppSet(HashSet<AppleApp> favoriteAppSet) {
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
