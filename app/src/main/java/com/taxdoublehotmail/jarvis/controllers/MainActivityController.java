package com.taxdoublehotmail.jarvis.controllers;

import android.os.Bundle;

public interface MainActivityController {
    void onCreate(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outState);

    void onNavigationViewPrimarySelected();

    void onNavigationViewArchiveSelected();

    void onNavigationViewTrashSelected();
}
