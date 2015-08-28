package com.taxdoublehotmail.jarvis.models;

import java.util.Date;

public final class NoteConstants {
    public static final String TAG = NoteConstants.class.getSimpleName();

    private NoteConstants() {
        throw new AssertionError(String.format("%s: Cannot be initialized.", TAG));
    }

    public static long getDefaultId() {
        return -1;
    }

    public static String getDefaultTitle() {
        return "No Title";
    }

    public static String getDefaultContent() {
        return "No Content";
    }

    public static Date getDefaultDateModified() {
        return new Date();
    }

    public static NoteState getDefaultNoteState() {
        return NoteState.PRIMARY;
    }
}
