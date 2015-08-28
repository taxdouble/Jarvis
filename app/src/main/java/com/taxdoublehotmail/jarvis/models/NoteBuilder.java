package com.taxdoublehotmail.jarvis.models;

import java.util.Date;

public class NoteBuilder {
    public static final String TAG = NoteBuilder.class.getSimpleName();

    private long mId = -1;
    private String mTitle = null;
    private String mContent = null;
    private Date mDateModified = null;
    private NoteState mNoteState = null;

    public NoteBuilder() {}

    public NoteBuilder(Note note) {
        this.mId = note.getId();
        this.mTitle = note.getTitle();
        this.mContent = note.getContent();
        this.mDateModified = note.getDateModified();
        this.mNoteState = note.getNoteState();
    }

    public NoteBuilder setId(long id) {
        this.mId = id;

        return this;
    }

    public NoteBuilder setTitle(String title) {
        this.mTitle = title;

        return this;
    }

    public NoteBuilder setContent(String content) {
        this.mContent = content;

        return this;
    }

    public NoteBuilder setDateModified(Date dateModified) {
        this.mDateModified = dateModified;

        return this;
    }

    public NoteBuilder setNoteState(NoteState noteState) {
        this.mNoteState = noteState;

        return this;
    }

    public Note build() {
        return new Note(
                this.mId,
                this.mTitle,
                this.mContent,
                this.mDateModified,
                this.mNoteState
        );
    }
}
