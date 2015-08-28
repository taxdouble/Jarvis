package com.taxdoublehotmail.jarvis.utils.events;

public final class LatestPrimaryNotePositionEvent {
    public static final String TAG = LatestPrimaryNotePositionEvent.class.getSimpleName();

    private int mPosition;

    public LatestPrimaryNotePositionEvent(int position) {
        this.mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}