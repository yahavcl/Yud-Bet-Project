package com.example.yahavproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
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

import java.text.Annotation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class SundayActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title_sun;
    ListView lv_notes_sun;
    FloatingActionButton fab_edit_sun;
    FloatingActionButton fab_clear_sun;
    FloatingActionButton fab_add_sun;

    static ArrayList<NoteItem> notesListSun = new ArrayList<NoteItem>();
    static NoteAdapter noteAdapterSun;

    static int notePositionSun;
    static int noteOgColorSun;

    AlertDialog dialogMenu;
    AlertDialog dialogClearDay;

    Animation fabOpen, fabClose;
    boolean isFabOpen = false;



    @Override
    public void onBackPressed() {
        startActivity(new Intent(SundayActivity.this, MainActivity.class));
    }

    FirebaseDatabase database;
    DatabaseReference myRef;

    String last_screen;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunday);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        last_screen = getIntent().getStringExtra("last screen");


        tv_title_sun = (TextView)findViewById(R.id.tv_title_sun);
        fab_edit_sun = (FloatingActionButton)findViewById(R.id.fab_edit_sun);
        fab_clear_sun = (FloatingActionButton)findViewById(R.id.fab_clear_sun);
        fab_add_sun = (FloatingActionButton)findViewById(R.id.fab_add_sun);
        lv_notes_sun = (ListView) findViewById(R.id.lv_notes_sun);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.from_right_open_fab);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.to_right_close_fab);

        fab_add_sun.setOnClickListener(this);
        fab_edit_sun.setOnClickListener(this);
        fab_clear_sun.setOnClickListener(this);


        Query query = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child("Sunday").child("NOTES").orderByValue();


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notesListSun.clear();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);

                        if (noteItem.noteColor == R.color.done_note) {
                            //isNowDone = true;
                            notesListSun.add(noteItem);
                            noteItem.noteDone = true;
                            noteAdapterSun.notifyDataSetChanged();
                        } else {
                            //isNowDone = false;
                            notesListSun.add(noteItem);
                            noteItem.noteDone = false;
                            noteAdapterSun.notifyDataSetChanged();
                        }
                }

                Collections.sort(notesListSun, new Comparator<NoteItem>() {
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


        noteAdapterSun = new NoteAdapter(this, 0, 0, notesListSun);

        lv_notes_sun.setAdapter(noteAdapterSun);

        registerForContextMenu(lv_notes_sun);


        lv_notes_sun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                notePositionSun = position;
                startActivity(new Intent(SundayActivity.this, ShowNoteActivity.class)
                .putExtra("caller_to_open_note", "SundayActivity"));


            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);

        notePositionSun = info.position;

        if (notesListSun.get(notePositionSun).noteColor == R.color.done_note) {
            menu.findItem(R.id.first_line).setTitle("undone");
            menu.removeItem(R.id.second_line);

        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.first_line) {

            if (notesListSun.get(notePositionSun).noteColor == R.color.done_note) {

                Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child("Sunday").child("DONE NOTES").orderByValue();

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dst : dataSnapshot.getChildren()) {
                            NoteItem noteItem = dst.getValue(NoteItem.class);
                            if (noteItem.note_time.equals(notesListSun.get(notePositionSun).note_time)) {
                                notesListSun.get(notePositionSun).setNoteColor(noteItem.noteColor);
                                notesListSun.get(notePositionSun).noteDone = false;
                                noteAdapterSun.notifyDataSetChanged();
                                lv_notes_sun.setAdapter(noteAdapterSun);


                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days")
                                        .child("Sunday").child("NOTES").child(noteItem.note_time)
                                        .setValue(noteItem);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Sunday")
                                        .child("DONE NOTES").child(noteItem.note_time).removeValue();



                                //isNowDone = false;

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            else  {

                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Sunday")
                        .child("DONE NOTES").child(notesListSun.get(notePositionSun).note_time)
                        .setValue(notesListSun.get(notePositionSun));

                notesListSun.get(notePositionSun).setNoteColor(R.color.done_note);
                notesListSun.get(notePositionSun).noteDone = true;


                noteAdapterSun.notifyDataSetChanged();
                lv_notes_sun.setAdapter(noteAdapterSun);


                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Sunday")
                        .child("NOTES").child(notesListSun.get(notePositionSun).note_time).setValue(notesListSun.get(notePositionSun));

                //isNowDone = true;
            }

            return true;

        }
        else if (item.getItemId() == R.id.second_line) {
            Intent editIntent = new Intent(SundayActivity.this, NoteActivity.class);
            noteOgColorSun = notesListSun.get(notePositionSun).noteColor;
            editIntent.putExtra("edit", true);
            editIntent.putExtra("caller", "SundayActivity");
            startActivity(editIntent);
            return true;
        }

        else if (item.getItemId() == R.id.third_line){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            dialogMenu = builder.create();
            dialogMenu.show();

            return true;
        }

        return false;

        //return super.onContextItemSelected(item);
    }


    @Override
    public void onClick(View view) {

        if (view == fab_edit_sun) {
            animationFab();
        }

        if (view == fab_clear_sun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("This will delete all events that belong to this day");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            dialogClearDay = builder.create();
            dialogClearDay.show();
        }

        if (view == fab_add_sun) {
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("caller", "SundayActivity");
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
                            .child("days").child("Sunday").child("NOTES").child(notesListSun.get(notePositionSun).note_time).removeValue();
                    notesListSun.remove(notePositionSun);
                    noteAdapterSun.notifyDataSetChanged();
                    lv_notes_sun.setAdapter(noteAdapterSun);
                }
            }
            else {
                if (number == -1) {
                    clearDay("Sunday", notesListSun, noteAdapterSun, lv_notes_sun);
                    Toast.makeText(SundayActivity.this, "Starting a new Sunday", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SundayActivity.this, MainActivity.class));

                }
            }
        }
    }

    private void animationFab () {
        if (isFabOpen) {
            fab_clear_sun.startAnimation(fabClose);
            fab_add_sun.startAnimation(fabClose);
            fab_clear_sun.setClickable(false);
            fab_add_sun.setClickable(false);
            isFabOpen = false;
        }
        else {
            fab_clear_sun.startAnimation(fabOpen);
            fab_add_sun.startAnimation(fabOpen);
            fab_clear_sun.setClickable(true);
            fab_add_sun.setClickable(true);
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