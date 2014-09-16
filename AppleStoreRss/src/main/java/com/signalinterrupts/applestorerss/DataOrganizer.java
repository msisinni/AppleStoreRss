package com.signalinterrupts.applestorerss;

import android.content.Context;

import java.util.ArrayList;

public class DataOrganizer {

	private static DataOrganizer sDataOrganizer;
	private ArrayList<AppleApp> mAppleAppList;
	private Context mContext;

	private DataOrganizer(Context context) {
		mContext = context;
		// loading here;
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

	public AppleApp getAppleApp(String appTitle) {
		for (AppleApp appleApp : mAppleAppList) {
			if (appleApp.getAppTitle().equals(appTitle)) {
				return appleApp;
			}
		}
		return null;
	}



}
