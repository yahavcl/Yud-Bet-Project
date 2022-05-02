package com.example.yahavproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class MondayActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title_mon;
    ListView lv_notes_mon;
    FloatingActionButton fab_edit_mon;
    FloatingActionButton fab_clear_mon;
    FloatingActionButton fab_add_mon;

    static ArrayList<NoteItem> notesListMon = new ArrayList<NoteItem>();
    static NoteAdapter noteAdapterMon;

    static int notePositionMon;
    static int noteOgColorMon;

    FirebaseDatabase database;
    DatabaseReference myRef;

    AlertDialog dialogMenu;
    AlertDialog dialogClearDay;

    Animation fabOpen, fabClose;
    boolean isFabOpen = false;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MondayActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monday);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        tv_title_mon = (TextView)findViewById(R.id.tv_title_mon);
        lv_notes_mon = (ListView) findViewById(R.id.lv_notes_mon);
        fab_edit_mon = (FloatingActionButton)findViewById(R.id.fab_edit_mon);
        fab_clear_mon = (FloatingActionButton)findViewById(R.id.fab_clear_mon);
        fab_add_mon = (FloatingActionButton)findViewById(R.id.fab_add_mon);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.from_right_open_fab);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.to_right_close_fab);

        fab_add_mon.setOnClickListener(this);
        fab_edit_mon.setOnClickListener(this);
        fab_clear_mon.setOnClickListener(this);


        Query query = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child("Monday").child("NOTES").orderByValue();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notesListMon.clear();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);
                    if (noteItem.noteColor == R.color.done_note) {
                        notesListMon.add(noteItem);
                        noteItem.noteDone = true;
                        noteAdapterMon.notifyDataSetChanged();
                    }
                    else {
                        notesListMon.add(noteItem);
                        noteItem.noteDone = false;
                        noteAdapterMon.notifyDataSetChanged();
                    }
                }

                Collections.sort(notesListMon, new Comparator<NoteItem>() {
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

        noteAdapterMon = new NoteAdapter(this, 0, 0, notesListMon);
        lv_notes_mon.setAdapter(noteAdapterMon);

        registerForContextMenu(lv_notes_mon);

        lv_notes_mon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                notePositionMon = position;
                startActivity(new Intent(MondayActivity.this, ShowNoteActivity.class)
                .putExtra("caller_to_open_note", "MondayActivity"));
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);

        notePositionMon = info.position;

        if (notesListMon.get(notePositionMon).noteColor == R.color.done_note) {
            menu.findItem(R.id.first_line).setTitle("undone");
            menu.removeItem(R.id.second_line);

        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.first_line) {

            if (notesListMon.get(notePositionMon).noteColor == R.color.done_note) {

                Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child("Monday").child("DONE NOTES").orderByValue();

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dst : dataSnapshot.getChildren()) {
                            NoteItem noteItem = dst.getValue(NoteItem.class);
                            if (noteItem.note_time.equals(notesListMon.get(notePositionMon).note_time)) {
                                notesListMon.get(notePositionMon).setNoteColor(noteItem.noteColor);
                                notesListMon.get(notePositionMon).noteDone = false;
                                noteAdapterMon.notifyDataSetChanged();
                                lv_notes_mon.setAdapter(noteAdapterMon);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days") //new
                                        .child("Monday").child("NOTES").child(noteItem.note_time)
                                        .setValue(noteItem);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days")
                                        .child("Monday").child("DONE NOTES").child(noteItem.note_time).removeValue();



                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            else  {

                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Monday") //new
                        .child("DONE NOTES").child(notesListMon.get(notePositionMon).note_time)
                        .setValue(notesListMon.get(notePositionMon));


                notesListMon.get(notePositionMon).setNoteColor(R.color.done_note);
                notesListMon.get(notePositionMon).noteDone = true;
                noteAdapterMon.notifyDataSetChanged();
                lv_notes_mon.setAdapter(noteAdapterMon);


                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Monday") //new
                        .child("NOTES").child(notesListMon.get(notePositionMon).note_time).setValue(notesListMon.get(notePositionMon));

                //isNowDone = true;
            }



            return true;

        }
        else if (item.getItemId() == R.id.second_line) {
            Intent editIntent = new Intent(MondayActivity.this, NoteActivity.class);
            noteOgColorMon = notesListMon.get(notePositionMon).noteColor;
            editIntent.putExtra("edit", true);
            editIntent.putExtra("caller", "MondayActivity");
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

    }

    @Override
    public void onClick(View view) {

        if (view == fab_edit_mon) {
            animationFab();
        }

        if (view == fab_clear_mon) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("This will delete all events that belong to this day");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            dialogClearDay = builder.create();
            dialogClearDay.show();
        }

        if (view == fab_add_mon) {
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("caller", "MondayActivity");
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
                            .child("days").child("Monday").child("NOTES")
                            .child(notesListMon.get(notePositionMon).note_time).removeValue();
                    notesListMon.remove(notePositionMon);
                    noteAdapterMon.notifyDataSetChanged();
                    lv_notes_mon.setAdapter(noteAdapterMon);
                }
            }
            else {
                if (number == -1) {
                    clearDay("Monday", notesListMon, noteAdapterMon, lv_notes_mon);
                    Toast.makeText(MondayActivity.this, "Starting a new Monday", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MondayActivity.this, MainActivity.class));
                }
            }

        }
    }
    private void animationFab () {
        if (isFabOpen) {
            fab_clear_mon.startAnimation(fabClose);
            fab_add_mon.startAnimation(fabClose);
            fab_clear_mon.setClickable(false);
            fab_add_mon.setClickable(false);
            isFabOpen = false;
        }
        else {
            fab_clear_mon.startAnimation(fabOpen);
            fab_add_mon.startAnimation(fabOpen);
            fab_clear_mon.setClickable(true);
            fab_add_mon.setClickable(true);
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