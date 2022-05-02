package com.example.yahavproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_email;
    EditText et_password;
    TextView tv_email;
    TextView tv_password;
    Button btn_login;
    Button btn_signup;
    CheckBox cb_remember_me;
    static User nowUsingThisPhone;

    static String lastDate;


    static boolean rememberUser = false;

    static String savedUserEmail;


    public static final String user_name = "user_name_key";
    public static final String user_email = "user_email_key";
    public static final String user_password = "user_password_key";
    public static final String myPreferences = "myPref";

    SharedPreferences sharedPreferences;

    ProgressDialog p;

    //String dayNow;

    //Intent my_notification_service;

    FirebaseDatabase database;
    DatabaseReference myRef;

    MyBroadcastReceiver_NetworkConnectivity receiver_networkConnectivity = new MyBroadcastReceiver_NetworkConnectivity();
    boolean isRegister = false;
    static boolean disconnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_password = (TextView) findViewById(R.id.tv_password);
        btn_login = (Button) findViewById(R.id.btn_log_in);
        btn_signup = (Button) findViewById(R.id.btn_sign_up);
        cb_remember_me = (CheckBox) findViewById(R.id.cb_remember_me);

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);



        //dayNow = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date());

/*        Context context = getApplicationContext();                                           //here
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.saved_email), Context.MODE_PRIVATE);*/

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        sharedPreferences = getSharedPreferences(myPreferences, MODE_PRIVATE);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        String new_email = sharedPreferences.getString(user_email, et_email.getText().toString());


        if (checkbox.equals("true")) {

            p = ProgressDialog.show(LoginActivity.this, "wait", "connected", true);
            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            SharedPreferences.Editor editorNew = sharedPref.edit();

            Query query = myRef.child("my app users").orderByValue();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dst : dataSnapshot.getChildren()) {
                            User user = dst.getValue(User.class);
                            if (user.email.equals(new_email)) {
                                nowUsingThisPhone = user;
                                rememberUser = true;
                                editorNew.putString(getString(R.string.saved_email), user.email);
                                //editorNew.putString(getString(R.string.remember_user), "true");
                                editorNew.apply();

                                p.show();
                                Toast.makeText(LoginActivity.this, "Welcome back " + nowUsingThisPhone.userName,
                                        Toast.LENGTH_LONG).show();


                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        else if (checkbox.equals("false")) {
/*            SharedPreferences.Editor editorNew = sharedPref.edit();
            editorNew.putString(getString(R.string.remember_user), "false");
            editorNew.apply();*/


        }


        cb_remember_me.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    SharedPreferences.Editor editor1 = preferences.edit();

                    Query query = myRef.child("my app users").orderByValue();
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                User user = dst.getValue(User.class);
                                if (user.email.equals(et_email.getText().toString())) {
                                    nowUsingThisPhone = user;
                                    editor1.putString("remember", "true");
                                    editor1.apply();
                                    editor.putString(user_email, et_email.getText().toString());
                                    editor.putString(user_password, et_password.getText().toString());
                                    editor.putString(user_name, user.userName);
                                    editor.apply();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(LoginActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                }
                else if (!compoundButton.isChecked()) {
                    SharedPreferences.Editor editor1 = preferences.edit();
                    editor1.putString("remember", "false");
                    editor1.apply();
                    Toast.makeText(LoginActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onClick(View view) {

        receiver_networkConnectivity.onReceive(this, this.getIntent());

        if (view == btn_signup) {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class).
                    putExtra("keyEmail", et_email.getText().toString()).
                    putExtra("keyPassword", et_password.getText().toString()));
        }

        else {

            if (et_email.getText().toString().trim().length() == 0 || et_password.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Enter an email and password", Toast.LENGTH_LONG).show();
            }
            else {

                String new_user_email = et_email.getText().toString();
                String new_user_password = et_password.getText().toString();
                Query q = myRef.child("my app users").orderByValue();
                q.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        boolean userAlreadyExist = false;
                        for (DataSnapshot dst : datasnapshot.getChildren()) {
                            User u = dst.getValue(User.class);
                            if (u.email.equals(new_user_email) && u.password.equals(new_user_password)) {
                                nowUsingThisPhone = u;
                                Toast.makeText(LoginActivity.this, "Welcome back " + u.userName, Toast.LENGTH_LONG).show();
                                userAlreadyExist = true;

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            }
                        }



                        if (userAlreadyExist == false) {
                            Toast.makeText(LoginActivity.this, "User doesn't exist, please sign up!",
                                    Toast.LENGTH_LONG).show();

                            startActivity(new Intent(LoginActivity.this, SignupActivity.class).
                                    putExtra("keyEmail", et_email.getText().toString()).
                                    putExtra("keyPassword", et_password.getText().toString()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

    }


/*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver_networkConnectivity, filter);
        if (receiver_networkConnectivity != null)
            isRegister = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver_networkConnectivity != null)
            unregisterReceiver(receiver_networkConnectivity);
        if(isRegister == true)
            unregisterReceiver(receiver_networkConnectivity);

    }*/

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