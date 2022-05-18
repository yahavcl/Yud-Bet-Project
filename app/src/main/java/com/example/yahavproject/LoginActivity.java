package com.example.yahavproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    TextInputLayout til_email;
    TextInputLayout til_password;
    TextView tv_login;
    Button btn_login;
    Button btn_signup;
    CheckBox cb_remember_me;
    TextView tv_dont_have_an_account;
    static User nowUsingThisPhone;


    static boolean rememberUser = false;


    public static final String user_name = "user_name_key";
    public static final String user_email = "user_email_key";
    public static final String user_password = "user_password_key";
    public static final String myPreferences = "myPref";

    SharedPreferences sharedPreferences;

    ProgressDialog progressDialog;

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


        //et_email = (EditText) findViewById(R.id.et_email);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        tv_login = (TextView) findViewById(R.id.tv_login);
        //tv_password = (TextView) findViewById(R.id.tv_password);
        btn_login = (Button) findViewById(R.id.btn_log_in);
        btn_signup = (Button) findViewById(R.id.btn_sign_up);
        cb_remember_me = (CheckBox) findViewById(R.id.cb_remember_me);
        tv_dont_have_an_account = (TextView) findViewById(R.id.tv_dont_have_an_account);


        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        sharedPreferences = getSharedPreferences(myPreferences, MODE_PRIVATE);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        String new_email = sharedPreferences.getString(user_email, til_email.getEditText().getText().toString());


        if (checkbox.equals("true")) {

            progressDialog = ProgressDialog.show(LoginActivity.this, "wait", "connected", true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

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
                                editorNew.apply();

                                progressDialog.show();
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
                                if (user.email.equals(til_email.getEditText().getText().toString())) {
                                    nowUsingThisPhone = user;
                                    editor1.putString("remember", "true");
                                    editor1.apply();
                                    editor.putString(user_email, til_email.getEditText().getText().toString());
                                    editor.putString(user_password, til_password.getEditText().getText().toString());
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
                    putExtra("keyEmail", til_email.getEditText().getText().toString()).
                    putExtra("keyPassword", til_password.getEditText().getText().toString()));
        }

        else {

            if (til_email.getEditText().getText().toString().trim().length() == 0 || til_password.getEditText().getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Enter an email and password", Toast.LENGTH_LONG).show();
            }
            else {

                String new_user_email = til_email.getEditText().getText().toString();
                String new_user_password = til_password.getEditText().getText().toString();
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
                                    putExtra("keyEmail", til_email.getEditText().getText().toString()).
                                    putExtra("keyPassword", til_password.getEditText().getText().toString()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
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