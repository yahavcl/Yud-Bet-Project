package com.example.yahavproject;


import androidx.annotation.NonNull;

import java.util.Arrays;

public class NoteItem {
    public String note_title;
    public String note_time;
    public String description;
    //public String note_date;
    public int noteColor;
    public boolean noteDone = false;
    public boolean everyDay = false;
    public boolean everyWeek = false;

    public NoteItem() {}

    public NoteItem(String note_title, String note_time, String description, int noteColor) {
        this.note_title = note_title;
        this.note_time = note_time;
        this.description = description;
        //this.note_date = note_date;
        this.noteColor = noteColor;
    }

    public NoteItem (@NonNull NoteItem other) {
        other.note_title = note_title;
        other.note_time = note_time;
        other.description = description;
        //other.note_date = note_date;
        other.noteColor = noteColor;
    }


    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_time() {
        return note_time;
    }

    public void setNote_time(String note_time) {
        this.note_time = note_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(int noteColor) {
        this.noteColor = noteColor;
    }

}
