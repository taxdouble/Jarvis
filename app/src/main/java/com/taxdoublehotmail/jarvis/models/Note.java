package com.taxdoublehotmail.jarvis.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Note implements Parcelable {
    public static final String TAG = Note.class.getSimpleName();
    public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

    private final long mId;
    private final String mTitle;
    private final String mContent;
    private final Date mDateModified;
    private final NoteState mNoteState;

    public Note(long id, String title, String content, Date dateModified, NoteState noteState) {
        this.mId = id;
        this.mTitle = title;
        this.mContent = content;
        this.mDateModified = dateModified;
        this.mNoteState = noteState;
    }

    private Note(Parcel source) {
        this(
                source.readLong(),
                source.readString(),
                source.readString(),
                new Date(source.readLong()),
                NoteState.valueOf(source.readString())
        );
    }

    public long getId() {
        return this.mId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getContent() {
        return this.mContent;
    }

    public Date getDateModified() {
        return this.mDateModified;
    }

    public NoteState getNoteState() {
        return this.mNoteState;
    }

    // Parcelable:

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mContent);
        dest.writeLong(this.mDateModified.getTime());
        dest.writeString(this.mNoteState.name());
    }
}
