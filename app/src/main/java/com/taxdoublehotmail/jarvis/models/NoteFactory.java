package com.taxdoublehotmail.jarvis.models;

public final class NoteFactory {
    public static final String TAG = NoteFactory.class.getSimpleName();

    private NoteFactory() {
        throw new AssertionError(String.format("%s: Cannot be initialized.", TAG));
    }

    public static Note createDefaultNote() {
        return new Note(
                NoteConstants.getDefaultId(),
                NoteConstants.getDefaultTitle(),
                NoteConstants.getDefaultContent(),
                NoteConstants.getDefaultDateModified(),
                NoteConstants.getDefaultNoteState()
        );
    }
}
