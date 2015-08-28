package com.taxdoublehotmail.jarvis.views;

import android.os.Bundle;

import com.taxdoublehotmail.jarvis.models.Note;

public interface TrashFragmentView {
    void scrollToTop();

    void saveLayoutManager(Bundle outState, String parcelableKey);

    void restoreLayoutManager(Bundle outState, String parcelableKey);

    void launchEditorActivity(Note currentNote);

    void showDeletedSnackbar();

    void showDeletedAllSnackbar();

    int getNotesCount();

    Note getNote(int position);

    void addNote(Note newNote);

    void removeNote(long currentId);

    void clearNotes();
}
