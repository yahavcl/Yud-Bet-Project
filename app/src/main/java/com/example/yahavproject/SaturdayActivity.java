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
import java.util.List;

public class SaturdayActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title_sat;
    ListView lv_notes_sat;
    FloatingActionButton fab_add_sat;
    FloatingActionButton fab_edit_sat;
    FloatingActionButton fab_clear_sat;

    static ArrayList<NoteItem> notesListSat = new ArrayList<NoteItem>();
    static NoteAdapter noteAdapterSat;

    static int notePositionSat;
    static int noteOgColorSat;

    AlertDialog dialogMenu;
    AlertDialog dialogClearDay;

    Animation fabOpen, fabClose;
    boolean isFabOpen = false;


    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SaturdayActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saturday);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        tv_title_sat = (TextView) findViewById(R.id.tv_title_sat);
        lv_notes_sat = (ListView) findViewById(R.id.lv_notes_sat);
        fab_add_sat = (FloatingActionButton) findViewById(R.id.fab_add_sat);
        fab_edit_sat = (FloatingActionButton) findViewById(R.id.fab_edit_sat);
        fab_clear_sat = (FloatingActionButton) findViewById(R.id.fab_clear_sat);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.from_right_open_fab);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.to_right_close_fab);

        fab_add_sat.setOnClickListener(this);
        fab_edit_sat.setOnClickListener(this);
        fab_clear_sat.setOnClickListener(this);

        Query query = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child("Saturday").child("NOTES").orderByValue();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notesListSat.clear();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);
                    if (noteItem.noteColor == R.color.done_note) {
                        notesListSat.add(noteItem);
                        noteItem.noteDone = true;
                        noteAdapterSat.notifyDataSetChanged();
                    }
                    else {
                        notesListSat.add(noteItem);
                        noteItem.noteDone = false;
                        noteAdapterSat.notifyDataSetChanged();
                    }
                }

                Collections.sort(notesListSat, new Comparator<NoteItem>() {
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

        noteAdapterSat = new NoteAdapter(this, 0, 0, notesListSat);
        lv_notes_sat.setAdapter(noteAdapterSat);

        registerForContextMenu(lv_notes_sat);

        lv_notes_sat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                notePositionSat = position;
                startActivity(new Intent(SaturdayActivity.this, ShowNoteActivity.class)
                        .putExtra("caller_to_open_note", "SaturdayActivity"));
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);

        notePositionSat = info.position;

        if (notesListSat.get(notePositionSat).noteColor == R.color.done_note) {
            menu.findItem(R.id.first_line).setTitle("undone");
            menu.removeItem(R.id.second_line);

        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.first_line) {

            if (notesListSat.get(notePositionSat).noteColor == R.color.done_note) {

                Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child("Saturday").child("DONE NOTES").orderByValue();

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dst : dataSnapshot.getChildren()) {
                            NoteItem noteItem = dst.getValue(NoteItem.class);
                            if (noteItem.note_time.equals(notesListSat.get(notePositionSat).note_time)) {
                                notesListSat.get(notePositionSat).setNoteColor(noteItem.noteColor);
                                notesListSat.get(notePositionSat).noteDone = false;
                                noteAdapterSat.notifyDataSetChanged();
                                lv_notes_sat.setAdapter(noteAdapterSat);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days") //new
                                        .child("Saturday").child("NOTES").child(noteItem.note_time)
                                        .setValue(noteItem);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days")
                                        .child("Saturday").child("DONE NOTES").child(noteItem.note_time).removeValue();



                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            else  {

                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Saturday") //new
                        .child("DONE NOTES").child(notesListSat.get(notePositionSat).note_time)
                        .setValue(notesListSat.get(notePositionSat));


                notesListSat.get(notePositionSat).setNoteColor(R.color.done_note);
                notesListSat.get(notePositionSat).noteDone = true;
                noteAdapterSat.notifyDataSetChanged();
                lv_notes_sat.setAdapter(noteAdapterSat);


                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Saturday") //new
                        .child("NOTES").child(notesListSat.get(notePositionSat).note_time).setValue(notesListSat.get(notePositionSat));


            }

            return true;

        }
        else if (item.getItemId() == R.id.second_line) {
            Intent editIntent = new Intent(SaturdayActivity.this, NoteActivity.class);
            noteOgColorSat = notesListSat.get(notePositionSat).noteColor;
            editIntent.putExtra("edit", true);
            editIntent.putExtra("caller", "SaturdayActivity");
            startActivity(editIntent);
            return true;
        }

        else if (item.getItemId() == R.id.third_line){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new SaturdayActivity.HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new SaturdayActivity.HandlerAlertDialogListener());
            dialogMenu = builder.create();
            dialogMenu.show();

            return true;
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == fab_edit_sat) {
            animationFab();
        }

        if (view == fab_clear_sat) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("This will delete all events that belong to this day");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            dialogClearDay = builder.create();
            dialogClearDay.show();
        }
        if (view == fab_add_sat) {
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("caller", "SaturdayActivity");
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
                            .child("days").child("Saturday").child("NOTES")
                            .child(notesListSat.get(notePositionSat).note_time).removeValue();
                    notesListSat.remove(notePositionSat);
                    noteAdapterSat.notifyDataSetChanged();
                    lv_notes_sat.setAdapter(noteAdapterSat);
                }
            }
            else {
                if (number == -1) {
                    clearDay("Saturday", notesListSat, noteAdapterSat, lv_notes_sat);
                    Toast.makeText(SaturdayActivity.this, "Starting a new Saturday", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SaturdayActivity.this, MainActivity.class));
                }
            }
        }
    }
    private void animationFab () {
        if (isFabOpen) {
            fab_clear_sat.startAnimation(fabClose);
            fab_add_sat.startAnimation(fabClose);
            fab_clear_sat.setClickable(false);
            fab_add_sat.setClickable(false);
            isFabOpen = false;
        }
        else {
            fab_clear_sat.startAnimation(fabOpen);
            fab_add_sat.startAnimation(fabOpen);
            fab_clear_sat.setClickable(true);
            fab_add_sat.setClickable(true);
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