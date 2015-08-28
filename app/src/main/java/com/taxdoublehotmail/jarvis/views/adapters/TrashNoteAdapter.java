package com.taxdoublehotmail.jarvis.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.taxdoublehotmail.jarvis.R;
import com.taxdoublehotmail.jarvis.models.Note;
import com.taxdoublehotmail.jarvis.utils.BusProvider;
import com.taxdoublehotmail.jarvis.utils.events.LatestTrashNotePositionEvent;
import com.taxdoublehotmail.jarvis.views.adapters.viewholders.TrashNoteViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TrashNoteAdapter extends RecyclerView.Adapter<TrashNoteViewHolder> {
    public static final String TAG = TrashNoteAdapter.class.getSimpleName();

    private final List<Note> mNotes;

    public TrashNoteAdapter() {
        this(new ArrayList<Note>());
    }

    public TrashNoteAdapter(List<Note> notes) {
        mNotes = notes;
    }

    @Override
    public TrashNoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrashNoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(TrashNoteViewHolder holder, int position) {
        BusProvider.getInstance().post(new LatestTrashNotePositionEvent(position));

        holder.bindNoteToView(mNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public Note getNote(int position) {
        return mNotes.get(position);
    }

    public void addNote(Note newNote) {
        int currentPosition = mNotes.size();

        mNotes.add(newNote);

        notifyItemInserted(currentPosition);
    }

    public void removeNote(long currentId) {
        int currentPosition = -1;
        for (int index = 0; index < mNotes.size(); index++) {
            if (mNotes.get(index).getId() == currentId) {
                currentPosition = index;
                break;
            }
        }

        if (currentPosition != -1) {
            mNotes.remove(currentPosition);

            notifyItemRemoved(currentPosition);
        }
    }

    public void clearNotes() {
        int initialPosition = 0;
        int currentSize = mNotes.size();

        mNotes.clear();

        notifyItemRangeRemoved(initialPosition, currentSize);
    }
}
