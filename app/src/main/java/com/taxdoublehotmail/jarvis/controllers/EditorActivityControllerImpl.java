package com.taxdoublehotmail.jarvis.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.taxdoublehotmail.jarvis.BuildConfig;
import com.taxdoublehotmail.jarvis.models.Note;
import com.taxdoublehotmail.jarvis.models.NoteBuilder;
import com.taxdoublehotmail.jarvis.models.NoteConstants;
import com.taxdoublehotmail.jarvis.models.NoteFactory;
import com.taxdoublehotmail.jarvis.models.NoteRepository;
import com.taxdoublehotmail.jarvis.utils.BusProvider;
import com.taxdoublehotmail.jarvis.utils.events.LatestUpdatedNoteEvent;
import com.taxdoublehotmail.jarvis.views.EditorActivityView;
import com.taxdoublehotmail.jarvis.views.activities.EditorActivity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class EditorActivityControllerImpl implements EditorActivityController {
    public static final String TAG = EditorActivityControllerImpl.class.getSimpleName();

    public static final String CURRENT_NOTE_PARCELABLE_KEY = TAG + ":" + "CurrentNoteParcelableKey";

    public static final int DEBOUNCE_TIMEOUT = 1000;

    private final EditorActivityView mEditorActivityView;
    private final NoteRepository mNoteRepository;

    private NoteBuilder mCurrentNoteBuilder;

    private Subscription mUpdateNoteSubscription;
    private PublishSubject<Observable<Note>> mUpdatedNotePublishSubject;

    public EditorActivityControllerImpl(EditorActivityView editorActivityView, NoteRepository noteRepository) {
        mEditorActivityView = editorActivityView;
        mNoteRepository = noteRepository;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCurrentNoteBuilder = new NoteBuilder(NoteFactory.createDefaultNote());

        initializeUpdatedNotePublishSubject();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_NOTE_PARCELABLE_KEY)) {
                Note temporaryNote = savedInstanceState.getParcelable(CURRENT_NOTE_PARCELABLE_KEY);
                if (temporaryNote != null) {
                    mCurrentNoteBuilder = new NoteBuilder(temporaryNote);

                    mEditorActivityView.setNoteTitle(temporaryNote.getTitle());
                    mEditorActivityView.setNoteContent(temporaryNote.getContent());
                    mEditorActivityView.setNoteDate(temporaryNote.getDateModified());
                }
            }
        }
    }

    @Override
    public void onIntent(Intent currentIntent) {
        if (currentIntent.hasExtra(EditorActivity.NOTE_ARGUMENT_KEY)) {
            Note temporaryNote = currentIntent.getParcelableExtra(EditorActivity.NOTE_ARGUMENT_KEY);
            if (temporaryNote != null) {
                mCurrentNoteBuilder = new NoteBuilder(temporaryNote);

                mEditorActivityView.setNoteTitle(temporaryNote.getTitle());
                mEditorActivityView.setNoteContent(temporaryNote.getContent());
                mEditorActivityView.setNoteDate(temporaryNote.getDateModified());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(CURRENT_NOTE_PARCELABLE_KEY, mCurrentNoteBuilder.build());
    }

    @Override
    public void onDestroy() {
        if (mUpdateNoteSubscription != null) {
            mUpdateNoteSubscription.unsubscribe();
            mUpdateNoteSubscription = null;
        }
    }

    @Override
    public void onTitleEditTextChanged(String newText) {
        Note currentNote = mCurrentNoteBuilder.build();

        if (!currentNote.getTitle().equals(newText)) {
            mCurrentNoteBuilder.setTitle(newText);
            mCurrentNoteBuilder.setDateModified(new Date(System.currentTimeMillis()));

            mUpdatedNotePublishSubject.onNext(Observable.just(mCurrentNoteBuilder.build()));

            mEditorActivityView.setNoteDate(new Date(System.currentTimeMillis()));
        }
    }

    @Override
    public void onContentEditTextChanged(String newText) {
        Note currentNote = mCurrentNoteBuilder.build();

        if (!currentNote.getContent().equals(newText)) {
            mCurrentNoteBuilder.setContent(newText);
            mCurrentNoteBuilder.setDateModified(new Date(System.currentTimeMillis()));

            mUpdatedNotePublishSubject.onNext(Observable.just(mCurrentNoteBuilder.build()));

            mEditorActivityView.setNoteDate(new Date(System.currentTimeMillis()));
        }
    }

    private void initializeUpdatedNotePublishSubject() {
        mUpdatedNotePublishSubject = PublishSubject.create();
        mUpdateNoteSubscription = Observable.switchOnNext(mUpdatedNotePublishSubject)
                .debounce(DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Note, Observable<? extends Long>>() {
                    @Override
                    public Observable<? extends Long> call(Note note) {
                        if (note.getId() == NoteConstants.getDefaultId()) {
                            return mNoteRepository.createNote(note);
                        } else {
                            return mNoteRepository.updateNote(note);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        // Do Nothing.
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "retrievePrimaryNoteListByDateModified", e);
                        }
                    }

                    @Override
                    public void onNext(Long aLong) {
                        mCurrentNoteBuilder.setId(aLong);

                        BusProvider.getInstance().post(new LatestUpdatedNoteEvent(aLong));
                    }
                });
    }
}
