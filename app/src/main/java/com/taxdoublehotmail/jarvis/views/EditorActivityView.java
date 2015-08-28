package com.taxdoublehotmail.jarvis.views;

import java.util.Date;

public interface EditorActivityView {
    String getNoteTitle();

    String getNoteContent();

    void setNoteTitle(String currentTitle);

    void setNoteContent(String currentContent);

    void setNoteDate(Date currentDate);
}
