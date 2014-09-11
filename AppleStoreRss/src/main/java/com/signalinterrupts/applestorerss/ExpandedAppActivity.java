package com.signalinterrupts.applestorerss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

public class ExpandedAppActivity extends ActionBarActivity {

	private ArrayList<AppleApp> mAppleApps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_expanded_app);
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
		if (fragment == null) {
			fragment = new ExpandedAppFragment();
			fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
	}
}
