package com.example.yahavproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TuesdayActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title_tue;
    ListView lv_notes_tue;
    FloatingActionButton fab_add_tue;
    FloatingActionButton fab_clear_tue;
    FloatingActionButton fab_edit_tue;

    static ArrayList<NoteItem> notesListTue = new ArrayList<NoteItem>();
    static NoteAdapter noteAdapterTue;

    static int notePositionTue;
    static int noteOgColorTue;

    AlertDialog dialogMenu;
    AlertDialog dialogClearDay;

    Animation fabOpen, fabClose;
    boolean isFabOpen = false;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TuesdayActivity.this, MainActivity.class));
    }

    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuesday);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        tv_title_tue = (TextView) findViewById(R.id.tv_title_tue);
        lv_notes_tue = (ListView) findViewById(R.id.lv_notes_tue);
        fab_add_tue = (FloatingActionButton) findViewById(R.id.fab_add_tue);
        fab_edit_tue = (FloatingActionButton) findViewById(R.id.fab_edit_tue);
        fab_clear_tue = (FloatingActionButton) findViewById(R.id.fab_clear_tue);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.from_right_open_fab);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.to_right_close_fab);

        fab_add_tue.setOnClickListener(this);
        fab_clear_tue.setOnClickListener(this);
        fab_edit_tue.setOnClickListener(this);

        Query query = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child("Tuesday").child("NOTES").orderByValue();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notesListTue.clear();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);
                    if (noteItem.noteColor == R.color.done_note) {
                        notesListTue.add(noteItem);
                        noteItem.noteDone = true;
                        noteAdapterTue.notifyDataSetChanged();
                    }
                    else {
                        notesListTue.add(noteItem);
                        noteItem.noteDone = false;
                        noteAdapterTue.notifyDataSetChanged();
                    }
                }

                Collections.sort(notesListTue, new Comparator<NoteItem>() {
                    @Override
                    public int compare(NoteItem n1, NoteItem n2) {
                        int n1_hour_tens_int = Character.getNumericValue(n1.getNote_time().charAt(0));
                        int n1_hour_units_int = Character.getNumericValue(n1.getNote_time().charAt(1));
                        int n2_hour_tens_int = Character.getNumericValue(n2.getNote_time().charAt(0));
                        int n2_hour_units_int = Character.getNumericValue(n2.getNote_time().charAt(1));
                        int n1_hour = n1_hour_tens_int * 10 + n1_hour_units_int;
                        int n2_hour = n2_hour_tens_int * 10 + n2_hour_units_int;
                        int n1_minute_tens_int = Character.getNumericValue(n1.getNote_time().charAt(3));
                        int n1_minute_units_int = Character.getNumericValue(n1.getNote_time().charAt(4));
                        int n2_minute_tens_int = Character.getNumericValue(n2.getNote_time().charAt(3));
                        int n2_minute_units_int = Character.getNumericValue(n2.getNote_time().charAt(4));
                        int n1_minute = n1_minute_tens_int * 10 + n1_minute_units_int;
                        int n2_minute = n2_minute_tens_int * 10 + n2_minute_units_int;

                        if (n1_hour == n2_hour)
                            return Math.min(n1_minute, n2_minute);
                        return Math.min(n1_hour, n2_hour);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        noteAdapterTue = new NoteAdapter(this, 0, 0, notesListTue);
        lv_notes_tue.setAdapter(noteAdapterTue);

        registerForContextMenu(lv_notes_tue);

        lv_notes_tue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                notePositionTue = position;
                startActivity(new Intent(TuesdayActivity.this, ShowNoteActivity.class)
                        .putExtra("caller_to_open_note", "TuesdayActivity"));
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);

        notePositionTue = info.position;

        if (notesListTue.get(notePositionTue).noteColor == R.color.done_note) {
            menu.findItem(R.id.first_line).setTitle("undone");
            menu.removeItem(R.id.second_line);

        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.first_line) {

            if (notesListTue.get(notePositionTue).noteColor == R.color.done_note) {

                Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child("Tuesday").child("DONE NOTES").orderByValue();

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dst : dataSnapshot.getChildren()) {
                            NoteItem noteItem = dst.getValue(NoteItem.class);
                            if (noteItem.note_time.equals(notesListTue.get(notePositionTue).note_time)) {
                                notesListTue.get(notePositionTue).setNoteColor(noteItem.noteColor);
                                notesListTue.get(notePositionTue).noteDone = false;
                                noteAdapterTue.notifyDataSetChanged();
                                lv_notes_tue.setAdapter(noteAdapterTue);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days") //new
                                        .child("Tuesday").child("NOTES").child(noteItem.note_time)
                                        .setValue(noteItem);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days")
                                        .child("Tuesday").child("DONE NOTES").child(noteItem.note_time).removeValue();



                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            else  {

                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Tuesday") //new
                        .child("DONE NOTES").child(notesListTue.get(notePositionTue).note_time)
                        .setValue(notesListTue.get(notePositionTue));


                notesListTue.get(notePositionTue).setNoteColor(R.color.done_note);
                notesListTue.get(notePositionTue).noteDone = true;
                noteAdapterTue.notifyDataSetChanged();
                lv_notes_tue.setAdapter(noteAdapterTue);


                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Tuesday") //new
                        .child("NOTES").child(notesListTue.get(notePositionTue).note_time).setValue(notesListTue.get(notePositionTue));

            }

            return true;

        }
        else if (item.getItemId() == R.id.second_line) {
            Intent editIntent = new Intent(TuesdayActivity.this, NoteActivity.class);
            noteOgColorTue = notesListTue.get(notePositionTue).noteColor;
            editIntent.putExtra("edit", true);
            editIntent.putExtra("caller", "TuesdayActivity");
            startActivity(editIntent);
            return true;
        }

        else if (item.getItemId() == R.id.third_line){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new TuesdayActivity.HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new TuesdayActivity.HandlerAlertDialogListener());
            dialogMenu = builder.create();
            dialogMenu.show();

            return true;
        }

        return false;

    }

    @Override
    public void onClick(View view) {
        if (view == fab_edit_tue) {
            animationFab();
        }

        if (view == fab_clear_tue) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("This will delete all events that belong to this day");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            dialogClearDay = builder.create();
            dialogClearDay.show();
        }
        if (view == fab_add_tue) {
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("caller", "TuesdayActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    }

    public class HandlerAlertDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int number) {
            if (dialog == dialogMenu) {
                if (number == -1) {
                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Tuesday").child("NOTES")
                            .child(notesListTue.get(notePositionTue).note_time).removeValue();
                    notesListTue.remove(notePositionTue);
                    noteAdapterTue.notifyDataSetChanged();
                    lv_notes_tue.setAdapter(noteAdapterTue);
                }
            }
            else {
                if (number == -1) {
                    clearDay("Tuesday", notesListTue, noteAdapterTue, lv_notes_tue);
                    Toast.makeText(TuesdayActivity.this, "Starting a new Tuesday", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TuesdayActivity.this, MainActivity.class));
                }
            }
        }
    }
    private void animationFab () {
        if (isFabOpen) {
            fab_clear_tue.startAnimation(fabClose);
            fab_add_tue.startAnimation(fabClose);
            fab_clear_tue.setClickable(false);
            fab_add_tue.setClickable(false);
            isFabOpen = false;
        }
        else {
            fab_clear_tue.startAnimation(fabOpen);
            fab_add_tue.startAnimation(fabOpen);
            fab_clear_tue.setClickable(true);
            fab_add_tue.setClickable(true);
            isFabOpen = true;
        }
    }

    public void clearDay (String day, ArrayList<NoteItem> notesList, NoteAdapter noteAdapter, ListView listView) {

        Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("EVERY WEEK NOTES").orderByValue();

        myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("NOTES").removeValue();

        myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("DONE NOTES").removeValue();

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notesList.clear();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);
                    noteItem.everyWeek = true;
                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child(day).child("NOTES").child(noteItem.note_time).setValue(noteItem);
                    notesList.add(noteItem);
                    noteAdapter.notifyDataSetChanged();
                    listView.setAdapter(noteAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}