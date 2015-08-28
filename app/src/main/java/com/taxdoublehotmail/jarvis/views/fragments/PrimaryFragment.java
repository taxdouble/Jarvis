package com.taxdoublehotmail.jarvis.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.taxdoublehotmail.jarvis.controllers.PrimaryFragmentController;
import com.taxdoublehotmail.jarvis.models.Note;
import com.taxdoublehotmail.jarvis.views.MainActivityView;
import com.taxdoublehotmail.jarvis.views.PrimaryFragmentView;
import com.taxdoublehotmail.jarvis.views.activities.EditorActivity;
import com.taxdoublehotmail.jarvis.views.adapters.PrimaryNoteAdapter;
import com.taxdoublehotmail.jarvis.views.widgets.RecyclerItemClickListener;

public final class PrimaryFragment extends Fragment implements PrimaryFragmentView {
    public static final String TAG = PrimaryFragment.class.getSimpleName();

    private PrimaryFragmentController mPrimaryFragmentController;

    private MainActivityView mMainActivityView;
    private CoordinatorLayout mPrimaryFragmentCoordinatorLayout;
    private CollapsingToolbarLayout mPrimaryFragmentCollapsingToolbarLayout;
    private Toolbar mPrimaryFragmentToolbar;
    private RecyclerView mPrimaryFragmentRecyclerView;
    private PrimaryNoteAdapter mPrimaryNoteAdapter;
    private FloatingActionButton mPrimaryFragmentFAB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePrimaryFragmentController();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_primary, container, false);

        initializeCoordinatorLayout(fragmentView);
        initializeCollapsingToolbarLayout(fragmentView);
        initializeToolbar(fragmentView);
        initializeRecyclerView(fragmentView);
        initializeFloatingActionButton(fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPrimaryFragmentController.onViewCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mPrimaryFragmentController.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mPrimaryFragmentController.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_primary, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scroll_to_top:
                mPrimaryFragmentController.onScrollToTopMenuItemClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializePrimaryFragmentController() {
        JarvisApplication temporaryJarvisApplication = (JarvisApplication)getContext().getApplicationContext();

        mMainActivityView = (MainActivityView)getActivity();
        mPrimaryFragmentController = temporaryJarvisApplication.getPrimaryFragmentController(mMainActivityView, PrimaryFragment.this);
    }

    public void initializeCoordinatorLayout(View parentView) {
        mPrimaryFragmentCoordinatorLayout = (CoordinatorLayout)parentView.findViewById(R.id.primaryFragmentCoordinatorLayout);
    }

    public void initializeCollapsingToolbarLayout(View parentView) {
        mPrimaryFragmentCollapsingToolbarLayout = (CollapsingToolbarLayout)parentView.findViewById(R.id.primaryFragmentCollapsingToolbarLayout);
        mPrimaryFragmentCollapsingToolbarLayout.setTitle(getString(R.string.fragment_primary_name));
    }

    public void initializeToolbar(View parentView) {
        mPrimaryFragmentToolbar = (Toolbar)parentView.findViewById(R.id.primaryFragmentToolbar);
        mPrimaryFragmentToolbar.setTitle(R.string.fragment_primary_name);

        mMainActivityView.setToolbar(mPrimaryFragmentToolbar);
    }

    public void initializeRecyclerView(View parentView) {
        mPrimaryFragmentRecyclerView = (RecyclerView)parentView.findViewById(R.id.primaryFragmentRecyclerView);
        mPrimaryFragmentRecyclerView.setHasFixedSize(false);
        mPrimaryFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPrimaryFragmentRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Adapter:
        mPrimaryNoteAdapter = new PrimaryNoteAdapter();
        mPrimaryFragmentRecyclerView.setAdapter(mPrimaryNoteAdapter);

        // OnMove and OnSwiped.
        ItemTouchHelper temporaryItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mPrimaryFragmentController.onNoteItemSwipe(viewHolder.getAdapterPosition());
            }
        });
        temporaryItemTouchHelper.attachToRecyclerView(mPrimaryFragmentRecyclerView);

        // OnItemClick and OnItemLongPress.
        mPrimaryFragmentRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                mPrimaryFragmentController.onNoteItemClick(position);
            }

            @Override
            public void onItemLongPress(View childView, int position) {
                // Do Nothing.
            }
        }));
    }

    public void initializeFloatingActionButton(View parentView) {
        mPrimaryFragmentFAB = (FloatingActionButton)parentView.findViewById(R.id.primaryFragmentFAB);
        mPrimaryFragmentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), EditorActivity.class);

                startActivity(newIntent);
            }
        });
    }

    @Override
    public void scrollToTop() {
        mPrimaryFragmentRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void saveLayoutManager(Bundle outState, String parcelableKey) {
        RecyclerView.LayoutManager temporaryLayoutManager = mPrimaryFragmentRecyclerView.getLayoutManager();
        Parcelable temporaryParcelable = temporaryLayoutManager.onSaveInstanceState();

        outState.putParcelable(parcelableKey, temporaryParcelable);
    }

    @Override
    public void restoreLayoutManager(Bundle outState, String parcelableKey) {
        RecyclerView.LayoutManager temporaryLayoutManager = mPrimaryFragmentRecyclerView.getLayoutManager();
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
    public void showArchivedSnackbar() {
        Snackbar.make(mPrimaryFragmentCoordinatorLayout, R.string.snackbar_archived, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public int getNotesCount() {
        return mPrimaryNoteAdapter.getItemCount();
    }

    @Override
    public Note getNote(int position) {
        return mPrimaryNoteAdapter.getNote(position);
    }

    @Override
    public void addNote(Note newNote) {
        mPrimaryNoteAdapter.addNote(newNote);
    }

    @Override
    public void removeNote(long currentId) {
        mPrimaryNoteAdapter.removeNote(currentId);
    }

    @Override
    public void clearNotes() {
        mPrimaryNoteAdapter.clearNotes();
    }
}
