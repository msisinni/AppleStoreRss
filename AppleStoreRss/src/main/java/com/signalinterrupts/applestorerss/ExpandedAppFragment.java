package com.signalinterrupts.applestorerss;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.UUID;

public class ExpandedAppFragment extends Fragment {

	public static final String EXTRA_APP_ID = "applestorerss.app_id";

	private AppleApp mAppleApp;
	private ExpandedCallbacks mExpandedCallbacks;
	private ImageView mAppImageView;
	private CheckBox mFavoriteCheckBox;
	private TextView mSummaryTextView;
	private TextView mDateTextView;
	private TextView mCopyrightTextView;


	public static ExpandedAppFragment newInstance(UUID appId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_APP_ID, appId);

		ExpandedAppFragment fragment = new ExpandedAppFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID appId = (UUID) getArguments().getSerializable(EXTRA_APP_ID);
		mAppleApp = DataOrganizer.get(getActivity()).getAppleApp(appId);
		/////////////////////////////////////////////////////Check on this ///////////////////////////////// --v
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_expanded_app, container, false);

		final String storeLink = mAppleApp.getStoreLink();
		final String companyLink = mAppleApp.getCompanyLink();
		final String genreLink = mAppleApp.getGenreLink();

		mAppImageView = (ImageView) view.findViewById(R.id.expanded_app_imageView);
		// Update when downloading portion complete; //////////////////////////////////////////////////

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
				mExpandedCallbacks.onAppUpdated(mAppleApp);
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

		mSummaryTextView = (TextView) view.findViewById(R.id.expanded_app_summaryTextView);
		mSummaryTextView.setText(mAppleApp.getSummary());

		mDateTextView = (TextView) view.findViewById(R.id.expanded_app_dateTextView);
		mDateTextView.setText(mAppleApp.getDate());

		mCopyrightTextView = (TextView) view.findViewById(R.id.expanded_app_copyrightTextView);
		mCopyrightTextView.setText(mAppleApp.getCopyright());

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
			case "Music":
				return R.drawable.genre_music;
			case "Social Networking":
				return R.drawable.genre_social;
			default:
				return R.drawable.genre_default;
		}
	}

	private void shareAppFunction() {

	}

	public void updateFavorite(UUID id) {
		if (mAppleApp.getId().equals(id)) {
			mFavoriteCheckBox.setChecked(!mFavoriteCheckBox.isChecked());
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
		void onAppUpdated(AppleApp appleApp);
	}


}
