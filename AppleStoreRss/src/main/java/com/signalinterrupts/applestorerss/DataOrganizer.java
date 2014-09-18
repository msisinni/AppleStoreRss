package com.signalinterrupts.applestorerss;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataOrganizer {

	private static DataOrganizer sDataOrganizer;
	private ArrayList<AppleApp> mAppleAppList;
	private HashSet<AppleApp> mFavoriteAppSet = new HashSet<>();
	private Context mContext;

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

	public void addToFavorites() {
		for (AppleApp appleApp : mAppleAppList) { // add new favorites / replace existing (in case update to app store)
			if (appleApp.isFavorite()) {
				mFavoriteAppSet.add(appleApp);
			}
		}
	}

	public ArrayList<AppleApp> getFavoriteAppList() {
		if (!mFavoriteAppSet.isEmpty()) {
			addToFavorites();
			return new ArrayList<>(mFavoriteAppSet);
		} else {
			return null;
		}
	}

	public void setFavoriteAppSet(HashSet<AppleApp> favoriteAppSet) {
		mFavoriteAppSet = favoriteAppSet;
	}

}
