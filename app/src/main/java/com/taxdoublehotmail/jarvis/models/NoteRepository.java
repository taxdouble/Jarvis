package com.taxdoublehotmail.jarvis.models;

import rx.Observable;

public interface NoteRepository {
    Observable<Long> createNote(Note currentNote);

    Observable<Long> updateNote(Note currentNote);

    Observable<Void> deleteNote(Note currentNote);

    Observable<Void> deleteTrashNoteList();

    Observable<Note> retrieveNoteById(long currentId);

    Observable<Note> retrieveNoteListByTitle(String currentTitle, int currentLimit, int currentOffset);

    Observable<Note> retrievePrimaryNoteListByDateModified(int currentLimit, int currentOffset);

    Observable<Note> retrieveArchiveNoteListByDateModified(int currentLimit, int currentOffset);

    Observable<Note> retrieveTrashNoteListByDateModified(int currentLimit, int currentOffset);
}
