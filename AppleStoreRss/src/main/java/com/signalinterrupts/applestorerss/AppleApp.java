package com.signalinterrupts.applestorerss;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppleApp {

	private String mAppTitle;
	private String mAppPrice;
	private boolean mFavorite;
	private String mSummary;
	private String mCopyright;
	private String mCompanyName;
	private String mCompanyLink;
	private String mStoreLink;
	private String mDate;
	private String mImageUrlSmall;
	private String mImageUrlBig;
	private String mGenre;
	private String mGenreLink;

	public String getAppTitle() {
		return mAppTitle;
	}

	public String getAppPrice() {
		return mAppPrice;
	}

	public boolean isFavorite() {
		return mFavorite;
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

	public void setFavorite(boolean favorite) {
		mFavorite = favorite;
	}

	public static class Builder {
		private String bAppTitle;
		private String bAppPrice;
		private boolean bFavorite;
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
		public Builder favorite(boolean b) { bFavorite = b; return this; }
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
		mFavorite = builder.bFavorite;
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

	public AppleApp(JSONObject jsonObject) throws JSONException {
		final String label = "label";
		final String attributes = "attributes";

		JSONObject jsonElement;
		jsonElement = jsonObject.getJSONObject("im:name");
		mAppTitle = jsonElement.getString(label);

		JSONArray jsonArray = jsonObject.getJSONArray("im:image");
		jsonElement = (JSONObject) jsonArray.get(0);
		mImageUrlSmall = jsonElement.getString(label);

		jsonElement = (JSONObject) jsonArray.get(2);
		mImageUrlBig = jsonElement.getString(label);

		jsonElement = jsonObject.getJSONObject("summary");
		mSummary = jsonElement.getString(label);

		jsonElement = jsonObject.getJSONObject("im:price");
		mAppPrice = jsonElement.getString(label);

		jsonElement = jsonObject.getJSONObject("rights");
		mCopyright = jsonElement.getString(label);

		jsonElement = jsonObject.getJSONObject("id");
		mStoreLink = jsonElement.getString(label);

		jsonElement = jsonObject.getJSONObject("im:artist");
		mCompanyName = jsonElement.getString(label);
		jsonElement = jsonElement.getJSONObject(attributes);
		mCompanyLink = jsonElement.getString("href");

		jsonElement = jsonObject.getJSONObject("category");
		jsonElement = jsonElement.getJSONObject(attributes);
		mGenre = jsonElement.getString("term");
		mGenreLink = jsonElement.getString("scheme");

		jsonElement = jsonObject.getJSONObject("im:releaseDate");
		jsonElement = jsonElement.getJSONObject(attributes);
		mDate = jsonElement.getString(label);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		AppleApp otherAppleApp = (AppleApp) other;
		return mAppTitle.equals(otherAppleApp.getAppTitle());
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31*hash + mAppTitle.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return mAppTitle;
	}
}


