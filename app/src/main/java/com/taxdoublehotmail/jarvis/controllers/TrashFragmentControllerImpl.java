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
import com.taxdoublehotmail.jarvis.utils.events.LatestTrashNotePositionEvent;
import com.taxdoublehotmail.jarvis.utils.events.LatestUpdatedNoteEvent;
import com.taxdoublehotmail.jarvis.views.MainActivityView;
import com.taxdoublehotmail.jarvis.views.TrashFragmentView;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TrashFragmentControllerImpl implements TrashFragmentController {
    public static final String TAG = TrashFragmentControllerImpl.class.getSimpleName();

    public static final String CURRENT_POSITION_PARCELABLE_KEY = TAG + ":" + "CurrentPositionParcelableKey";

    public static final int PAGINATION_SIZE = 25;

    private final MainActivityView mMainActivityView;
    private final TrashFragmentView mTrashFragmentView;
    private final NoteRepository mNoteRepository;

    private Subscription mRetrieveTrashNoteListByDateModifiedSubscription;
    private Subscription mRetrieveNoteByIdSubscription;
    private CompositeSubscription mDeleteNoteSubscription;

    private boolean mIsLoading;

    public TrashFragmentControllerImpl(MainActivityView mainActivityView, TrashFragmentView trashFragmentView, NoteRepository noteRepository) {
        mMainActivityView = mainActivityView;
        mTrashFragmentView = trashFragmentView;
        mNoteRepository = noteRepository;
    }

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);

        pullNextNotes(savedInstanceState);
    }

    @Subscribe
    public void onLatestTrashNotePositionEvent(LatestTrashNotePositionEvent event) {
        int position = event.getPosition();
        if (conditionPullNextNotes(position)) {
            pullNextNotes(null);
        }
    }

    @Subscribe
    public void onLatestUpdatedNoteEvent(LatestUpdatedNoteEvent event) {
        mTrashFragmentView.clearNotes();

        pullNextNotes(null);
    }

    @Override
    public void onDestroy() {
        if (mRetrieveTrashNoteListByDateModifiedSubscription != null) {
            mRetrieveTrashNoteListByDateModifiedSubscription.unsubscribe();
            mRetrieveTrashNoteListByDateModifiedSubscription = null;
        }
        if (mRetrieveNoteByIdSubscription != null) {
            mRetrieveNoteByIdSubscription.unsubscribe();
            mRetrieveNoteByIdSubscription = null;
        }
        if (mDeleteNoteSubscription != null) {
            mDeleteNoteSubscription.unsubscribe();
            mDeleteNoteSubscription = null;
        }

        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mTrashFragmentView.saveLayoutManager(outState, CURRENT_POSITION_PARCELABLE_KEY);
    }

    @Override
    public void onScrollToTopMenuItemClick() {
        mTrashFragmentView.scrollToTop();
    }

    @Override
    public void onDeleteAllMenuItemClick() {
        deleteAllTrashNotes();
    }

    @Override
    public void onNoteItemClick(int position) {
        Note temporaryNote = mTrashFragmentView.getNote(position);

        mTrashFragmentView.launchEditorActivity(temporaryNote);
    }

    @Override
    public void onNoteItemSwipe(int position) {
        Note currentNote = mTrashFragmentView.getNote(position);

        deleteNote(currentNote);
    }

    private boolean conditionPullNextNotes(int position) {
        return !mIsLoading && position > mTrashFragmentView.getNotesCount() - Math.log(mTrashFragmentView.getNotesCount());
    }

    private void pullNextNotes(final Bundle savedInstanceState) {
        if (mRetrieveTrashNoteListByDateModifiedSubscription != null) {
            mRetrieveTrashNoteListByDateModifiedSubscription.unsubscribe();
            mRetrieveTrashNoteListByDateModifiedSubscription = null;
        }

        mIsLoading = true;

        mRetrieveTrashNoteListByDateModifiedSubscription = mNoteRepository
                .retrieveTrashNoteListByDateModified(PAGINATION_SIZE, mTrashFragmentView.getNotesCount())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onCompleted() {
                        mIsLoading = false;

                        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION_PARCELABLE_KEY)) {
                            mTrashFragmentView.restoreLayoutManager(savedInstanceState, CURRENT_POSITION_PARCELABLE_KEY);

                            savedInstanceState.remove(CURRENT_POSITION_PARCELABLE_KEY);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "retrieveTrashNoteListByDateModified", e);
                        }

                        // TO DO.
                    }

                    @Override
                    public void onNext(Note aNote) {
                        mTrashFragmentView.addNote(aNote);
                    }
                });
    }

    private void deleteNote(final Note currentNote) {
        if (mDeleteNoteSubscription == null) {
            mDeleteNoteSubscription = new CompositeSubscription();
        }

        Note updatedNote = new NoteBuilder(currentNote)
                .setNoteState(NoteState.TRASH)
                .build();

        Subscription temporarySubscription = mNoteRepository
                .deleteNote(updatedNote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        mTrashFragmentView.showDeletedSnackbar();
                        mTrashFragmentView.removeNote(currentNote.getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "deleteNote", e);
                        }

                        // TO DO.
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        // Do Nothing.
                    }
                });

        mDeleteNoteSubscription.add(temporarySubscription);
    }

    private void deleteAllTrashNotes() {
        if (mDeleteNoteSubscription == null) {
            mDeleteNoteSubscription = new CompositeSubscription();
        }

        Subscription temporarySubscription = mNoteRepository
                .deleteTrashNoteList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        mTrashFragmentView.showDeletedAllSnackbar();
                        mTrashFragmentView.clearNotes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "deleteAllTrashNotes", e);
                        }

                        // TO DO.
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        // Do Nothing.
                    }
                });

        mDeleteNoteSubscription.add(temporarySubscription);
    }
}
