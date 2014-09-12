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
		mAppleApps = new ArrayList<>();
		for (int i = 0; i < 5; i++) {

			AppleApp.Builder appBuilder = new AppleApp.Builder();
			appBuilder.appTitle("App #" + i)
					.appPrice("$" + i + ".00")
					.favorite(i % 2 == 0)
					.summary("Blah\n\n\nBlah\n\n\n\n\nBlah\n\n\n\n\nBlah")
					.copyright("Copy " + i)
					.companyName("Company " + i)
					.companyLink("https://itunes.apple.com/us/artist/supercell/id488106216?mt=8&uo=2")
					.storeLink("https://itunes.apple.com/us/app/clash-of-clans/id529479190?mt=8&uo=2")
					.date("August 2, 2012")
					.imageUrlSmall("http://a260.phobos.apple.com/us/r30/Purple3/v4/57/f8/ef/57f8ef4e-a84c-e927-b3b6-4013fa75b0c7/mzl.qnshkfxv.53x53-50.png")
					.imageUrlBig("http://a421.phobos.apple.com/us/r30/Purple3/v4/57/f8/ef/57f8ef4e-a84c-e927-b3b6-4013fa75b0c7/mzl.qnshkfxv.100x100-75.png")
					.genre("Games")
					.genreLink("https://itunes.apple.com/us/genre/ios-games/id6014?mt=8&uo=2");
			AppleApp appleApp = appBuilder.build();

			mAppleApps.add(appleApp);

		}
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

	public AppleApp getAppleApp(UUID appId) {
		for (AppleApp appleApp : mAppleApps) {
			if (appleApp.getId().equals(appId)) {
				return appleApp;
			}
		}
		return null;
	}

}
