package com.taxdoublehotmail.jarvis.controllers;

import android.os.Bundle;

public interface PrimaryFragmentController {
    void onViewCreated(Bundle savedInstanceState);

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

    void onScrollToTopMenuItemClick();

    void onNoteItemClick(int position);

    void onNoteItemSwipe(int position);
}
