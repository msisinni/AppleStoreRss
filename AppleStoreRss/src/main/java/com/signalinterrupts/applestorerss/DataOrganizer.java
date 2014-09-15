package com.signalinterrupts.applestorerss;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

public class DataOrganizer {

	private static DataOrganizer sDataOrganizer;
	private ArrayList<AppleApp> mAppleApps;
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

	public ArrayList<AppleApp> getAppleApps() {
		return mAppleApps;
	}

	public void setAppleApps(ArrayList<AppleApp> appleApps) {
		mAppleApps = appleApps;
	}

	public AppleApp getAppleApp(String appTitle) {
		for (AppleApp appleApp : mAppleApps) {
			if (appleApp.getAppTitle().equals(appTitle)) {
				return appleApp;
			}
		}
		return null;
	}



}
