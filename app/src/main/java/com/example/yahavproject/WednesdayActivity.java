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

import com.google.android.material.bottomappbar.BottomAppBar;
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

public class WednesdayActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title_wed;
    ListView lv_notes_wed;
    FloatingActionButton fab_add_wed;
    FloatingActionButton fab_clear_wed;
    FloatingActionButton fab_edit_wed;

    static ArrayList<NoteItem> notesListWed = new ArrayList<NoteItem>();
    static NoteAdapter noteAdapterWed;

    static int notePositionWed;
    static int noteOgColorWed;

    FirebaseDatabase database;
    DatabaseReference myRef;

    AlertDialog dialogMenu;
    AlertDialog dialogClearDay;

    Animation fabOpen, fabClose;
    boolean isFabOpen = false;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(WednesdayActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wednesday);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        tv_title_wed = (TextView) findViewById(R.id.tv_title_wed);
        lv_notes_wed = (ListView) findViewById(R.id.lv_notes_wed);
        fab_add_wed = (FloatingActionButton) findViewById(R.id.fab_add_wed);
        fab_edit_wed = (FloatingActionButton) findViewById(R.id.fab_edit_wed);
        fab_clear_wed = (FloatingActionButton) findViewById(R.id.fab_clear_wed);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.from_right_open_fab);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.to_right_close_fab);

        fab_add_wed.setOnClickListener(this);
        fab_edit_wed.setOnClickListener(this);
        fab_clear_wed.setOnClickListener(this);

        Query query = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child("Wednesday").child("NOTES").orderByValue();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notesListWed.clear();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);
                    if (noteItem.noteColor == R.color.done_note) {
                        notesListWed.add(noteItem);
                        noteItem.noteDone = true;
                        noteAdapterWed.notifyDataSetChanged();
                    }
                    else {
                        notesListWed.add(noteItem);
                        noteItem.noteDone = false;
                        noteAdapterWed.notifyDataSetChanged();
                    }
                }

                Collections.sort(notesListWed, new Comparator<NoteItem>() {
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

        noteAdapterWed = new NoteAdapter(this, 0, 0, notesListWed);
        lv_notes_wed.setAdapter(noteAdapterWed);

        registerForContextMenu(lv_notes_wed);

        lv_notes_wed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                notePositionWed = position;
                startActivity(new Intent(WednesdayActivity.this, ShowNoteActivity.class)
                        .putExtra("caller_to_open_note", "WednesdayActivity"));
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);

        notePositionWed = info.position;

        if (notesListWed.get(notePositionWed).noteColor == R.color.done_note) {
            menu.findItem(R.id.first_line).setTitle("undone");
            menu.removeItem(R.id.second_line);

        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.first_line) {

            if (notesListWed.get(notePositionWed).noteColor == R.color.done_note) {

                Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child("Wednesday").child("DONE NOTES").orderByValue();

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dst : dataSnapshot.getChildren()) {
                            NoteItem noteItem = dst.getValue(NoteItem.class);
                            if (noteItem.note_time.equals(notesListWed.get(notePositionWed).note_time)) {
                                notesListWed.get(notePositionWed).setNoteColor(noteItem.noteColor);
                                notesListWed.get(notePositionWed).noteDone = false;
                                noteAdapterWed.notifyDataSetChanged();
                                lv_notes_wed.setAdapter(noteAdapterWed);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days") //new
                                        .child("Wednesday").child("NOTES").child(noteItem.note_time)
                                        .setValue(noteItem);

                                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days")
                                        .child("Wednesday").child("DONE NOTES").child(noteItem.note_time).removeValue();



                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            else  {

                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Wednesday") //new
                        .child("DONE NOTES").child(notesListWed.get(notePositionWed).note_time)
                        .setValue(notesListWed.get(notePositionWed));


                notesListWed.get(notePositionWed).setNoteColor(R.color.done_note);
                notesListWed.get(notePositionWed).noteDone = true;
                noteAdapterWed.notifyDataSetChanged();
                lv_notes_wed.setAdapter(noteAdapterWed);


                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email).child("days").child("Wednesday") //new
                        .child("NOTES").child(notesListWed.get(notePositionWed).note_time).setValue(notesListWed.get(notePositionWed));


            }

            return true;

        }
        else if (item.getItemId() == R.id.second_line) {
            Intent editIntent = new Intent(WednesdayActivity.this, NoteActivity.class);
            noteOgColorWed = notesListWed.get(notePositionWed).noteColor;
            editIntent.putExtra("edit", true);
            editIntent.putExtra("caller", "WednesdayActivity");
            startActivity(editIntent);
            return true;
        }

        else if (item.getItemId() == R.id.third_line){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("Are you sure you want to delete?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new WednesdayActivity.HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new WednesdayActivity.HandlerAlertDialogListener());
            dialogMenu = builder.create();
            dialogMenu.show();

            return true;
        }

        return false;

    }


    @Override
    public void onClick(View view) {
        if (view == fab_edit_wed) {
            animationFab();
        }

        if (view == fab_clear_wed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("This will delete all events that belong to this day");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            dialogClearDay = builder.create();
            dialogClearDay.show();
        }
        if (view == fab_add_wed) {
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("caller", "WednesdayActivity");
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
                            .child("days").child("Wednesday").child("NOTES")
                            .child(notesListWed.get(notePositionWed).note_time).removeValue();
                    notesListWed.remove(notePositionWed);
                    noteAdapterWed.notifyDataSetChanged();
                    lv_notes_wed.setAdapter(noteAdapterWed);
                }
            }
            else {
                if (number == -1) {
                    clearDay("Wednesday", notesListWed, noteAdapterWed, lv_notes_wed);
                    Toast.makeText(WednesdayActivity.this, "Starting a new Wednesday", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WednesdayActivity.this, MainActivity.class));
                }
            }
        }
    }
    private void animationFab () {
        if (isFabOpen) {
            fab_clear_wed.startAnimation(fabClose);
            fab_add_wed.startAnimation(fabClose);
            fab_clear_wed.setClickable(false);
            fab_add_wed.setClickable(false);
            isFabOpen = false;
        }
        else {
            fab_clear_wed.startAnimation(fabOpen);
            fab_add_wed.startAnimation(fabOpen);
            fab_clear_wed.setClickable(true);
            fab_add_wed.setClickable(true);
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