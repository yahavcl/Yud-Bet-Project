package com.example.yahavproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lv_day_list;
    ArrayList<Day> dayList;
    TextView tv_full_week;

    Button arrowSun;
    Button arrowMon;
    Button arrowTue;
    Button arrowWed;
    Button arrowThu;
    Button arrowFri;
    Button arrowSat;

    Button btn_log_out;
    Button btn_restart_week;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    Intent notifications_service;

    String currentDay;

    AlertDialog clearWeekDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lv_day_list = (ListView) findViewById(R.id.lv_day_list);
        tv_full_week = (TextView) findViewById(R.id.tv_full_week);

        btn_log_out = (Button)findViewById(R.id.btn_log_out);
        btn_restart_week = (Button)findViewById(R.id.btn_restart_week);

        btn_restart_week.setOnClickListener(this);

        currentDay = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date());

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        //String phoneUser = sharedPref.getString(getString(R.string.remember_user), "");

        btn_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                finish();

                LoginActivity.rememberUser = false;
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });

/*        if (LoginActivity.rememberUser) {
            notifications_service = new Intent(MainActivity.this, NotificationService.class);
            startService(notifications_service);
        }*/
/*        if (phoneUser.equals("true")) {

        }*/

        notifications_service = new Intent(MainActivity.this, NotificationService.class);
        startService(notifications_service);


        Day sunday = new Day("Sunday", arrowSun);
        Day monday = new Day("Monday", arrowMon);
        Day tuesday = new Day("Tuesday", arrowTue);
        Day wednesday = new Day("Wednesday", arrowWed);
        Day thursday = new Day("Thursday", arrowThu);
        Day friday = new Day("Friday", arrowFri);
        Day saturday = new Day("Saturday", arrowSat);


        dayList = new ArrayList<Day>();
        dayList.add(sunday);
        dayList.add(monday);
        dayList.add(tuesday);
        dayList.add(wednesday);
        dayList.add(thursday);
        dayList.add(friday);
        dayList.add(saturday);


        lv_day_list.setAdapter(new MyListAdapter(this, R.layout.one_line, dayList));


    }

    @Override
    public void onClick(View view) {

        if (view == btn_restart_week) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning");
            builder.setMessage("This will delete all of your events this week to make room for new events next week");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new MainActivity.HandlerAlertDialogListener());
            builder.setNegativeButton("Cancel", new MainActivity.HandlerAlertDialogListener());
            clearWeekDialog = builder.create();
            clearWeekDialog.show();
        }
    }

    private class MyListAdapter extends ArrayAdapter<Day> {

        //private int mySpecialLayout;
        public MyListAdapter(Context context, int resource, List<Day> objects) {
            super(context, resource, objects);

            //mySpecialLayout = resource;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder if_view_holder_is_not_null = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.one_line,parent,false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.viewHolderTextView = (TextView)convertView.findViewById(R.id.tv_title_sun);
                viewHolder.viewHolderButton = (Button) convertView.findViewById(R.id.btn_oneLine_arrow);

                Day temp = dayList.get(position);

                viewHolder.viewHolderTextView.setText((temp.getName()));

                if (currentDay.equals("Sun") && position == 0) {
                    viewHolder.viewHolderTextView.setBackgroundResource(R.drawable.current_day_shape);
                }

                if (currentDay.equals("Mon") && position == 1) {
                    viewHolder.viewHolderTextView.setBackgroundResource(R.drawable.current_day_shape);
                }

                if (currentDay.equals("Tue") && position == 2) {
                    viewHolder.viewHolderTextView.setBackgroundResource(R.drawable.current_day_shape);
                }

                if (currentDay.equals("Wed") && position == 3) {
                    viewHolder.viewHolderTextView.setBackgroundResource(R.drawable.current_day_shape);
                }

                if (currentDay.equals("Thu") && position == 4) {
                    viewHolder.viewHolderTextView.setBackgroundResource(R.drawable.current_day_shape);
                }

                if (currentDay.equals("Fri") && position == 5) {
                    viewHolder.viewHolderTextView.setBackgroundResource(R.drawable.current_day_shape);
                }

                if (currentDay.equals("Sat") && position == 6) {
                    viewHolder.viewHolderTextView.setBackgroundResource(R.drawable.current_day_shape);
                }

                viewHolder.viewHolderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getContext(),"button of list LINE number "+position +" was clicked", Toast.LENGTH_SHORT).show();
                        //receiver_networkConnectivity.onReceive(MainActivity.this, MainActivity.this.getIntent());

                        if (position == 0) {
                            startActivity(new Intent(MainActivity.this, SundayActivity.class));
                        }

                        else if (position == 1) {
                            startActivity(new Intent(MainActivity.this, MondayActivity.class));
                        }

                        else if (position == 2) {
                            startActivity(new Intent(MainActivity.this, TuesdayActivity.class));
                        }

                        else if (position == 3) {
                            startActivity(new Intent(MainActivity.this, WednesdayActivity.class));
                        }

                        else if (position == 4) {
                            startActivity(new Intent(MainActivity.this, ThursdayActivity.class));
                        }

                        else if (position == 5) {
                            startActivity(new Intent(MainActivity.this, FridayActivity.class));
                        }

                        else {
                            startActivity(new Intent(MainActivity.this, SaturdayActivity.class));
                        }


                    }
                });

                convertView.setTag(viewHolder);
            }
            else{
                if_view_holder_is_not_null = (ViewHolder)convertView.getTag();
                //if_view_holder_is_not_null.viewHolderTextView.setText(""+position);
            }
            return convertView;
        }
    }

    public class ViewHolder{
        TextView viewHolderTextView;
        Button viewHolderButton;
    }

    public class HandlerAlertDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int i) {
            if (i == -1) {
                clearWeekMain("Sunday");
                clearWeekMain("Monday");
                clearWeekMain("Tuesday");
                clearWeekMain("Wednesday");
                clearWeekMain("Thursday");
                clearWeekMain("Friday");
                clearWeekMain("Saturday");
                Toast.makeText(MainActivity.this, "Starting a new week", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void clearWeekMain(String day) {

        Query q = myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("EVERY WEEK NOTES").orderByValue();

        myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("NOTES").removeValue();

        myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                .child("days").child(day).child("DONE NOTES").removeValue();

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    NoteItem noteItem = dst.getValue(NoteItem.class);
                    noteItem.everyWeek = true;
                    myRef.child("my app users").child(LoginActivity.nowUsingThisPhone.email)
                            .child("days").child(day).child("NOTES").child(noteItem.note_time).setValue(noteItem);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    //color #7E878A arrow gray background
    //color #BDC6CA one line lite gray background

}

