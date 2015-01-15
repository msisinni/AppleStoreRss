package com.signalinterrupts.applestorerss;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class AppleApp {

	private final String mAppTitle;
	private final String mAppPrice;
	private final String mSummary;
	private final String mCopyright;
	private final String mCompanyName;
	private final String mCompanyLink;
	private final String mStoreLink;
	private final String mDate;
	private final String mImageUrlSmall;
	private final String mImageUrlBig;
	private final String mGenre;
	private final String mGenreLink;
	private boolean mFavorite;

	public String getAppTitle() {
		return mAppTitle;
	}

	public String getAppPrice() {
		return mAppPrice;
	}

	public String getSummary() {
		return mSummary;
	}

	public String getCopyright() {
		return mCopyright;
	}

	public String getCompanyName() {
		return mCompanyName;
	}

	public String getCompanyLink() {
		return mCompanyLink;
	}

	public String getStoreLink() {
		return mStoreLink;
	}

	public String getDate() {
		return mDate;
	}

	public String getImageUrlSmall() {
		return mImageUrlSmall;
	}

	public String getImageUrlBig() {
		return mImageUrlBig;
	}

	public String getGenre() {
		return mGenre;
	}

	public String getGenreLink() {
		return mGenreLink;
	}

	public boolean isFavorite() {
		return mFavorite;
	}

	public void setFavorite(boolean favorite) {
		mFavorite = favorite;
	}

	public static class Builder {
		private final String bAppTitle;
		private String bAppPrice;
		private String bSummary;
		private String bCopyright;
		private String bCompanyName;
		private String bCompanyLink;
		private String bStoreLink;
		private String bDate;
		private String bImageUrlSmall;
		private String bImageUrlBig;
		private String bGenre;
		private String bGenreLink;

		public Builder(String appTitle) {
			bAppTitle = appTitle;
		}

		// @formatter:off
		public Builder appPrice(String s) { bAppPrice = s; return this; }
		public Builder summary(String s) { bSummary = s; return this; }
		public Builder copyright(String s) { bCopyright = s; return this; }
		public Builder companyName(String s) { bCompanyName = s; return this; }
		public Builder companyLink(String s) { bCompanyLink = s; return this; }
		public Builder storeLink(String s) { bStoreLink = s; return this; }
		public Builder date(String s) { bDate = s; return this; }
		public Builder imageUrlSmall(String s) { bImageUrlSmall = s; return this; }
		public Builder imageUrlBig(String s) { bImageUrlBig = s; return this; }
		public Builder genre(String s) { bGenre = s; return this; }
		public Builder genreLink(String s) { bGenreLink = s; return this; }
		// @formatter:on

		public AppleApp build() {
			return new AppleApp(this);
		}
	}

	private AppleApp(Builder builder) {
		mAppTitle = builder.bAppTitle;
		mAppPrice = builder.bAppPrice;
		mSummary = builder.bSummary;
		mCopyright = builder.bCopyright;
		mCompanyName = builder.bCompanyName;
		mCompanyLink = builder.bCompanyLink;
		mStoreLink = builder.bStoreLink;
		mDate = builder.bDate;
		mImageUrlSmall = builder.bImageUrlSmall;
		mImageUrlBig = builder.bImageUrlBig;
		mGenre = builder.bGenre;
		mGenreLink = builder.bGenreLink;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		AppleApp otherAppleApp = (AppleApp) other;
		return mAppTitle.equals(otherAppleApp.getAppTitle());
	}

	/*
	Thoughts for further development:  add int field to AppleApps for easier sorting instead of sorting by Title;
	Less resource intensive than comparing Strings?;
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + mAppTitle.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return mAppTitle;
	}
}