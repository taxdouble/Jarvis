package com.taxdoublehotmail.jarvis.utils.events;

public final class LatestArchiveNotePositionEvent {
    public static final String TAG = LatestArchiveNotePositionEvent.class.getSimpleName();

    private int mPosition;

    public LatestArchiveNotePositionEvent(int position) {
        this.mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}