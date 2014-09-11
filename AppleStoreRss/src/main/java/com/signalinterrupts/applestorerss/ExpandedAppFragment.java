package com.signalinterrupts.applestorerss;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private TextView mTitleTextView;
	private TextView mCompanyNameTextView;
	private CheckBox mFavoriteCheckBox;


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

		mAppImageView = (ImageView) view.findViewById(R.id.expanded_app_imageView);
		// Update when downloading portion complete; //////////////////////////////////////////////////

		// Create onClickListeners for TextViews ////////////////////////////////////////////////////////
		mTitleTextView = (TextView) view.findViewById(R.id.expanded_app_titleTextView);
		mTitleTextView.setText(mAppleApp.getAppTitle());

		mCompanyNameTextView = (TextView) view.findViewById(R.id.expanded_app_companyNameTextView);
		mCompanyNameTextView.setText(mAppleApp.getCompanyName());

		mFavoriteCheckBox = (CheckBox) view.findViewById(R.id.expanded_app_favoriteCheckBox);
		mFavoriteCheckBox.setChecked(mAppleApp.isFavorite());
		mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mAppleApp.setFavorite(isChecked);
				mExpandedCallbacks.onAppUpdated(mAppleApp);
			}
		});

		return view;
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
		/*
		// Check if not null first?
		Activity parent = getActivity().getParent();
		super.onAttach(parent);
		mExpandedCallbacks = (ExpandedCallbacks) parent;
		 */
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
