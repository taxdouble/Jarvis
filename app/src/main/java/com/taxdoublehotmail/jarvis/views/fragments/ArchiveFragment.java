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
import com.taxdoublehotmail.jarvis.controllers.ArchiveFragmentController;
import com.taxdoublehotmail.jarvis.models.Note;
import com.taxdoublehotmail.jarvis.views.ArchiveFragmentView;
import com.taxdoublehotmail.jarvis.views.MainActivityView;
import com.taxdoublehotmail.jarvis.views.activities.EditorActivity;
import com.taxdoublehotmail.jarvis.views.adapters.ArchiveNoteAdapter;
import com.taxdoublehotmail.jarvis.views.widgets.RecyclerItemClickListener;

public final class ArchiveFragment extends Fragment implements ArchiveFragmentView {
    public static final String TAG = ArchiveFragment.class.getSimpleName();

    private ArchiveFragmentController mArchiveFragmentController;

    private MainActivityView mMainActivityVIew;
    private CoordinatorLayout mArchiveFragmentCoordinatorLayout;
    private CollapsingToolbarLayout mArchiveFragmentCollapsingToolbarLayout;
    private Toolbar mArchiveFragmentToolbar;
    private RecyclerView mArchiveFragmentRecyclerVIew;
    private ArchiveNoteAdapter mArchiveNoteAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeArchiveFragmentController();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_archive, container, false);

        initializeCoordinatorLayout(fragmentView);
        initializeCollapsingToolbarLayout(fragmentView);
        initializeToolbar(fragmentView);
        initializeRecyclerView(fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mArchiveFragmentController.onViewCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mArchiveFragmentController.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mArchiveFragmentController.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_archive, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scroll_to_top:
                mArchiveFragmentController.onScrollToTopMenuItemClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeArchiveFragmentController() {
        JarvisApplication temporaryJarvisApplication = (JarvisApplication)getContext().getApplicationContext();

        mMainActivityVIew = (MainActivityView)getActivity();
        mArchiveFragmentController = temporaryJarvisApplication.getArchiveFragmentController(mMainActivityVIew, ArchiveFragment.this);
    }

    public void initializeCoordinatorLayout(View parentView) {
        mArchiveFragmentCoordinatorLayout = (CoordinatorLayout)parentView.findViewById(R.id.archiveFragmentCoordinatorLayout);
    }

    public void initializeCollapsingToolbarLayout(View parentView) {
        mArchiveFragmentCollapsingToolbarLayout = (CollapsingToolbarLayout)parentView.findViewById(R.id.archiveFragmentCollapsingToolbarLayout);
        mArchiveFragmentCollapsingToolbarLayout.setTitle(getString(R.string.fragment_archive_name));
    }
     public void initializeToolbar(View parentView) {
         mArchiveFragmentToolbar = (Toolbar)parentView.findViewById((R.id.archiveFragmentToolbar));
         mArchiveFragmentToolbar.setTitle(R.string.fragment_archive_name);

         mMainActivityVIew.setToolbar(mArchiveFragmentToolbar);
     }

    public void initializeRecyclerView(View parentVIew) {
        mArchiveFragmentRecyclerVIew = (RecyclerView)parentVIew.findViewById(R.id.archiveFragmentRecyclerView);
        mArchiveFragmentRecyclerVIew.setHasFixedSize(false);
        mArchiveFragmentRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity()));
        mArchiveFragmentRecyclerVIew.setItemAnimator(new DefaultItemAnimator());

        // Adaptor:
        mArchiveNoteAdapter = new ArchiveNoteAdapter();
        mArchiveFragmentRecyclerVIew.setAdapter(mArchiveNoteAdapter);

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
                mArchiveFragmentController.onNoteItemSwipe(viewHolder.getAdapterPosition());
            }
        });
        temporaryItemTouchhelper.attachToRecyclerView(mArchiveFragmentRecyclerVIew);

        // OnItemClick and OnItemLongPress
        mArchiveFragmentRecyclerVIew.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                mArchiveFragmentController.onNoteItemClick(position);
            }

            @Override
            public void onItemLongPress(View childView, int position) {
                // Do Nothing
            }
        }));
    }

    @Override
    public void scrollToTop() {
        mArchiveFragmentRecyclerVIew.smoothScrollToPosition(0);
    }

    @Override
    public void saveLayoutManager(Bundle outState, String parcelableKey) {
        RecyclerView.LayoutManager temporaryLayoutManager = mArchiveFragmentRecyclerVIew.getLayoutManager();
        Parcelable temporaryParcelable = temporaryLayoutManager.onSaveInstanceState();

        outState.putParcelable(parcelableKey, temporaryParcelable);
    }

    @Override
    public void restoreLayoutManager(Bundle outState, String parcelableKey) {
        RecyclerView.LayoutManager temporaryLayoutManager = mArchiveFragmentRecyclerVIew.getLayoutManager();
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
    public void showTrashedSnackbar() {
        Snackbar.make(mArchiveFragmentCoordinatorLayout, R.string.snackbar_trashed, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public int getNotesCount() {
        return mArchiveNoteAdapter.getItemCount();
    }

    @Override
    public Note getNote(int position) {
        return mArchiveNoteAdapter.getNote(position);
    }

    @Override
    public void addNote(Note newNote) {
        mArchiveNoteAdapter.addNote(newNote);
    }

    @Override
    public void removeNote(long currentId) {
        mArchiveNoteAdapter.removeNote(currentId);
    }

    @Override
    public void clearNotes() {
        mArchiveNoteAdapter.clearNotes();
    }
}
