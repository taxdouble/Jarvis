package com.taxdoublehotmail.jarvis.controllers;

import android.os.Bundle;

import com.taxdoublehotmail.jarvis.views.MainActivityView;

public class MainActivityControllerImpl implements MainActivityController {
    public static final String TAG = MainActivityControllerImpl.class.getSimpleName();

    public static final String CURRENT_FRAGMENT_PARCELABLE_KEY = TAG + ":" + "CurrentFragmentParcelableKey";

    private final MainActivityView mMainActivityView;

    public MainActivityControllerImpl(MainActivityView mainActivityView) {
        mMainActivityView = mainActivityView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_FRAGMENT_PARCELABLE_KEY)) {
                mMainActivityView.restoreCurrentFragment(savedInstanceState, CURRENT_FRAGMENT_PARCELABLE_KEY);

                savedInstanceState.remove(CURRENT_FRAGMENT_PARCELABLE_KEY);
            }

            return;
        }

        mMainActivityView.switchPrimaryFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMainActivityView.saveCurrentFragment(outState, CURRENT_FRAGMENT_PARCELABLE_KEY);
    }

    @Override
    public void onNavigationViewPrimarySelected() {
        mMainActivityView.switchPrimaryFragment();
    }

    @Override
    public void onNavigationViewArchiveSelected() {
        mMainActivityView.switchArchiveFragment();
    }

    @Override
    public void onNavigationViewTrashSelected() {
        mMainActivityView.switchTrashFragment();
    }
}
