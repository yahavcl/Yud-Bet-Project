package com.example.yahavproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout screen;

    TextView tv_title;
    TextView tv_time;
    TextView tv_description;
    EditText et_title;
    EditText et_time;
    EditText et_description;
    CheckBox cb_every_day;
    CheckBox cb_every_week;
    Button btn_delete;
    Button btn_save;
    Button btn_green;
    Button btn_blue;
    Button btn_red;
    Button btn_yellow;
    Button btn_purple;
    Button btn_clean;
    ImageButton im_time_picker;

    Calendar calendar;
    int mHour;
    int mMinute;

    //String currentDate;

    FirebaseDatabase database;
    DatabaseReference myRef;

    int lastColor = R.color.white;

    String caller;

    String OG_title;
    String OG_time;
    String OG_description;
    boolean OG_every_day_checked = false;
    boolean OG_every_week_checked = false;

    NoteItem currentNote;

    boolean every_day_checked = false;
    boolean every_week_checked = false;

    int noteColorNow;

    AlertDialog deleteDialog;

    MyBroadcastReceiver_NetworkConnectivity receiver_networkConnectivity = new MyBroadcastReceiver_NetworkConnectivity();
    boolean isRegister = false;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        caller = getIntent().getStringExtra("caller");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        screen = (LinearLayout)findViewById(R.id.screen);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tv_description = (TextView)findViewById(R.id.tv_description);
        et_title = (EditText)findViewById(R.id.et_title);
        et_time = (EditText)findViewById(R.id.et_time);
        et_description = (EditText)findViewById(R.id.et_description);
        cb_every_day = (CheckBox)findViewById(R.id.cb_every_day);
        cb_every_week = (CheckBox)findViewById(R.id.cb_every_week);
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_save = (Button)findViewById(R.id.btn_save);
        im_time_picker = (ImageButton)findViewById(R.id.im_time_picker);

        btn_green = (Button)findViewById(R.id.btn_green);
        btn_blue = (Button)findViewById(R.id.btn_blue);
        btn_red = (Button)findViewById(R.id.btn_red);
        btn_yellow = (Button)findViewById(R.id.btn_yellow);
        btn_purple = (Button)findViewById(R.id.btn_purple);
        btn_clean = (Button)findViewById(R.id.btn_clean);

/*        SharedPreferences prefDay = getSharedPreferences("checkboxDay", MODE_PRIVATE);
        String checkboxDay = prefDay.getString("everyDay", "");
        SharedPreferences prefWeek = getSharedPreferences("checkboxWeek", MODE_PRIVATE);
        String checkboxWeek = prefWeek.getString("everyWeek", "");*/

        //SharedPreferences sharedPrefDay = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);

        //currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        if (getIntent().getBooleanExtra("edit", false) == true) {

            //String day = "";

            if (caller.equals("SundayActivity")) {
                OG_note(SundayActivity.notesListSun, SundayActivity.notePositionSun, SundayActivity.noteOgColorSun);
                //day = "Sunday";
            }
            if (caller.equals("MondayActivity")) {
                OG_note(MondayActivity.notesListMon, MondayActivity.notePositionMon, MondayActivity.noteOgColorMon);
                //day = "Monday";
            }
            if (caller.equals("TuesdayActivity")) {
                OG_note(TuesdayActivity.notesListTue, TuesdayActivity.notePositionTue, TuesdayActivity.noteOgColorTue);
                //day = "Tuesday";
            }
            if (caller.equals("WednesdayActivity")) {
                OG_note(WednesdayActivity.notesListWed, WednesdayActivity.notePositionWed, WednesdayActivity.noteOgColorWed);
                //day = "Wednesday";
            }
            if (caller.equals("ThursdayActivity")) {
                OG_note(ThursdayActivity.notesListThu, ThursdayActivity.notePositionThu, ThursdayActivity.noteOgColorThu);
                //day = "Thursday";
            }
            if (caller.equals("FridayActivity")) {
                OG_note(FridayActivity.notesListFri, FridayActivity.notePositionFri, FridayActivity.noteOgColorFri);
                //day = "Friday";
            }
            if (caller.equals("SaturdayActivity")) {
                OG_note(SaturdayActivity.notesListSat, SaturdayActivity.notePositionSat, SaturdayActivity.noteOgColorSat);
                //day = "Saturday";
            }

            et_title.setText(OG_title);
            et_time.setText(OG_time);
            et_description.setText(OG_description);
            screen.setBackgroundResource(noteColorNow);
            lastColor = noteColorNow;


            if (noteColorNow == R.color.blue_button) {
                btn_blue.setBackgroundTintMode(PorterDuff.Mode.SCREEN);
            }

            if (noteColorNow == R.color.red_button) {
                btn_red.setBackgroundTintMode(PorterDuff.Mode.SCREEN);
            }

            if (noteColorNow == R.color.green_button) {
                btn_green.setBackgroundTintMode(PorterDuff.Mode.SCREEN);
            }

            if (noteColorNow == R.color.yellow_button) {
                btn_yellow.setBackgroundTintMode(PorterDuff.Mode.SCREEN);
            }

            if (noteColorNow == R.color.purple_button) {
                btn_purple.setBackgroundTintMode(PorterDuff.Mode.SCREEN);
            }

            if (OG_every_week_checked) {
                cb_every_week.setChecked(true);
                every_week_checked = true;
            }

/*            else if (!OG_every_week_checked) {
                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child(day).child("EVERY WEEK NOTES").child(OG_time).removeValue();
            }*/
        }

        btn_delete.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        im_time_picker.setOnClickListener(this);

        btn_green.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_red.setOnClickListener(this);
        btn_yellow.setOnClickListener(this);
        btn_purple.setOnClickListener(this);
        btn_clean.setOnClickListener(this);

        calendar = Calendar.getInstance();
        mHour = calendar.get(calendar.HOUR_OF_DAY);
        mMinute = calendar.get(calendar.MINUTE);


/*        if (checkboxDay.equals("true")) {
            cb_every_day.setChecked(true);
        }

        if (checkboxWeek.equals("true")) {
            cb_every_week.setChecked(true);
        }*/


        cb_every_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
/*                    SharedPreferences.Editor editor = prefDay.edit();
                    if (OG_every_day_checked) {
                        editor.putString("everyDay", "true");
                        editor.apply();
                    }*/
                    every_day_checked = true;
                }
                else if (!compoundButton.isChecked()) {
                    every_day_checked = false;
                }
            }
        });

        cb_every_week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
/*                    SharedPreferences.Editor editor = prefWeek.edit();
                    if (OG_every_week_checked) {
                        editor.putString("everyWeek", "true");
                        editor.apply();
                    }*/
                    every_week_checked = true;
                }
                else if (!compoundButton.isChecked()) {
                    every_week_checked = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        int firstColor = screen.getSolidColor();

        if (view != btn_delete && view != btn_save) {
            btn_green.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            btn_blue.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            btn_red.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            btn_yellow.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            btn_purple.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
        }

        if (view == btn_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning");
            builder.setMessage("Changes will not be saved");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new HandlerAlertDialogListener());
            deleteDialog = builder.create();
            deleteDialog.show();
        }

        if (view == im_time_picker) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(NoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    if (hourOfDay < 10) {
                        if (minute < 10)
                            et_time.setText("0"+hourOfDay+":"+"0"+minute);
                        else
                            et_time.setText("0"+hourOfDay+":"+minute);
                    }
                    else {
                        if (minute < 10)
                            et_time.setText(hourOfDay+":"+"0"+minute);
                        else
                            et_time.setText(hourOfDay+":"+minute);
                    }
                }
            }, mHour, mMinute, true);
            timePickerDialog.show();
        }

        if (view == btn_green) {
            onColorButtonClick(btn_green, R.color.green_button);
        }

        if (view == btn_blue) {
            onColorButtonClick(btn_blue, R.color.blue_button);
        }

        if (view == btn_red) {
            onColorButtonClick(btn_red, R.color.red_button);
        }

        if (view == btn_yellow) {
            onColorButtonClick(btn_yellow, R.color.yellow_button);
        }

        if (view == btn_purple) {
            onColorButtonClick(btn_purple, R.color.purple_button);
        }

        else if (view == btn_clean) {
            screen.setBackgroundColor(firstColor);
            lastColor = R.color.white;
        }

        if (view == btn_save) {

            receiver_networkConnectivity.onReceive(this, this.getIntent());


            if (et_title.getText().toString().trim().length() == 0 || et_time.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Enter title and time", Toast.LENGTH_LONG).show();
            }

            else if (LoginActivity.disconnected) {
                Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
            }

            else {
                String noteTitle = et_title.getText().toString();
                String noteTime = et_time.getText().toString();
                String noteDescription = et_description.getText().toString();

                NoteItem n = new NoteItem(noteTitle, noteTime, noteDescription, lastColor);

                if (every_day_checked) {

                    n.everyDay = true;

                    everyDayChecked("Sunday", n);
                    everyDayChecked("Monday", n);
                    everyDayChecked("Tuesday", n);
                    everyDayChecked("Wednesday", n);
                    everyDayChecked("Thursday", n);
                    everyDayChecked("Friday", n);
                    everyDayChecked("Saturday", n);


/*                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Sunday").child("NOTES").child(noteTime).setValue(n);

                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Monday").child("NOTES").child(noteTime).setValue(n);

                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Tuesday").child("NOTES").child(noteTime).setValue(n);

                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Wednesday").child("NOTES").child(noteTime).setValue(n);

                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Thursday").child("NOTES").child(noteTime).setValue(n);

                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Friday").child("NOTES").child(noteTime).setValue(n);

                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child("Saturday").child("NOTES").child(noteTime).setValue(n);*/

                }


                if (caller.equals("SundayActivity")) {
                    fireBaseNewNote("Sunday", n, SundayActivity.noteOgColorSun, SundayActivity.notesListSun,
                            SundayActivity.noteAdapterSun);

                    startActivity(new Intent(NoteActivity.this, SundayActivity.class));
                }

                else if (caller.equals("MondayActivity")) {
                    fireBaseNewNote("Monday", n, MondayActivity.noteOgColorMon, MondayActivity.notesListMon,
                            MondayActivity.noteAdapterMon);
                    startActivity(new Intent(NoteActivity.this, MondayActivity.class));
                }

                else if (caller.equals("TuesdayActivity")) {
                    fireBaseNewNote("Tuesday", n, TuesdayActivity.noteOgColorTue, TuesdayActivity.notesListTue,
                            TuesdayActivity.noteAdapterTue);
                    startActivity(new Intent(NoteActivity.this, TuesdayActivity.class));
                }

                else if (caller.equals("WednesdayActivity")) {
                    fireBaseNewNote("Wednesday", n, WednesdayActivity.noteOgColorWed, WednesdayActivity.notesListWed,
                            WednesdayActivity.noteAdapterWed);
                    startActivity(new Intent(NoteActivity.this, WednesdayActivity.class));
                }

                else if (caller.equals("ThursdayActivity")) {
                    fireBaseNewNote("Thursday", n, ThursdayActivity.noteOgColorThu, ThursdayActivity.notesListThu,
                            ThursdayActivity.noteAdapterThu);
                    startActivity(new Intent(NoteActivity.this, ThursdayActivity.class));
                }

                else if (caller.equals("FridayActivity")) {
                    fireBaseNewNote("Friday", n, FridayActivity.noteOgColorFri, FridayActivity.notesListFri,
                            FridayActivity.noteAdapterFri);
                    startActivity(new Intent(NoteActivity.this, FridayActivity.class));
                }

                else if (caller.equals("SaturdayActivity")) {
                    fireBaseNewNote("Saturday", n, SaturdayActivity.noteOgColorSat, SaturdayActivity.notesListSat,
                            SaturdayActivity.noteAdapterSat);
                    startActivity(new Intent(NoteActivity.this, SaturdayActivity.class));
                }

                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

        }

    }

    public class HandlerAlertDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int number) {
            if (number == -1) {
                onBackPressed();
            }
        }
    }

    public void OG_note (ArrayList<NoteItem> noteList, int position, int ogColor) {

        currentNote = noteList.get(position);
        if (!noteList.isEmpty()) {
            noteColorNow = ogColor;
            OG_title = currentNote.note_title;
            OG_time = currentNote.note_time;
            OG_description = currentNote.description;
            OG_every_day_checked = currentNote.everyDay;
            OG_every_week_checked = currentNote.everyWeek;

/*            every_day_checked = currentNote.everyDay;
            every_week_checked = currentNote.everyWeek;*/
        }
    }

    public void onColorButtonClick (Button button, int buttonColor) {
        button.setBackgroundTintMode(PorterDuff.Mode.SCREEN);
        screen.setBackgroundColor(button.getBackgroundTintList().getDefaultColor());
        lastColor = buttonColor;
    }
    public void fireBaseNewNote(String day, NoteItem note, int ogColor, ArrayList<NoteItem> noteList, NoteAdapter noteAdapter) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        if (getIntent().getBooleanExtra("edit", false) == true) {

            if (note.note_title != OG_title || note.note_time != OG_time ||
                    note.description != OG_description ||
                    screen.getSolidColor() != ogColor) {

                myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                        .child("days").child(day).child("NOTES").child(OG_time).removeValue();
            }
        }

        if (every_week_checked) {
            myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                    .child("days").child(day).child("EVERY WEEK NOTES").child(note.note_time).setValue(note);
            note.everyWeek = true;
        }

        else if (!every_week_checked) {
            myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                    .child("days").child(day).child("EVERY WEEK NOTES").child(note.note_time).removeValue();
        }
        myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("NOTES").child(note.note_time).setValue(note);

        noteList.add(note);
        noteAdapter.notifyDataSetChanged();
    }

    public void everyDayChecked (String day, NoteItem note) {
        myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("NOTES").child(note.note_time).setValue(note);

        if (every_week_checked) {
            myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                    .child("days").child(day).child("EVERY WEEK NOTES").child(note.note_time).setValue(note);
            note.everyWeek = true;
        }
        else if (!every_week_checked) {
            myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                    .child("days").child(day).child("EVERY WEEK NOTES").child(note.note_time).removeValue();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver_networkConnectivity, filter);
        if (receiver_networkConnectivity != null)
            isRegister = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isRegister)
            unregisterReceiver(receiver_networkConnectivity);
    }

}