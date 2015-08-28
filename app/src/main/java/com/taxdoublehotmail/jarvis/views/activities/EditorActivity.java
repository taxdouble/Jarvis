package com.taxdoublehotmail.jarvis.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.taxdoublehotmail.jarvis.JarvisApplication;
import com.taxdoublehotmail.jarvis.R;
import com.taxdoublehotmail.jarvis.controllers.EditorActivityController;
import com.taxdoublehotmail.jarvis.views.EditorActivityView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditorActivity extends AppCompatActivity implements EditorActivityView {
    public static final String TAG = EditorActivity.class.getSimpleName();

    public static final String NOTE_ARGUMENT_KEY = TAG + ":" + "NoteArgumentKey";

    public static final DateFormat EDITOR_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private EditorActivityController mEditorActivityController;

    private Toolbar mEditorActivityToolBar;
    private EditText mEditorActivityTitleEditText;
    private EditText mEditorActivityContentEditText;
    private TextView mEditorActivityDateModifiedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeEditorActivityController();

        setContentView(R.layout.activity_editor);

        initializeToolBar();
        initializeTitleEditText();
        initializeContentEditText();
        initializeDateModifiedTextView();

        mEditorActivityController.onCreate(savedInstanceState);

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            mEditorActivityController.onIntent(currentIntent);

            currentIntent.removeExtra(NOTE_ARGUMENT_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        mEditorActivityController.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mEditorActivityController.onDestroy();
    }

    public void initializeEditorActivityController() {
        JarvisApplication temporaryClerkApplication = (JarvisApplication)getApplication();

        mEditorActivityController = temporaryClerkApplication.getEditorActivityController(EditorActivity.this);
    }

    public void initializeToolBar() {
        mEditorActivityToolBar = (Toolbar)findViewById(R.id.editorActivityToolBar);
        mEditorActivityToolBar.setTitle(R.string.activity_editor_name);

        setSupportActionBar(mEditorActivityToolBar);

        ActionBar temporaryActionBar = getSupportActionBar();
        if (temporaryActionBar != null) {
            temporaryActionBar.setHomeButtonEnabled(true);
            temporaryActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void initializeTitleEditText() {
        mEditorActivityTitleEditText = (EditText)findViewById(R.id.editorActivityTitleEditText);
        mEditorActivityTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do Nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEditorActivityController.onTitleEditTextChanged(s.toString());
            }
        });
    }

    public void initializeContentEditText() {
        mEditorActivityContentEditText = (EditText)findViewById(R.id.editorActivityContentEditText);
        mEditorActivityContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do Nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEditorActivityController.onContentEditTextChanged(s.toString());
            }
        });
    }

    public void initializeDateModifiedTextView() {
        mEditorActivityDateModifiedTextView = (TextView)findViewById(R.id.editorActivityDateModifiedTextView);
        mEditorActivityDateModifiedTextView.setText(EDITOR_DATE_FORMAT.format(new Date(System.currentTimeMillis())));
    }

    @Override
    public String getNoteTitle() {
        return mEditorActivityTitleEditText.getText().toString();
    }

    @Override
    public String getNoteContent() {
        return mEditorActivityContentEditText.getText().toString();
    }

    @Override
    public void setNoteTitle(String currentTitle) {
        mEditorActivityTitleEditText.setText(currentTitle);
    }

    @Override
    public void setNoteContent(String currentContent) {
        mEditorActivityContentEditText.setText(currentContent);
    }

    @Override
    public void setNoteDate(Date currentDate) {
        mEditorActivityDateModifiedTextView.setText(EDITOR_DATE_FORMAT.format(currentDate));
    }
}
