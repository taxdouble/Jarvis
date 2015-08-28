package com.taxdoublehotmail.jarvis.utils.events;

public final class LatestUpdatedNoteEvent {
    public static final String TAG = LatestUpdatedNoteEvent.class.getSimpleName();

    private long mId;

    public LatestUpdatedNoteEvent(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }
}