package com.signalinterrupts.applestorerss;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class ExpandedAppFragment extends Fragment {
	private static final String TAG = "ExpandedAppFragment";
	public static final String EXTRA_APP_TITLE = "applestorerss.app_title";

	private AppleApp mAppleApp;
	private ExpandedCallbacks mExpandedCallbacks;
	private ImageView mAppImageView;
	private CheckBox mFavoriteCheckBox;

	public static ExpandedAppFragment newInstance(String appTitle) {
		Bundle args = new Bundle();
		args.putString(EXTRA_APP_TITLE, appTitle);

		ExpandedAppFragment fragment = new ExpandedAppFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String appTitle = getArguments().getString(EXTRA_APP_TITLE);
		mAppleApp = DataOrganizer.get(getActivity()).getAppleApp(appTitle);
		setHasOptionsMenu(false);
		new DownloadImagesTask().execute();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_expanded_app, container, false);

		final String storeLink = mAppleApp.getStoreLink();
		final String companyLink = mAppleApp.getCompanyLink();
		final String genreLink = mAppleApp.getGenreLink();

		mAppImageView = (ImageView) view.findViewById(R.id.expanded_app_imageView);
		mAppImageView.setImageResource(R.drawable.loading_image_large);

		// Could make one OnClickListener for all webpage openers, but too messy with switch based on view ids imo;
		TextView titleTextView = (TextView) view.findViewById(R.id.expanded_app_titleTextView);
		titleTextView.setText(mAppleApp.getAppTitle());
		titleTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWebPage(storeLink);
			}
		});

		TextView companyNameTextView = (TextView) view.findViewById(R.id.expanded_app_companyNameTextView);
		companyNameTextView.setText(mAppleApp.getCompanyName());
		companyNameTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWebPage(companyLink);
			}
		});

		mFavoriteCheckBox = (CheckBox) view.findViewById(R.id.expanded_app_favoriteCheckBox);
		mFavoriteCheckBox.setChecked(mAppleApp.isFavorite());
		mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mAppleApp.setFavorite(isChecked);
				mExpandedCallbacks.onAppUpdated();
			}
		});

		Button purchaseButton = (Button) view.findViewById(R.id.expanded_app_purchaseButton);
		purchaseButton.setText(mAppleApp.getAppPrice());
		purchaseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWebPage(storeLink);
			}
		});

		ImageView genreImageView = (ImageView) view.findViewById(R.id.expanded_app_genreImageView);
		// set appropriate image for genre of app;
		genreImageView.setImageResource(genreImageSwitch(mAppleApp.getGenre()));
		genreImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWebPage(genreLink);
			}
		});

		TextView genreTextView = (TextView) view.findViewById(R.id.expanded_app_genreTextView);
		genreTextView.setText(mAppleApp.getGenre());
		genreTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWebPage(genreLink);
			}
		});

		ImageView shareImageView = (ImageView) view.findViewById(R.id.expanded_app_shareImageView);
		shareImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareAppFunction();
			}
		});

		TextView shareTextView = (TextView) view.findViewById(R.id.expanded_app_shareTextView);
		shareTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareAppFunction();
			}
		});

		TextView summaryTextView = (TextView) view.findViewById(R.id.expanded_app_summaryTextView);
		summaryTextView.setText(mAppleApp.getSummary());

		TextView dateTextView = (TextView) view.findViewById(R.id.expanded_app_dateTextView);
		dateTextView.setText(mAppleApp.getDate());

		TextView copyrightTextView = (TextView) view.findViewById(R.id.expanded_app_copyrightTextView);
		copyrightTextView.setText(mAppleApp.getCopyright());

		return view;
	}

	private void openWebPage(String link) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(link));
		startActivity(intent);
	}

	private int genreImageSwitch(String genreName) {
		switch (genreName) {
			case "Games":
				return R.drawable.genre_games;
			case "Sports":
				return R.drawable.genre_sports;
			case "Music":
				return R.drawable.genre_music;
			case "Social Networking":
				return R.drawable.genre_social;
			case "Productivity":
				return R.drawable.genre_productivity;
			case "Utilities":
				return R.drawable.genre_utilities;
			default:
				return R.drawable.genre_default;
		}
	}

	private void shareAppFunction() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_string) + mAppleApp.getAppTitle());
		intent.putExtra(Intent.EXTRA_TEXT, mAppleApp.getStoreLink());
		intent.setType("text/plain");
		startActivity(Intent.createChooser(intent, getString(R.string.share_string) + mAppleApp.getAppTitle()));
	}

	public void updateFavorite(String appTitle) {
		if (mAppleApp.getAppTitle().equals(appTitle)) {
			mFavoriteCheckBox.setChecked(mAppleApp.isFavorite());
		}
	}

	private class DownloadImagesTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				byte[] bitmapBytes = new JsonDataPuller().getUrlBytes(mAppleApp.getImageUrlBig());
				Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
				bitmap.setDensity(DisplayMetrics.DENSITY_HIGH);
				return bitmap;
			} catch (IOException e) {
				Log.e(TAG, "Error downloading image", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isVisible()) {
				mAppImageView.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * Required interface for hosting activities;
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mExpandedCallbacks = (ExpandedCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mExpandedCallbacks = null;
	}

	public interface ExpandedCallbacks {
		void onAppUpdated();
	}

}
