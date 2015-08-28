package com.taxdoublehotmail.jarvis.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

public interface EditorActivityController {
    void onCreate(Bundle savedInstanceState);

    void onIntent(Intent currentIntent);

    void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState);

    void onDestroy();

    void onTitleEditTextChanged(String newText);

    void onContentEditTextChanged(String newText);
}