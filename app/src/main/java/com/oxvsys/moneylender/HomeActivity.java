package com.oxvsys.moneylender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity{
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database.setPersistenceEnabled(true);

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
//        if (findViewById(R.id.fragment_container) != null) {
//            if (savedInstanceState != null) {
//                return;
//            }
////            BlankFragment1 blankFragment1 = new BlankFragment1();
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, blankFragment1).commit();
//        }
    }

}