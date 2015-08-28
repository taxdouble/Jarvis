package com.taxdoublehotmail.jarvis.models;

import android.provider.BaseColumns;

public final class NoteSchema implements BaseColumns {
    public static final String TAG = NoteSchema.class.getSimpleName();

    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_CONTENT = "content";
    public static final String COLUMN_NAME_DATE_MODIFIED = "dateModified";
    public static final String COLUMN_NAME_NOTESTATE = "noteState";

    public static final String CREATE_TABLE_STATEMENT = createTableStatement();

    private NoteSchema() {
        throw new AssertionError(String.format("%s:Cannot be initialized."));
    }

    public static String createTableStatement() {
        /*
         * CREATE TABLE notes (
         *  _id INTEGER PRIMARY KEY AUTOINCREMENT,
         *  title TEXT NOT NULL,
         *  content TEXT NOT NULL,
         *  dateModified INTEGER NOT NULL,
         *  noteState TEXT NOT NULL
         * );
         */

        return "CREATE TABLE " + NoteSchema.TABLE_NAME + " ("
                + NoteSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteSchema.COLUMN_NAME_TITLE + " TEXT NOT NULL, "
                + NoteSchema.COLUMN_NAME_CONTENT + " TEXT NOT NULL, "
                + NoteSchema.COLUMN_NAME_DATE_MODIFIED + " INTEGER NOT NULL, "
                + NoteSchema.COLUMN_NAME_NOTESTATE + " TEXT NOT NULL"
                + ")";
    }
}
