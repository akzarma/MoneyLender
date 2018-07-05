package com.oxvsys.moneylender;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.getData;
import static com.oxvsys.moneylender.MainActivity.saveData;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";


    boolean isInvalid, userDoesNotExist, networkError = false;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.login_progress);

        Log.d(TAG, "onCreate: " + "login started");

        if (getData("user_id", getApplicationContext()).equals("ERROR")) {


            final EditText mobile_view = findViewById(R.id.login_mobile);
            final EditText password_view = findViewById(R.id.login_password);
            Button login_button = findViewById(R.id.login_button);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    DatabaseReference ref = database.getReference("users");
                    Log.d(TAG, "onClick: " + "in on click");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: " + dataSnapshot);
                            if (dataSnapshot.hasChild(mobile_view.getText().toString())) {
                                for (DataSnapshot user : dataSnapshot.getChildren()) {
                                    String pwd = ((HashMap<String, Object>) user.getValue()).get("pwd").toString();
                                    String type = ((HashMap<String, Object>) user.getValue()).get("type").toString();
                                    if (mobile_view.getText().toString().equals(user.getKey())) {
                                        if (password_view.getText().toString().equals(pwd)) {
                                            Log.d(TAG, "onDataChange: " + "success");
                                            saveData("user_id", user.getKey(), getApplicationContext());
                                            saveData("user_type", type, getApplicationContext());
                                            Log.d(TAG, "onDataChange: " + pwd + type);
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;

                                        } else {
                                            Log.d(TAG, "onDataChange: " + "failure");
                                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
//                            Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
                                progressBar.setVisibility(View.INVISIBLE);

                            } else {
                                Log.d(TAG, "onDataChange: " + "user does not exist");
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "User does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Network Error.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });


//                if (finishedAdmins && finishedAgents) {
//                    if (isInvalid) {
//                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
//                    } else if (userDoesNotExist) {
//                        Toast.makeText(getApplicationContext(), "User does not exist.", Toast.LENGTH_SHORT).show();
//                    } else if (networkError) {
//                        Toast.makeText(getApplicationContext(), "Network Error.", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), "There seems to be a problem logging in. Try again.", Toast.LENGTH_LONG).show();
//                    }
//
//                    isInvalid = false;
//                    userDoesNotExist = false;
//                    networkError = false;
//                    finishedAdmins = false;
//                    finishedAgents = false;
//                }
                }
            });
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
