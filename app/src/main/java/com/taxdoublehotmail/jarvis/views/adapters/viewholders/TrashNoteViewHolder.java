package com.taxdoublehotmail.jarvis.views.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.taxdoublehotmail.jarvis.R;
import com.taxdoublehotmail.jarvis.models.Note;

public class TrashNoteViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = TrashNoteViewHolder.class.getSimpleName();

    private TextView mRecyclerViewItemTitleTextView;
    private TextView mRecyclerViewItemContentTextView;

    public TrashNoteViewHolder(View itemView) {
        super(itemView);

        mRecyclerViewItemTitleTextView = (TextView)itemView.findViewById(R.id.recyclerViewItemTitleTextView);
        mRecyclerViewItemContentTextView = (TextView)itemView.findViewById(R.id.recyclerViewItemContentTextView);
    }

    public void bindNoteToView(Note note) {
        mRecyclerViewItemTitleTextView.setText(note.getTitle());
        mRecyclerViewItemContentTextView.setText(note.getContent());
    }
}
