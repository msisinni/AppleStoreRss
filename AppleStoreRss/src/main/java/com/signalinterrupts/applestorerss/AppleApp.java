package com.signalinterrupts.applestorerss;

import java.util.UUID;

public class AppleApp {

	private UUID mId;
	private String mAppName;
	private String mAppPrice;
	private boolean mFavorite;
	private String mSummary;
	private String mCopyright;
	private String mCompanyName;
	private String mCompanyLink;
	private String mStoreLink;
	private String mDate;
	private String mImageLinkSmall;
	private String mImageLinkBig;
	private String mGenre;
	private String mGenreLink;

	public AppleApp() {
		mId = UUID.randomUUID();
	}

	public UUID getId() {
		return mId;
	}

	public String getAppTitle() {
		return mAppName;
	}

	public void setAppName(String appName) {
		mAppName = appName;
	}

	public String getAppPrice() {
		return mAppPrice;
	}

	public void setAppPrice(String appPrice) {
		mAppPrice = appPrice;
	}

	public boolean isFavorite() {
		return mFavorite;
	}

	public void setFavorite(boolean favorite) {
		mFavorite = favorite;
	}

	// Below is stuff that is only displayed on the expanded view

	public String getSummary() {
		return mSummary;
	}

	public void setSummary(String summary) {
		mSummary = summary;
	}

	public String getCopyright() {
		return mCopyright;
	}

	public void setCopyright(String copyright) {
		mCopyright = copyright;
	}

	public String getCompanyName() {
		return mCompanyName;
	}

	public void setCompanyName(String companyName) {
		mCompanyName = companyName;
	}

	public String getCompanyLink() {
		return mCompanyLink;
	}

	public void setCompanyLink(String companyLink) {
		mCompanyLink = companyLink;
	}

	public String getStoreLink() {
		return mStoreLink;
	}

	public void setStoreLink(String storeLink) {
		mStoreLink = storeLink;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		mDate = date;
	}

	public String getImageLinkSmall() {
		return mImageLinkSmall;
	}

	public void setImageLinkSmall(String imageLinkSmall) {
		mImageLinkSmall = imageLinkSmall;
	}

	public String getImageLinkBig() {
		return mImageLinkBig;
	}

	public void setImageLinkBig(String imageLinkBig) {
		mImageLinkBig = imageLinkBig;
	}

	public String getGenre() {
		return mGenre;
	}

	public void setGenre(String genre) {
		mGenre = genre;
	}

	public String getGenreLink() {
		return mGenreLink;
	}

	public void setGenreLink(String genreLink) {
		mGenreLink = genreLink;
	}



}


