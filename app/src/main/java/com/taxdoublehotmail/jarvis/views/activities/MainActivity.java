package com.taxdoublehotmail.jarvis.views.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.taxdoublehotmail.jarvis.JarvisApplication;
import com.taxdoublehotmail.jarvis.R;
import com.taxdoublehotmail.jarvis.controllers.MainActivityController;
import com.taxdoublehotmail.jarvis.views.MainActivityView;
import com.taxdoublehotmail.jarvis.views.fragments.ArchiveFragment;
import com.taxdoublehotmail.jarvis.views.fragments.PrimaryFragment;
import com.taxdoublehotmail.jarvis.views.fragments.TrashFragment;

public class MainActivity extends AppCompatActivity implements MainActivityView {
    public static final String TAG = MainActivity.class.getSimpleName();

    private MainActivityController mMainActivityController;

    private DrawerLayout mMainActivityDrawerLayout;
    private ActionBarDrawerToggle mMainActivityActionBarDrawerToggle;
    private FrameLayout mMainActivityFrameLayout;
    private Fragment mMainActivityFragment;
    private NavigationView mMainActivityNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeMainActivityController();

        setContentView(R.layout.activity_main);

        initializeDrawerLayout();
        initializeNavigationView();
        initializeFrameLayout();

        mMainActivityController.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mMainActivityActionBarDrawerToggle != null) {
            mMainActivityActionBarDrawerToggle.syncState();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mMainActivityController.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mMainActivityActionBarDrawerToggle != null) {
            mMainActivityActionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mMainActivityDrawerLayout.isDrawerOpen(mMainActivityNavigationView)) {
            menu.close();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMainActivityActionBarDrawerToggle != null) {
            return mMainActivityActionBarDrawerToggle.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMainActivityDrawerLayout.isDrawerOpen(mMainActivityNavigationView)) {
            mMainActivityDrawerLayout.closeDrawer(mMainActivityNavigationView);
            return;
        }

        super.onBackPressed();
    }

    public void initializeMainActivityController() {
        JarvisApplication temporaryJarvisApplication = (JarvisApplication)getApplication();

        mMainActivityController = temporaryJarvisApplication.getMainActivityController(MainActivity.this);
    }

    public void initializeDrawerLayout() {
        mMainActivityDrawerLayout = (DrawerLayout)findViewById(R.id.mainActivityDrawerLayout);
        mMainActivityActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mMainActivityDrawerLayout, R.string.action_drawer_open, R.string.action_drawer_close);
        mMainActivityDrawerLayout.setDrawerListener(mMainActivityActionBarDrawerToggle);
    }

    public void initializeFrameLayout() {
        mMainActivityFrameLayout = (FrameLayout)findViewById(R.id.mainActivityFrameLayout);
    }

    public void initializeNavigationView() {
        mMainActivityNavigationView = (NavigationView)findViewById(R.id.mainActivityNavigationView);
        mMainActivityNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigationViewPrimary:
                        mMainActivityController.onNavigationViewPrimarySelected();
                        return true;
                    case R.id.navigationViewArchive:
                        mMainActivityController.onNavigationViewArchiveSelected();
                        return true;
                    case R.id.navigationViewTrash:
                        mMainActivityController.onNavigationViewTrashSelected();
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        ActionBar temporaryActionBar = getSupportActionBar();
        if (temporaryActionBar != null) {
            temporaryActionBar.setHomeButtonEnabled(true);
            temporaryActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMainActivityActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mMainActivityDrawerLayout, R.string.action_drawer_open, R.string.action_drawer_close);
        mMainActivityActionBarDrawerToggle.syncState();

        mMainActivityDrawerLayout.setDrawerListener(null);
        mMainActivityDrawerLayout.setDrawerListener(mMainActivityActionBarDrawerToggle);
    }

    @Override
    public void saveCurrentFragment(Bundle outState, String parcelableKey) {
        getSupportFragmentManager().putFragment(outState, parcelableKey, mMainActivityFragment);
    }

    @Override
    public void restoreCurrentFragment(Bundle outState, String parcelableKey) {
        mMainActivityFragment = getSupportFragmentManager().getFragment(outState, parcelableKey);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivityFrameLayout, mMainActivityFragment)
                .commit();
    }

    @Override
    public void switchPrimaryFragment() {
        if (!(mMainActivityFragment instanceof PrimaryFragment)) {
            mMainActivityDrawerLayout.closeDrawer(mMainActivityNavigationView);
            mMainActivityNavigationView.setCheckedItem(R.id.navigationViewPrimary);

            mMainActivityFragment = new PrimaryFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainActivityFrameLayout, mMainActivityFragment)
                    .commit();
        }
    }

    @Override
    public void switchArchiveFragment() {
        if (!(mMainActivityFragment instanceof ArchiveFragment)) {
            mMainActivityDrawerLayout.closeDrawer(mMainActivityNavigationView);
            mMainActivityNavigationView.setCheckedItem(R.id.navigationViewArchive);

            mMainActivityFragment = new ArchiveFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainActivityFrameLayout, mMainActivityFragment)
                    .commit();
        }
    }

    @Override
    public void switchTrashFragment() {
        if (!(mMainActivityFragment instanceof TrashFragment)) {
            mMainActivityDrawerLayout.closeDrawer(mMainActivityNavigationView);
            mMainActivityNavigationView.setCheckedItem(R.id.navigationViewTrash);

            mMainActivityFragment = new TrashFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainActivityFrameLayout, mMainActivityFragment)
                    .commit();
        }
    }
}
