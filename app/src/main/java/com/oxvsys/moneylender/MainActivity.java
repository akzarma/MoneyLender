package com.oxvsys.moneylender;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,FragmentKYC.OnFragmentInteractionListener {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database.setPersistenceEnabled(true);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (
                DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("KYC");
            FragmentKYC fragmentKYC = new FragmentKYC();
            fragmentTransaction.replace(R.id.fragment_container, fragmentKYC).addToBackStack(null).
                    commit();
        } else if (id == R.id.nav_gallery) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Daily Collect Loan");
            FragmentCollectDaily fragmentCollectDaily = new FragmentCollectDaily();
            fragmentTransaction.replace(R.id.fragment_container, fragmentCollectDaily).addToBackStack(null).
                    commit();

        } else if (id == R.id.nav_slideshow) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Monthly Collect Loan");
            FragmentCollectMonthly fragmentCollectMonthly = new FragmentCollectMonthly();
            fragmentTransaction.replace(R.id.fragment_container, fragmentCollectMonthly).addToBackStack(null).
                    commit();
        } else if (id == R.id.nav_monthly_loan_grant) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Monthly Loan Grant");
            FragmentMonthlyLoan fragmentCollectMonthly = new FragmentMonthlyLoan();
            fragmentTransaction.replace(R.id.fragment_container, fragmentCollectMonthly).addToBackStack(null).
                    commit();
        } else if (id == R.id.nav_dashboard) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Dashboard");
            FragmentDashboard fragmentDashboard = new FragmentDashboard();
            fragmentTransaction.replace(R.id.fragment_container, fragmentDashboard).addToBackStack(null).
                    commit();
        } else if(id == R.id.nav_send){
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Daily Info");
            FragmentDailyInfo fragmentDailyInfo = new FragmentDailyInfo();
            fragmentTransaction.replace(R.id.fragment_container, fragmentDailyInfo).addToBackStack(null).
                    commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
