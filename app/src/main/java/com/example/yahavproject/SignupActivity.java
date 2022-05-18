package com.example.yahavproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_signup;
    TextInputLayout til_user_name;
    TextInputLayout til_email2;
    TextInputLayout til_password2;
    CheckBox cb_remember_me2;
    Button btn_sign_up2;
    Button btn_back;

    FirebaseDatabase database;
    DatabaseReference myRef;

    //static User nowUsingThisPhoneFirstTime;

    SharedPreferences sharedPreferences;



    //MyBroadcastReceiver_NetworkConnectivity receiver_networkConnectivity = new MyBroadcastReceiver_NetworkConnectivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        tv_signup = (TextView)findViewById(R.id.tv_signup);
        til_user_name = (TextInputLayout)findViewById(R.id.til_user_name);
        til_email2 = (TextInputLayout) findViewById(R.id.til_email2);
        til_password2 = (TextInputLayout) findViewById(R.id.til_password2);
        cb_remember_me2 = (CheckBox)findViewById(R.id.cb_remember_me2);
        btn_sign_up2 = (Button)findViewById(R.id.btn_sign_up2);
        btn_back = (Button)findViewById(R.id.btn_back);

        btn_sign_up2.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        til_email2.getEditText().setText(getIntent().getStringExtra("keyEmail"));
        til_password2.getEditText().setText(getIntent().getStringExtra("keyPassword"));

        sharedPreferences = getSharedPreferences(LoginActivity.myPreferences, MODE_PRIVATE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);


        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        String new_email = sharedPreferences.getString(LoginActivity.user_email, til_email2.getEditText().getText().toString());

        if (checkbox.equals("true")) {

            SharedPreferences.Editor editorNew = sharedPref.edit();

            Query query = myRef.child("my app users").orderByValue();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dst : dataSnapshot.getChildren()) {
                        User user = dst.getValue(User.class);
                        if (user.email.equals(new_email)) {
                            LoginActivity.nowUsingThisPhone = user;
                            LoginActivity.rememberUser = true;
                            editorNew.putString(getString(R.string.saved_email), user.email);
                            editorNew.apply();
                            Toast.makeText(SignupActivity.this,
                                    "Welcome back " + LoginActivity.nowUsingThisPhone.userName, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignupActivity.this , MainActivity.class));

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else if (checkbox.equals("false")) {
            //Toast.makeText(this, "Please Sign Up", Toast.LENGTH_SHORT).show();
        }

        cb_remember_me2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences.Editor editor1 = preferences.edit();
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Query query = myRef.child("my app users").orderByValue();
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                User user = dst.getValue(User.class);
                                if (!(user.email.equals(til_email2.getEditText().getText().toString()))) {
                                    LoginActivity.nowUsingThisPhone = user;
                                    editor1.putString("remember", "true");
                                    editor1.apply();
                                    editor.putString(LoginActivity.user_email, til_email2.getEditText().getText().toString());
                                    editor.putString(LoginActivity.user_password, til_password2.getEditText().getText().toString());
                                    editor.putString(LoginActivity.user_name, user.userName);
                                    editor.apply();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(SignupActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                }
                else if (!compoundButton.isChecked()) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(SignupActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onClick(View view) {

       // receiver_networkConnectivity.onReceive(this, this.getIntent());

        if (view == btn_back)
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));

        else {
            if (til_email2.getEditText().getText().toString().trim().length() == 0 || til_password2.getEditText().getText().toString().trim().length() == 0 || til_user_name.getEditText().getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Enter an email, username and password", Toast.LENGTH_LONG).show();
            }
            else {
                String new_user_name = til_user_name.getEditText().getText().toString();
                String new_user_email = til_email2.getEditText().getText().toString();
                String new_user_password = til_password2.getEditText().getText().toString();
                Query q = myRef.child("my app users").orderByValue();
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        boolean userAlreadyExist2 = false;
                        for (DataSnapshot dst : datasnapshot.getChildren()) {
                            User u2 = dst.getValue(User.class);

                            if (u2.email.equals(new_user_email)) {
                                Toast.makeText(SignupActivity.this, "User already Exist, choose another email", Toast.LENGTH_LONG).show();
                                userAlreadyExist2 = true;
                            }
                        }
                        if (userAlreadyExist2 == false) {
                            User u = new User (new_user_name, new_user_email, new_user_password);
                            LoginActivity.nowUsingThisPhone = u;
                            myRef.child("my app users").child(new_user_email).setValue(u);
                            //myRef = database.getReference().child("my app users").child(new_user_email).child("days");

                            Toast.makeText(SignupActivity.this, "User successfully registered", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }

    }

}