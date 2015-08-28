package com.taxdoublehotmail.jarvis.views;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public interface MainActivityView {
    void setToolbar(Toolbar toolbar);

    void saveCurrentFragment(Bundle outState, String parcelableKey);

    void restoreCurrentFragment(Bundle outState, String parcelableKey);

    void switchPrimaryFragment();

    void switchArchiveFragment();

    void switchTrashFragment();
}
