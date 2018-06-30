package com.oxvsys.moneylender;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";
    static FirebaseDatabase database = FirebaseDatabase.getInstance();

    boolean isInvalid, userDoesNotExist, networkError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database.setPersistenceEnabled(true);
        final EditText mobile_view = findViewById(R.id.login_mobile);
        final EditText password_view = findViewById(R.id.login_password);
        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = database.getReference("users").child("agents");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(mobile_view.getText().toString())) {
                            String password = ((HashMap<String, String>) dataSnapshot.getValue()).get(mobile_view.getText().toString());
                            if (password_view.getText().toString().equals(password)) {
                                Log.d(TAG, "onDataChange: " + "success");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Log.d(TAG, "onDataChange: " + "failure");
                                Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "onDataChange: " + "user does not exist");
                            Toast.makeText(getApplicationContext(),"User does not exist.",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Network Error.",Toast.LENGTH_SHORT).show();
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
////                    else {
////                        Toast.makeText(getApplicationContext(), "There seems to be a problem logging in. Try again.", Toast.LENGTH_LONG).show();
////                    }
//
//                    isInvalid = false;
//                    userDoesNotExist = false;
//                    networkError = false;
//                    finishedAdmins = false;
//                    finishedAgents = false;
//                }

            }
        });
    }
}
