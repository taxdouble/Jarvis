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
import com.taxdoublehotmail.jarvis.utils.events.LatestArchiveNotePositionEvent;
import com.taxdoublehotmail.jarvis.utils.events.LatestUpdatedNoteEvent;
import com.taxdoublehotmail.jarvis.views.ArchiveFragmentView;
import com.taxdoublehotmail.jarvis.views.MainActivityView;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ArchiveFragmentControllerImpl implements ArchiveFragmentController {
    public static final String TAG = ArchiveFragmentControllerImpl.class.getSimpleName();

    public static final String CURRENT_POSITION_PARCELABLE_KEY = TAG + ":" + "CurrentPositionParcelableKey";

    public static final int PAGINATION_SIZE = 25;

    private final MainActivityView mMainActivityView;
    private final ArchiveFragmentView mArchiveFragmentView;
    private final NoteRepository mNoteRepository;

    private Subscription mRetrieveArchiveNoteListByDateModifiedSubscription;
    private Subscription mRetrieveNoteByIdSubscription;
    private CompositeSubscription mUpdateNoteSubscription;

    private boolean mIsLoading;

    public ArchiveFragmentControllerImpl(MainActivityView mainActivityView, ArchiveFragmentView archiveFragmentView, NoteRepository noteRepository) {
        mMainActivityView = mainActivityView;
        mArchiveFragmentView = archiveFragmentView;
        mNoteRepository = noteRepository;
    }

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);

        pullNextNotes(savedInstanceState);
    }

    @Subscribe
    public void onLatestArchiveNotePositionEvent(LatestArchiveNotePositionEvent event) {
        int position = event.getPosition();
        if (conditionPullNextNotes(position)) {
            pullNextNotes(null);
        }
    }

    @Subscribe
    public void onLatestUpdatedNoteEvent(LatestUpdatedNoteEvent event) {
        mArchiveFragmentView.clearNotes();

        pullNextNotes(null);
    }

    @Override
    public void onDestroy() {
        if (mRetrieveArchiveNoteListByDateModifiedSubscription != null) {
            mRetrieveArchiveNoteListByDateModifiedSubscription.unsubscribe();
            mRetrieveArchiveNoteListByDateModifiedSubscription = null;
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
        mArchiveFragmentView.saveLayoutManager(outState, CURRENT_POSITION_PARCELABLE_KEY);
    }

    @Override
    public void onScrollToTopMenuItemClick() {
        mArchiveFragmentView.scrollToTop();
    }

    @Override
    public void onNoteItemClick(int position) {
        Note temporaryNote = mArchiveFragmentView.getNote(position);

        mArchiveFragmentView.launchEditorActivity(temporaryNote);
    }

    @Override
    public void onNoteItemSwipe(int position) {
        Note currentNote = mArchiveFragmentView.getNote(position);

        updateNoteToTrash(currentNote);
    }

    private boolean conditionPullNextNotes(int position) {
        return !mIsLoading && position > mArchiveFragmentView.getNotesCount() - Math.log(mArchiveFragmentView.getNotesCount());
    }

    private void pullNextNotes(final Bundle savedInstanceState) {
        if (mRetrieveArchiveNoteListByDateModifiedSubscription != null) {
            mRetrieveArchiveNoteListByDateModifiedSubscription.unsubscribe();
            mRetrieveArchiveNoteListByDateModifiedSubscription = null;
        }

        mIsLoading = true;

        mRetrieveArchiveNoteListByDateModifiedSubscription = mNoteRepository
                .retrieveArchiveNoteListByDateModified(PAGINATION_SIZE, mArchiveFragmentView.getNotesCount())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onCompleted() {
                        mIsLoading = false;

                        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION_PARCELABLE_KEY)) {
                            mArchiveFragmentView.restoreLayoutManager(savedInstanceState, CURRENT_POSITION_PARCELABLE_KEY);

                            savedInstanceState.remove(CURRENT_POSITION_PARCELABLE_KEY);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "retrieveArchiveNoteListByDateModified", e);
                        }

                        // TO DO.
                    }

                    @Override
                    public void onNext(Note aNote) {
                        mArchiveFragmentView.addNote(aNote);
                    }
                });
    }

    private void updateNoteToTrash(final Note currentNote) {
        if (mUpdateNoteSubscription == null) {
            mUpdateNoteSubscription = new CompositeSubscription();
        }

        Note updatedNote = new NoteBuilder(currentNote)
                .setNoteState(NoteState.TRASH)
                .build();

        Subscription temporarySubscription = mNoteRepository
                .updateNote(updatedNote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        mArchiveFragmentView.showTrashedSnackbar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "updateNoteToTrash", e);
                        }

                        // TO DO.
                    }

                    @Override
                    public void onNext(Long aLong) {
                        mArchiveFragmentView.removeNote(aLong);
                    }
                });

        mUpdateNoteSubscription.add(temporarySubscription);
    }
}
