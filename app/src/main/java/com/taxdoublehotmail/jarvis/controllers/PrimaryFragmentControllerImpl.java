package com.taxdoublehotmail.jarvis.controllers;

import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.taxdoublehotmail.jarvis.BuildConfig;
import com.taxdoublehotmail.jarvis.models.Note;
import com.taxdoublehotmail.jarvis.models.NoteBuilder;
import com.taxdoublehotmail.jarvis.models.NoteRepository;
import com.taxdoublehotmail.jarvis.models.NoteState;
import com.taxdoublehotmail.jarvis.utils.BusProvider;
import com.taxdoublehotmail.jarvis.utils.events.LatestPrimaryNotePositionEvent;
import com.taxdoublehotmail.jarvis.utils.events.LatestUpdatedNoteEvent;
import com.taxdoublehotmail.jarvis.views.MainActivityView;
import com.taxdoublehotmail.jarvis.views.PrimaryFragmentView;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PrimaryFragmentControllerImpl implements PrimaryFragmentController {
    public static final String TAG = PrimaryFragmentControllerImpl.class.getSimpleName();

    public static final String CURRENT_POSITION_PARCELABLE_KEY = TAG + ":" + "CurrentPositionParcelableKey";

    public static final int PAGINATION_SIZE = 25;

    private final MainActivityView mMainActivityView;
    private final PrimaryFragmentView mPrimaryFragmentView;
    private final NoteRepository mNoteRepository;

    private Subscription mRetrievePrimaryNoteListByDateModifiedSubscription;
    private Subscription mRetrieveNoteByIdSubscription;
    private CompositeSubscription mUpdateNoteSubscription;

    private boolean mIsLoading;

    public PrimaryFragmentControllerImpl(MainActivityView mainActivityView, PrimaryFragmentView primaryFragmentView, NoteRepository noteRepository) {
        mMainActivityView = mainActivityView;
        mPrimaryFragmentView = primaryFragmentView;
        mNoteRepository = noteRepository;
    }

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);

        pullNextNotes(savedInstanceState);
    }

    @Subscribe
    public void onLatestPrimaryNotePositionEvent(LatestPrimaryNotePositionEvent event) {
        int position = event.getPosition();
        if (conditionPullNextNotes(position)) {
            pullNextNotes(null);
        }
    }

    @Subscribe
    public void onLatestUpdatedNoteEvent(LatestUpdatedNoteEvent event) {
        mPrimaryFragmentView.clearNotes();

        pullNextNotes(null);
    }

    @Override
    public void onDestroy() {
        if (mRetrievePrimaryNoteListByDateModifiedSubscription != null) {
            mRetrievePrimaryNoteListByDateModifiedSubscription.unsubscribe();
            mRetrievePrimaryNoteListByDateModifiedSubscription = null;
        }
        if (mRetrieveNoteByIdSubscription != null) {
            mRetrieveNoteByIdSubscription.unsubscribe();
            mRetrieveNoteByIdSubscription = null;
        }
        if (mUpdateNoteSubscription != null) {
            mUpdateNoteSubscription.unsubscribe();
            mUpdateNoteSubscription = null;
        }

        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mPrimaryFragmentView.saveLayoutManager(outState, CURRENT_POSITION_PARCELABLE_KEY);
    }

    @Override
    public void onScrollToTopMenuItemClick() {
        mPrimaryFragmentView.scrollToTop();
    }

    @Override
    public void onNoteItemClick(int position) {
        Note temporaryNote = mPrimaryFragmentView.getNote(position);

        mPrimaryFragmentView.launchEditorActivity(temporaryNote);
    }

    @Override
    public void onNoteItemSwipe(int position) {
        Note currentNote = mPrimaryFragmentView.getNote(position);

        updateNoteToArchive(currentNote);
    }

    private boolean conditionPullNextNotes(int position) {
        return !mIsLoading && position > mPrimaryFragmentView.getNotesCount() - Math.log(mPrimaryFragmentView.getNotesCount());
    }

    private void pullNextNotes(final Bundle savedInstanceState) {
        if (mRetrievePrimaryNoteListByDateModifiedSubscription != null) {
            mRetrievePrimaryNoteListByDateModifiedSubscription.unsubscribe();
            mRetrievePrimaryNoteListByDateModifiedSubscription = null;
        }

        mIsLoading = true;

        mRetrievePrimaryNoteListByDateModifiedSubscription = mNoteRepository
                .retrievePrimaryNoteListByDateModified(PAGINATION_SIZE, mPrimaryFragmentView.getNotesCount())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onCompleted() {
                        mIsLoading = false;

                        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION_PARCELABLE_KEY)) {
                            mPrimaryFragmentView.restoreLayoutManager(savedInstanceState, CURRENT_POSITION_PARCELABLE_KEY);

                            savedInstanceState.remove(CURRENT_POSITION_PARCELABLE_KEY);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "retrievePrimaryNoteListByDateModified", e);
                        }

                        // TO DO.
                    }

                    @Override
                    public void onNext(Note aNote) {
                        mPrimaryFragmentView.addNote(aNote);
                    }
                });
    }

    private void updateNoteToArchive(final Note currentNote) {
        if (mUpdateNoteSubscription == null) {
            mUpdateNoteSubscription = new CompositeSubscription();
        }

        Note updatedNote = new NoteBuilder(currentNote)
                .setNoteState(NoteState.ARCHIVE)
                .build();

        Subscription temporarySubscription = mNoteRepository
                .updateNote(updatedNote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        mPrimaryFragmentView.showArchivedSnackbar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "updateNote", e);
                        }

                        // TO DO.
                    }

                    @Override
                    public void onNext(Long aLong) {
                        mPrimaryFragmentView.removeNote(aLong);
                    }
                });

        mUpdateNoteSubscription.add(temporarySubscription);
    }
}