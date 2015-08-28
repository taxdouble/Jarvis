package com.taxdoublehotmail.jarvis.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;

public class NoteRepositoryImpl implements NoteRepository {
    public static final String TAG = NoteRepositoryImpl.class.getSimpleName();

    private final SQLiteDatabase mJarvisDatabase;

    public NoteRepositoryImpl(SQLiteDatabase sqLiteDatabase) {
        this.mJarvisDatabase = sqLiteDatabase;
    }

    @Override
    public Observable<Long> createNote(final Note currentNote) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                try {
                    /*
                     * INSERT INTO note (title, content, dateModified, noteState) VALUES (..., ..., ..., ...);
                     */

                    ContentValues currentContentValues = new ContentValues();
                    currentContentValues.put(NoteSchema.COLUMN_NAME_TITLE, currentNote.getTitle());
                    currentContentValues.put(NoteSchema.COLUMN_NAME_CONTENT, currentNote.getContent());
                    currentContentValues.put(NoteSchema.COLUMN_NAME_DATE_MODIFIED, currentNote.getDateModified().getTime());
                    currentContentValues.put(NoteSchema.COLUMN_NAME_NOTESTATE, currentNote.getNoteState().name());

                    Long rowId = mJarvisDatabase.insert(NoteSchema.TABLE_NAME, null, currentContentValues);

                    if (rowId == -1) {
                        throw new SQLException(String.format("Could not insert data for currentNote of id %s.", -1));
                    }

                    subscriber.onNext(rowId);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Long> updateNote(final Note currentNote) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                try {
                    /*
                     * UPDATE note
                     * SET title = ..., content = ..., dateModified = ..., noteState = ...
                     * WHERE _id = ...;
                     */

                    ContentValues currentContentValues = new ContentValues();
                    currentContentValues.put(NoteSchema.COLUMN_NAME_TITLE, currentNote.getTitle());
                    currentContentValues.put(NoteSchema.COLUMN_NAME_CONTENT, currentNote.getContent());
                    currentContentValues.put(NoteSchema.COLUMN_NAME_DATE_MODIFIED, currentNote.getDateModified().getTime());
                    currentContentValues.put(NoteSchema.COLUMN_NAME_NOTESTATE, currentNote.getNoteState().toString());

                    String currentSelection = NoteSchema._ID + " = ?";
                    String[] currentSelectionArgs = { String.valueOf(currentNote.getId()) };

                    int numberUpdated = mJarvisDatabase.update(NoteSchema.TABLE_NAME, currentContentValues, currentSelection, currentSelectionArgs);

                    if (numberUpdated != 1) {
                        throw new android.database.SQLException(String.format("Could not update data for currentNote of id %s.", currentNote.getId()));
                    }

                    subscriber.onNext(currentNote.getId());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> deleteNote(final Note currentNote) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    String currentSelection = NoteSchema._ID + " = ?";
                    String[] currentSelectionArgs = { String.valueOf(currentNote.getId()) };

                    int numberDeleted = mJarvisDatabase.delete(NoteSchema.TABLE_NAME, currentSelection, currentSelectionArgs);

                    if (numberDeleted != 1) {
                        throw new android.database.SQLException(String.format("Could not delete data for currentNote of id %s.", currentNote.getId()));
                    }

                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> deleteTrashNoteList() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    String currentSelection = NoteSchema.COLUMN_NAME_NOTESTATE + " = ?";
                    String[] currentSelectionArgs = { NoteState.TRASH.name() };

                    mJarvisDatabase.delete(NoteSchema.TABLE_NAME, currentSelection, currentSelectionArgs);

                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Note> retrieveNoteById(final long currentId) {
        return Observable.create(new Observable.OnSubscribe<Note>() {
            @Override
            public void call(Subscriber<? super Note> subscriber) {
                Cursor currentCursor = null;

                try {
                    String[] currentColumns = {
                            NoteSchema._ID,
                            NoteSchema.COLUMN_NAME_TITLE,
                            NoteSchema.COLUMN_NAME_CONTENT,
                            NoteSchema.COLUMN_NAME_DATE_MODIFIED,
                            NoteSchema.COLUMN_NAME_NOTESTATE
                    };
                    String currentSelection = NoteSchema._ID + " = ?";
                    String[] currentSelectionArgs = { String.valueOf(currentId) };
                    String currentGroupByClause = null;
                    String currentHavingClause = null;
                    String currentOrderByClause = null;
                    String currentLimitClause = "1";

                    /*
                     * SELECT _id, title, content, dateModified, noteState FROM note WHERE _id = ? LIMIT 1;
                     */

                    currentCursor = mJarvisDatabase.query(NoteSchema.TABLE_NAME, currentColumns, currentSelection, currentSelectionArgs, currentGroupByClause, currentHavingClause, currentOrderByClause, currentLimitClause);

                    int columnIndexId = currentCursor.getColumnIndex(NoteSchema._ID);
                    int columnIndexTitle = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_TITLE);
                    int columnIndexContent = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_CONTENT);
                    int columnIndexDateModified = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_DATE_MODIFIED);
                    int columnIndexNoteState = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_NOTESTATE);

                    if (currentCursor.getCount() == 1 && currentCursor.moveToFirst()) {
                        long temporaryId = currentCursor.getLong(columnIndexId);
                        String temporaryTitle = currentCursor.getString(columnIndexTitle);
                        String temporaryContent = currentCursor.getString(columnIndexContent);
                        Date temporaryDate = new Date(currentCursor.getLong(columnIndexDateModified));
                        NoteState temporaryNoteState = NoteState.valueOf(currentCursor.getString(columnIndexNoteState));

                        Note temporaryNote = new Note(temporaryId, temporaryTitle, temporaryContent, temporaryDate, temporaryNoteState);

                        subscriber.onNext(temporaryNote);
                    } else {
                        throw new android.database.SQLException(String.format("Could not retrieve data for Note of id %s", currentId));
                    }

                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }

                if (currentCursor != null) {
                    currentCursor.close();
                }
            }
        });
    }

    @Override
    public Observable<Note> retrieveNoteListByTitle(final String currentTitle, final int currentLimit, final int currentOffset) {
        return Observable.create(new Observable.OnSubscribe<Note>() {
            @Override
            public void call(Subscriber<? super Note> subscriber) {
                Cursor currentCursor = null;

                try {
                    String[] currentColumns = {
                            NoteSchema._ID,
                            NoteSchema.COLUMN_NAME_TITLE,
                            NoteSchema.COLUMN_NAME_CONTENT,
                            NoteSchema.COLUMN_NAME_DATE_MODIFIED,
                            NoteSchema.COLUMN_NAME_NOTESTATE
                    };
                    String currentSelection = NoteSchema.COLUMN_NAME_TITLE + " LIKE ?";
                    String[] currentSelectionArgs = { "%" + currentTitle + "%" };
                    String currentGroupByClause = null;
                    String currentHavingClause = null;
                    String currentOrderByClause = NoteSchema.COLUMN_NAME_DATE_MODIFIED + " DESC";
                    String currentLimitClause = String.valueOf(currentOffset) + "," + String.valueOf(currentLimit);

                    /*
                     * SELECT _id, title, content, dateModified, noteState FROM note WHERE title LIKE ? ORDER BY dateModified DESC LIMIT ...,...;
                     */

                    currentCursor = mJarvisDatabase.query(NoteSchema.TABLE_NAME, currentColumns, currentSelection, currentSelectionArgs, currentGroupByClause, currentHavingClause, currentOrderByClause, currentLimitClause);

                    int columnIndexId = currentCursor.getColumnIndex(NoteSchema._ID);
                    int columnIndexTitle = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_TITLE);
                    int columnIndexContent = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_CONTENT);
                    int columnIndexDateModified = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_DATE_MODIFIED);
                    int columnIndexNoteState = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_NOTESTATE);

                    if (currentCursor.moveToFirst()) {
                        do {
                            long temporaryId = currentCursor.getLong(columnIndexId);
                            String temporaryTitle = currentCursor.getString(columnIndexTitle);
                            String temporaryContent = currentCursor.getString(columnIndexContent);
                            Date temporaryDate = new Date(currentCursor.getLong(columnIndexDateModified));
                            NoteState temporaryNoteState = NoteState.valueOf(currentCursor.getString(columnIndexNoteState));

                            Note temporaryNote = new Note(temporaryId, temporaryTitle, temporaryContent, temporaryDate, temporaryNoteState);

                            subscriber.onNext(temporaryNote);
                        } while (currentCursor.moveToNext());
                    }

                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }

                if (currentCursor != null) {
                    currentCursor.close();
                }
            }
        });
    }

    @Override
    public Observable<Note> retrievePrimaryNoteListByDateModified(final int currentLimit, final int currentOffset) {
        return Observable.create(new Observable.OnSubscribe<Note>() {
            @Override
            public void call(Subscriber<? super Note> subscriber) {
                Cursor currentCursor = null;

                try {
                    String[] currentColumns = {
                            NoteSchema._ID,
                            NoteSchema.COLUMN_NAME_TITLE,
                            NoteSchema.COLUMN_NAME_CONTENT,
                            NoteSchema.COLUMN_NAME_DATE_MODIFIED,
                            NoteSchema.COLUMN_NAME_NOTESTATE
                    };
                    String currentSelection = NoteSchema.COLUMN_NAME_NOTESTATE+ " = ?";
                    String[] currentSelectionArgs = { String.valueOf(NoteState.PRIMARY) };
                    String currentGroupByClause = null;
                    String currentHavingClause = null;
                    String currentOrderByClause = NoteSchema.COLUMN_NAME_DATE_MODIFIED + " DESC";
                    String currentLimitClause = String.valueOf(currentOffset) + "," + String.valueOf(currentLimit);

                    /*
                     * SELECT _id, title, content, dateModified, noteState FROM note WHERE noteState LIKE PRIMARY ORDER BY dateModified DESC LIMIT ...,...;
                     */

                    currentCursor = mJarvisDatabase.query(NoteSchema.TABLE_NAME, currentColumns, currentSelection, currentSelectionArgs, currentGroupByClause, currentHavingClause, currentOrderByClause, currentLimitClause);

                    int columnIndexId = currentCursor.getColumnIndex(NoteSchema._ID);
                    int columnIndexTitle = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_TITLE);
                    int columnIndexContent = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_CONTENT);
                    int columnIndexDateModified = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_DATE_MODIFIED);
                    int columnIndexNoteState = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_NOTESTATE);

                    if (currentCursor.moveToFirst()) {
                        do {
                            long temporaryId = currentCursor.getLong(columnIndexId);
                            String temporaryTitle = currentCursor.getString(columnIndexTitle);
                            String temporaryContent = currentCursor.getString(columnIndexContent);
                            Date temporaryDate = new Date(currentCursor.getLong(columnIndexDateModified));
                            NoteState temporaryNoteState = NoteState.valueOf(currentCursor.getString(columnIndexNoteState));

                            Note temporaryNote = new Note(temporaryId, temporaryTitle, temporaryContent, temporaryDate, temporaryNoteState);

                            subscriber.onNext(temporaryNote);
                        } while (currentCursor.moveToNext());
                    }

                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }

                if (currentCursor != null) {
                    currentCursor.close();
                }
            }
        });
    }

    @Override
    public Observable<Note> retrieveArchiveNoteListByDateModified(final int currentLimit, final int currentOffset) {
        return Observable.create(new Observable.OnSubscribe<Note>() {
            @Override
            public void call(Subscriber<? super Note> subscriber) {
                Cursor currentCursor = null;

                try {
                    String[] currentColumns = {
                            NoteSchema._ID,
                            NoteSchema.COLUMN_NAME_TITLE,
                            NoteSchema.COLUMN_NAME_CONTENT,
                            NoteSchema.COLUMN_NAME_DATE_MODIFIED,
                            NoteSchema.COLUMN_NAME_NOTESTATE
                    };
                    String currentSelection = NoteSchema.COLUMN_NAME_NOTESTATE+ " = ?";
                    String[] currentSelectionArgs = { String.valueOf(NoteState.ARCHIVE) };
                    String currentGroupByClause = null;
                    String currentHavingClause = null;
                    String currentOrderByClause = NoteSchema.COLUMN_NAME_DATE_MODIFIED + " DESC";
                    String currentLimitClause = String.valueOf(currentOffset) + "," + String.valueOf(currentLimit);

                    /*
                     * SELECT _id, title, content, dateModified, noteState FROM note WHERE noteState LIKE ARCHIVE ORDER BY dateModified DESC LIMIT ...,...;
                     */

                    currentCursor = mJarvisDatabase.query(NoteSchema.TABLE_NAME, currentColumns, currentSelection, currentSelectionArgs, currentGroupByClause, currentHavingClause, currentOrderByClause, currentLimitClause);

                    int columnIndexId = currentCursor.getColumnIndex(NoteSchema._ID);
                    int columnIndexTitle = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_TITLE);
                    int columnIndexContent = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_CONTENT);
                    int columnIndexDateModified = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_DATE_MODIFIED);
                    int columnIndexNoteState = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_NOTESTATE);

                    if (currentCursor.moveToFirst()) {
                        do {
                            long temporaryId = currentCursor.getLong(columnIndexId);
                            String temporaryTitle = currentCursor.getString(columnIndexTitle);
                            String temporaryContent = currentCursor.getString(columnIndexContent);
                            Date temporaryDate = new Date(currentCursor.getLong(columnIndexDateModified));
                            NoteState temporaryNoteState = NoteState.valueOf(currentCursor.getString(columnIndexNoteState));

                            Note temporaryNote = new Note(temporaryId, temporaryTitle, temporaryContent, temporaryDate, temporaryNoteState);

                            subscriber.onNext(temporaryNote);
                        } while (currentCursor.moveToNext());
                    }

                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }

                if (currentCursor != null) {
                    currentCursor.close();
                }
            }
        });
    }

    @Override
    public Observable<Note> retrieveTrashNoteListByDateModified(final int currentLimit, final int currentOffset) {
        return Observable.create(new Observable.OnSubscribe<Note>() {
            @Override
            public void call(Subscriber<? super Note> subscriber) {
                Cursor currentCursor = null;

                try {
                    String[] currentColumns = {
                            NoteSchema._ID,
                            NoteSchema.COLUMN_NAME_TITLE,
                            NoteSchema.COLUMN_NAME_CONTENT,
                            NoteSchema.COLUMN_NAME_DATE_MODIFIED,
                            NoteSchema.COLUMN_NAME_NOTESTATE
                    };
                    String currentSelection = NoteSchema.COLUMN_NAME_NOTESTATE+ " = ?";
                    String[] currentSelectionArgs = { String.valueOf(NoteState.TRASH) };
                    String currentGroupByClause = null;
                    String currentHavingClause = null;
                    String currentOrderByClause = NoteSchema.COLUMN_NAME_DATE_MODIFIED + " DESC";
                    String currentLimitClause = String.valueOf(currentOffset) + "," + String.valueOf(currentLimit);

                    /*
                     * SELECT _id, title, content, dateModified, noteState FROM note WHERE noteState LIKE TRASH ORDER BY dateModified DESC LIMIT ...,...;
                     */

                    currentCursor = mJarvisDatabase.query(NoteSchema.TABLE_NAME, currentColumns, currentSelection, currentSelectionArgs, currentGroupByClause, currentHavingClause, currentOrderByClause, currentLimitClause);

                    int columnIndexId = currentCursor.getColumnIndex(NoteSchema._ID);
                    int columnIndexTitle = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_TITLE);
                    int columnIndexContent = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_CONTENT);
                    int columnIndexDateModified = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_DATE_MODIFIED);
                    int columnIndexNoteState = currentCursor.getColumnIndex(NoteSchema.COLUMN_NAME_NOTESTATE);

                    if (currentCursor.moveToFirst()) {
                        do {
                            long temporaryId = currentCursor.getLong(columnIndexId);
                            String temporaryTitle = currentCursor.getString(columnIndexTitle);
                            String temporaryContent = currentCursor.getString(columnIndexContent);
                            Date temporaryDate = new Date(currentCursor.getLong(columnIndexDateModified));
                            NoteState temporaryNoteState = NoteState.valueOf(currentCursor.getString(columnIndexNoteState));

                            Note temporaryNote = new Note(temporaryId, temporaryTitle, temporaryContent, temporaryDate, temporaryNoteState);

                            subscriber.onNext(temporaryNote);
                        } while (currentCursor.moveToNext());
                    }

                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }

                if (currentCursor != null) {
                    currentCursor.close();
                }
            }
        });
    }
}
