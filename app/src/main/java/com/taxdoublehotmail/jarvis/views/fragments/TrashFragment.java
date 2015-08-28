package com.taxdoublehotmail.jarvis.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.taxdoublehotmail.jarvis.JarvisApplication;
import com.taxdoublehotmail.jarvis.R;
import com.taxdoublehotmail.jarvis.controllers.TrashFragmentController;
import com.taxdoublehotmail.jarvis.models.Note;
import com.taxdoublehotmail.jarvis.views.MainActivityView;
import com.taxdoublehotmail.jarvis.views.TrashFragmentView;
import com.taxdoublehotmail.jarvis.views.activities.EditorActivity;
import com.taxdoublehotmail.jarvis.views.adapters.TrashNoteAdapter;
import com.taxdoublehotmail.jarvis.views.widgets.RecyclerItemClickListener;

public final class TrashFragment extends Fragment implements TrashFragmentView {
    public static final String TAG = TrashFragment.class.getSimpleName();

    private TrashFragmentController mTrashFragmentController;

    private MainActivityView mMainActivityVIew;
    private CoordinatorLayout mTrashFragmentCoordinatorLayout;
    private CollapsingToolbarLayout mTrashFragmentCollapsingToolbarLayout;
    private Toolbar mTrashFragmentToolbar;
    private RecyclerView mTrashFragmentRecyclerVIew;
    private TrashNoteAdapter mTrashNoteAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeArchiveFragmentController();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_trash, container, false);

        initializeCoordinatorLayout(fragmentView);
        initializeCollapsingToolbarLayout(fragmentView);
        initializeToolbar(fragmentView);
        initializeRecyclerView(fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTrashFragmentController.onViewCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mTrashFragmentController.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mTrashFragmentController.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_trash, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scroll_to_top:
                mTrashFragmentController.onScrollToTopMenuItemClick();
                return true;
            case R.id.action_empty_trash:
                mTrashFragmentController.onDeleteAllMenuItemClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeArchiveFragmentController() {
        JarvisApplication temporaryJarvisApplication = (JarvisApplication)getContext().getApplicationContext();

        mMainActivityVIew = (MainActivityView)getActivity();
        mTrashFragmentController = temporaryJarvisApplication.getTrashFragmentController(mMainActivityVIew, TrashFragment.this);
    }

    public void initializeCoordinatorLayout(View parentView) {
        mTrashFragmentCoordinatorLayout = (CoordinatorLayout)parentView.findViewById(R.id.trashFragmentCoordinatorLayout);
    }

    public void initializeCollapsingToolbarLayout(View parentView) {
        mTrashFragmentCollapsingToolbarLayout = (CollapsingToolbarLayout)parentView.findViewById(R.id.trashFragmentCollapsingToolbarLayout);
        mTrashFragmentCollapsingToolbarLayout.setTitle(getString(R.string.fragment_trash_name));
    }
    public void initializeToolbar(View parentView) {
        mTrashFragmentToolbar = (Toolbar)parentView.findViewById((R.id.trashFragmentToolbar));
        mTrashFragmentToolbar.setTitle(R.string.fragment_trash_name);

        mMainActivityVIew.setToolbar(mTrashFragmentToolbar);
    }

    public void initializeRecyclerView(View parentVIew) {
        mTrashFragmentRecyclerVIew = (RecyclerView)parentVIew.findViewById(R.id.trashFragmentRecyclerView);
        mTrashFragmentRecyclerVIew.setHasFixedSize(false);
        mTrashFragmentRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrashFragmentRecyclerVIew.setItemAnimator(new DefaultItemAnimator());

        // Adaptor:
        mTrashNoteAdapter = new TrashNoteAdapter();
        mTrashFragmentRecyclerVIew.setAdapter(mTrashNoteAdapter);

        //OnMove and OnSwiped
        ItemTouchHelper temporaryItemTouchhelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mTrashFragmentController.onNoteItemSwipe(viewHolder.getAdapterPosition());
            }
        });
        temporaryItemTouchhelper.attachToRecyclerView(mTrashFragmentRecyclerVIew);

        // OnItemClick and OnItemLongPress
        mTrashFragmentRecyclerVIew.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                mTrashFragmentController.onNoteItemClick(position);
            }

            @Override
            public void onItemLongPress(View childView, int position) {
                // Do Nothing
            }
        }));
    }

    @Override
    public void scrollToTop() {
        mTrashFragmentRecyclerVIew.smoothScrollToPosition(0);
    }

    @Override
    public void saveLayoutManager(Bundle outState, String parcelableKey) {
        RecyclerView.LayoutManager temporaryLayoutManager = mTrashFragmentRecyclerVIew.getLayoutManager();
        Parcelable temporaryParcelable = temporaryLayoutManager.onSaveInstanceState();

        outState.putParcelable(parcelableKey, temporaryParcelable);
    }

    @Override
    public void restoreLayoutManager(Bundle outState, String parcelableKey) {
        RecyclerView.LayoutManager temporaryLayoutManager = mTrashFragmentRecyclerVIew.getLayoutManager();
        Parcelable temporaryParcelable = outState.getParcelable(parcelableKey);

        temporaryLayoutManager.onRestoreInstanceState(temporaryParcelable);
    }

    @Override
    public void launchEditorActivity(Note currentNote) {
        Intent newIntent = new Intent(getActivity(), EditorActivity.class);

        newIntent.putExtra(EditorActivity.NOTE_ARGUMENT_KEY, currentNote);

        startActivity(newIntent);
    }

    @Override
    public void showDeletedSnackbar() {
        Snackbar.make(mTrashFragmentCoordinatorLayout, R.string.snackbar_deleted, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showDeletedAllSnackbar() {
        Snackbar.make(mTrashFragmentCoordinatorLayout, R.string.snackbar_deleted_all, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public int getNotesCount() {
        return mTrashNoteAdapter.getItemCount();
    }

    @Override
    public Note getNote(int position) {
        return mTrashNoteAdapter.getNote(position);
    }

    @Override
    public void addNote(Note newNote) {
        mTrashNoteAdapter.addNote(newNote);
    }

    @Override
    public void removeNote(long currentId) {
        mTrashNoteAdapter.removeNote(currentId);
    }

    @Override
    public void clearNotes() {
        mTrashNoteAdapter.clearNotes();
    }
}
