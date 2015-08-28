package com.taxdoublehotmail.jarvis;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.facebook.stetho.Stetho;
import com.taxdoublehotmail.jarvis.controllers.ArchiveFragmentController;
import com.taxdoublehotmail.jarvis.controllers.ArchiveFragmentControllerImpl;
import com.taxdoublehotmail.jarvis.controllers.EditorActivityController;
import com.taxdoublehotmail.jarvis.controllers.EditorActivityControllerImpl;
import com.taxdoublehotmail.jarvis.controllers.MainActivityController;
import com.taxdoublehotmail.jarvis.controllers.MainActivityControllerImpl;
import com.taxdoublehotmail.jarvis.controllers.PrimaryFragmentController;
import com.taxdoublehotmail.jarvis.controllers.PrimaryFragmentControllerImpl;
import com.taxdoublehotmail.jarvis.controllers.TrashFragmentController;
import com.taxdoublehotmail.jarvis.controllers.TrashFragmentControllerImpl;
import com.taxdoublehotmail.jarvis.models.JarvisSQLiteOpenHelper;
import com.taxdoublehotmail.jarvis.models.NoteRepository;
import com.taxdoublehotmail.jarvis.models.NoteRepositoryImpl;
import com.taxdoublehotmail.jarvis.views.ArchiveFragmentView;
import com.taxdoublehotmail.jarvis.views.EditorActivityView;
import com.taxdoublehotmail.jarvis.views.MainActivityView;
import com.taxdoublehotmail.jarvis.views.PrimaryFragmentView;
import com.taxdoublehotmail.jarvis.views.TrashFragmentView;

public class JarvisApplication extends Application {
    public static final String TAG = JarvisApplication.class.getSimpleName();

    private JarvisSQLiteOpenHelper mJarvisSQLiteOpenHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        mJarvisSQLiteOpenHelper = new JarvisSQLiteOpenHelper(this);

        if (BuildConfig.DEBUG) {
            initializeStetho();
        }
    }

    private void initializeStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build()
        );
    }

    public SQLiteDatabase getJarvisDatabase() {
        return mJarvisSQLiteOpenHelper.getWritableDatabase();
    }

    public NoteRepository getNoteRepository() {
        return new NoteRepositoryImpl(getJarvisDatabase());
    }

    public MainActivityController getMainActivityController(MainActivityView mainActivityView) {
        return new MainActivityControllerImpl(mainActivityView);
    }

    public EditorActivityController getEditorActivityController(EditorActivityView editorActivityView) {
        return new EditorActivityControllerImpl(editorActivityView, getNoteRepository());
    }

    public PrimaryFragmentController getPrimaryFragmentController(MainActivityView mainActivityView, PrimaryFragmentView primaryFragmentView) {
        return new PrimaryFragmentControllerImpl(mainActivityView, primaryFragmentView, getNoteRepository());
    }

    public ArchiveFragmentController getArchiveFragmentController(MainActivityView mainActivityView, ArchiveFragmentView archiveFragmentView) {
        return new ArchiveFragmentControllerImpl(mainActivityView, archiveFragmentView, getNoteRepository());
    }

    public TrashFragmentController getTrashFragmentController(MainActivityView mainActivityView, TrashFragmentView trashFragmentView) {
        return new TrashFragmentControllerImpl(mainActivityView, trashFragmentView, getNoteRepository());
    }
}
