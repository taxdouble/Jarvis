package com.taxdoublehotmail.jarvis.utils.events;

public final class LatestTrashNotePositionEvent {
    public static final String TAG = LatestTrashNotePositionEvent.class.getSimpleName();

    private int mPosition;

    public LatestTrashNotePositionEvent(int position) {
        this.mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}