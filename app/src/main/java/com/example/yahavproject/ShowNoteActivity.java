package com.example.yahavproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowNoteActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_time;
    TextView tv_title;
    TextView tv_des;
    ImageButton btn_back;
    LinearLayout background;
    TextView tv_done;

    String caller_to_open_note;
    NoteItem currentNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);

        caller_to_open_note = getIntent().getStringExtra("caller_to_open_note");

        tv_time = (TextView)findViewById(R.id.show_note_time);
        tv_title = (TextView)findViewById(R.id.show_note_title);
        tv_des = (TextView)findViewById(R.id.show_note_des);
        btn_back = (ImageButton)findViewById(R.id.show_note_back);
        background = (LinearLayout)findViewById(R.id.full_note_size);
        tv_done = (TextView) findViewById(R.id.tv_done);


        if (caller_to_open_note.equals("SundayActivity")) {
            currentNote = SundayActivity.notesListSun.get(SundayActivity.notePositionSun);
            tv_time.setText(currentNote.note_time);
            tv_title.setText(currentNote.note_title);
            tv_des.setText(currentNote.description);
            background.setBackgroundResource(currentNote.noteColor);
        }

        if (caller_to_open_note.equals("MondayActivity")) {
            currentNote = MondayActivity.notesListMon.get(MondayActivity.notePositionMon);
            tv_time.setText(currentNote.note_time);
            tv_title.setText(currentNote.note_title);
            tv_des.setText(currentNote.description);
            background.setBackgroundResource(currentNote.noteColor);
        }

        if (caller_to_open_note.equals("TuesdayActivity")) {
            currentNote = TuesdayActivity.notesListTue.get(TuesdayActivity.notePositionTue);
            tv_time.setText(currentNote.note_time);
            tv_title.setText(currentNote.note_title);
            tv_des.setText(currentNote.description);
            background.setBackgroundResource(currentNote.noteColor);
        }

        if (caller_to_open_note.equals("WednesdayActivity")) {
            currentNote = WednesdayActivity.notesListWed.get(WednesdayActivity.notePositionWed);
            tv_time.setText(currentNote.note_time);
            tv_title.setText(currentNote.note_title);
            tv_des.setText(currentNote.description);
            background.setBackgroundResource(currentNote.noteColor);
        }

        if (caller_to_open_note.equals("ThursdayActivity")) {
            currentNote = ThursdayActivity.notesListThu.get(ThursdayActivity.notePositionThu);
            tv_time.setText(currentNote.note_time);
            tv_title.setText(currentNote.note_title);
            tv_des.setText(currentNote.description);
            background.setBackgroundResource(currentNote.noteColor);
        }

        if (caller_to_open_note.equals("FridayActivity")) {
            currentNote = FridayActivity.notesListFri.get(FridayActivity.notePositionFri);
            tv_time.setText(currentNote.note_time);
            tv_title.setText(currentNote.note_title);
            tv_des.setText(currentNote.description);
            background.setBackgroundResource(currentNote.noteColor);
        }

        if (caller_to_open_note.equals("SaturdayActivity")) {
            currentNote = SaturdayActivity.notesListSat.get(SaturdayActivity.notePositionSat);
            tv_time.setText(currentNote.note_time);
            tv_title.setText(currentNote.note_title);
            tv_des.setText(currentNote.description);
            background.setBackgroundResource(currentNote.noteColor);
        }

        if (currentNote.noteColor == R.color.done_note) {
            tv_done.setVisibility(View.VISIBLE);
        }


        btn_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if  (view == btn_back)
            onBackPressed();
    }
}



            /*tv_time.setText(SundayActivity.notesListSun.get(SundayActivity.notePositionSun).note_time);
            tv_title.setText(SundayActivity.notesListSun.get(SundayActivity.notePositionSun).note_title);
            tv_des.setText(SundayActivity.notesListSun.get(SundayActivity.notePositionSun).description);
            background.setBackgroundResource(SundayActivity.notesListSun.get(SundayActivity.notePositionSun).noteColor);*/