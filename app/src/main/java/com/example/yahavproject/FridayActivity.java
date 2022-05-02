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

public class FridayActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title_fri;
    ListView lv_notes_fri;
    FloatingActionButton fab_add_fri;
    FloatingActionButton fab_edit_fri;
    FloatingActionButton fab_clear_fri;

    static ArrayList<NoteItem> notesListFri = new ArrayList<NoteItem>();
    static NoteAdapter noteAdapterFri;

    static int notePositionFri;
    static int noteOgColorFri;

    AlertDialog dialogMenu;
    AlertDialog dialogClearDay;

    Animation fabOpen, fabClose;
    boolean isFabOpen = false;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FridayActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friday);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        tv_title_fri = (TextView) findViewById(R.id.tv_title_fri);
        lv_notes_fri = (ListView) findViewById(R.id.lv_notes_fri);
        fab_add_fri = (FloatingActionButton) findViewById(R.id.fab_add_fri);
        fab_edit_fri = (FloatingActionButton) findViewById(R.id.fab_edit_fri);
        fab_clear_fri = (FloatingActionButton) findViewById(R.id.fab_clear_fri);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.from_right_open_fab);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.to_right_close_fab);

        fab_add_fri.setOnClickListener(this);
        fab_edit_fri.setOnClickListener(this);
        fab_clear_fri.setOnClickListener(this);

        Query query = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child("Friday").child("NOTES").orderByValue();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notesListFri.clear();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);
                    if (noteItem.noteColor == R.color.done_note) {
                        notesListFri.add(noteItem);
                        noteItem.noteDone = true;
                        noteAdapterFri.notifyDataSetChanged();
                    }
                    else {
                        notesListFri.add(noteItem);
                        noteItem.noteDone = false;
                        noteAdapterFri.notifyDataSetChanged();
                    }
                }

                Collections.sort(notesListFri, new Comparator<NoteItem>() {
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

        noteAdapterFri = new NoteAdapter(this, 0, 0, notesListFri);
        lv_notes_fri.setAdapter(noteAdapterFri);

        registerForContextMenu(lv_notes_fri);

        lv_notes_fri.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                notePositionFri = position;
                startActivity(new Intent(FridayActivity.this, ShowNoteActivity.class)
                        .putExtra("caller_to_open_note", "FridayActivity"));
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);

        notePositionFri = info.position;

        if (notesListFri.get(notePositionFri).noteColor == R.color.done_note) {
            menu.findItem(R.id.first_line).setTitle("undone");
            menu.removeItem(R.id.second_line);

        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.first_line) {

            if (notesListFri.get(notePositionFri).noteColor == R.color.done_note) {

                Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child("Friday").child("DONE NOTES").orderByValue();

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dst : dataSnapshot.getChildren()) {
                            NoteItem noteItem = dst.getValue(NoteItem.class);
                            if (noteItem.note_time.equals(notesListFri.get(notePositionFri).note_time)) {
                                notesListFri.get(notePositionFri).setNoteColor(noteItem.noteColor);
                                notesListFri.get(notePositionFri).noteDone = false;
                                noteAdapterFri.notifyDataSetChanged();
                                lv_notes_fri.setAdapter(noteAdapterFri);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days") //new
                                        .child("Friday").child("NOTES").child(noteItem.note_time)
                                        .setValue(noteItem);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days")
                                        .child("Friday").child("DONE NOTES").child(noteItem.note_time).removeValue();



                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            else  {

                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Friday") //new
                        .child("DONE NOTES").child(notesListFri.get(notePositionFri).note_time)
                        .setValue(notesListFri.get(notePositionFri));


                notesListFri.get(notePositionFri).setNoteColor(R.color.done_note);
                notesListFri.get(notePositionFri).noteDone = true;
                noteAdapterFri.notifyDataSetChanged();
                lv_notes_fri.setAdapter(noteAdapterFri);


                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Friday") //new
                        .child("NOTES").child(notesListFri.get(notePositionFri).note_time).setValue(notesListFri.get(notePositionFri));


            }

            return true;

        }
        else if (item.getItemId() == R.id.second_line) {
            Intent editIntent = new Intent(FridayActivity.this, NoteActivity.class);
            noteOgColorFri = notesListFri.get(notePositionFri).noteColor;
            editIntent.putExtra("edit", true);
            editIntent.putExtra("caller", "FridayActivity");
            startActivity(editIntent);
            return true;
        }

        else if (item.getItemId() == R.id.third_line){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new FridayActivity.HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new FridayActivity.HandlerAlertDialogListener());
            dialogMenu = builder.create();
            dialogMenu.show();

            return true;
        }

        return false;
    }


    @Override
    public void onClick(View view) {
        if (view == fab_edit_fri) {
            animationFab();
        }

        if (view == fab_clear_fri) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("This will delete all events that belong to this day");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            dialogClearDay = builder.create();
            dialogClearDay.show();
        }
        if (view == fab_add_fri) {
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("caller", "FridayActivity");
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
                            .child("days").child("Friday").child("NOTES")
                            .child(notesListFri.get(notePositionFri).note_time).removeValue();
                    notesListFri.remove(notePositionFri);
                    noteAdapterFri.notifyDataSetChanged();
                    lv_notes_fri.setAdapter(noteAdapterFri);
                }
            }
            else {
                if (number == -1) {
                    clearDay("Friday", notesListFri, noteAdapterFri, lv_notes_fri);
                    Toast.makeText(FridayActivity.this, "Starting a new Friday", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FridayActivity.this, MainActivity.class));
                }
            }
        }
    }
    private void animationFab () {
        if (isFabOpen) {
            fab_clear_fri.startAnimation(fabClose);
            fab_add_fri.startAnimation(fabClose);
            fab_clear_fri.setClickable(false);
            fab_add_fri.setClickable(false);
            isFabOpen = false;
        }
        else {
            fab_clear_fri.startAnimation(fabOpen);
            fab_add_fri.startAnimation(fabOpen);
            fab_clear_fri.setClickable(true);
            fab_add_fri.setClickable(true);
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