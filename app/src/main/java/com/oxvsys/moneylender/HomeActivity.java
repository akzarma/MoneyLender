package com.oxvsys.moneylender;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.oxvsys.moneylender.R;

public class HomeActivity extends AppCompatActivity implements FragmentKYC.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (findViewById(R.id.fragment_container) != null) {
//            if (savedInstanceState != null) {
//                return;
//            }
////            BlankFragment1 blankFragment1 = new BlankFragment1();
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, blankFragment1).commit();
//        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}