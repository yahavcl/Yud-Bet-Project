package com.example.yahavproject;

import static com.example.yahavproject.LoginActivity.myPreferences;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationService extends Service {

    NotificationCompat.Builder builder;
    NotificationManager manager;
    final Handler handler = new Handler();
    String currentTime;
    String currentDay;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    Query q;
    NoteItem thisNote;
    ValueEventListener vel;


    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vel != null) {
            q.removeEventListener(vel);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    public void onStop(){

    }


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String email_service = sharedPref.getString(getString(R.string.saved_email), "");

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    handler.postDelayed(this, 40000);
                    currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    currentDay = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date());


                    if (currentDay.equals("Sun"))
                        q = myRef.child("my app users").child(email_service)
                                .child("days").child("Sunday").child("NOTES").orderByValue();

                    else if (currentDay.equals("Mon"))
                        q = myRef.child("my app users").child(email_service)
                                .child("days").child("Monday").child("NOTES").orderByValue();

                    else if (currentDay.equals("Tue"))
                        q = myRef.child("my app users").child(email_service)
                                .child("days").child("Tuesday").child("NOTES").orderByValue();

                    else if (currentDay.equals("Wed"))
                        q = myRef.child("my app users").child(email_service)
                                .child("days").child("Wednesday").child("NOTES").orderByValue();

                    else if (currentDay.equals("Thu"))
                        q = myRef.child("my app users").child(email_service)
                                .child("days").child("Thursday").child("NOTES").orderByValue();

                    else if (currentDay.equals("Fri"))
                        q = myRef.child("my app users").child(email_service)
                                .child("days").child("Friday").child("NOTES").orderByValue();

                    else if (currentDay.equals("Sat"))
                        q = myRef.child("my app users").child(email_service)
                                .child("days").child("Saturday").child("NOTES").orderByValue();


                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                    NoteItem noteItem = dst.getValue(NoteItem.class);
                                    if (noteItem.noteColor != R.color.done_note) {
                                        if ((noteItem.note_time).equals(currentTime)) {
                                            thisNote = noteItem;
                                            notifyRightNow();
                                        }
                                    }
                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    };


                    q.addValueEventListener(valueEventListener);
                    vel = valueEventListener;

                }
            };
            handler.post(runnable);

        return Service.START_STICKY;
    }

    public void notifyRightNow(){

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(thisNote.note_title)
                .setContentText(currentTime)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        int NOTIFICATION_ID = 12345;

        Intent notification_intent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent
                .getActivity(this, 0, notification_intent, PendingIntent.FLAG_UPDATE_CURRENT);


        manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setContentIntent(contentIntent);
        builder.setContentText(currentTime);
        manager.notify(NOTIFICATION_ID, builder.build());

    }


    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}