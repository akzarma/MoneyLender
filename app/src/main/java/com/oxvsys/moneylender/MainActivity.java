package com.oxvsys.moneylender;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentKYC.OnFragmentInteractionListener {

    Toolbar toolbar;
    static String logged_agent;
    static String logged_type;
    static String user_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        logged_agent = getData("user_id", getApplicationContext());
        logged_type = getData("user_type", getApplicationContext());
        user_pwd = getData("user_pwd", getApplicationContext());


        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_menu = navigationView.getMenu();


        if (logged_type.equals("agent")) {
            nav_menu.findItem(R.id.nav_monthly_loan_grant).setVisible(false);
            nav_menu.findItem(R.id.nav_kyc).setVisible(false);
            nav_menu.findItem(R.id.nav_agent_register).setVisible(false);
            nav_menu.findItem(R.id.nav_send).setVisible(false);
            if (user_pwd.equals("1234")) {
                nav_menu.findItem(R.id.nav_gallery).setVisible(false);
//                nav_menu.findItem(R.id.toolbar).setVisible(false);
            }

        }


        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.nav_header_username);
        TextView info_user = hView.findViewById(R.id.nav_header_info);

        nav_user.setText(getData("user_id", getApplicationContext()));
        info_user.setText("");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setupFirstFragment();

        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CALL_PHONE},
                        1);

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(MainActivity.this, "Permission denied to Call", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void setupFirstFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Calendar calendar = Calendar.getInstance();


        if (user_pwd.equals("1234")) {

            SetPassword setPassword = new SetPassword();
            fragmentTransaction.replace(R.id.fragment_container, setPassword).
                    addToBackStack(null)
                    .commit();
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Dashboard");
//        String logged_in = getData("user_type", getApplicationContext());
        if (logged_type.equals("admin")) {
            FragmentDashboard fragmentDashboard = FragmentDashboard.newInstance(calendar);
            fragmentTransaction.replace(R.id.fragment_container, fragmentDashboard).
                    addToBackStack(null).
                    commit();
        } else {
            FragmentDashboardSpecific fragmentDashboardSpecific =
                    FragmentDashboardSpecific.newInstance(calendar);
            fragmentTransaction.replace(R.id.fragment_container,
                    fragmentDashboardSpecific).addToBackStack(null).
                    commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Calendar calendar = Calendar.getInstance();
            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    setupFirstFragment();
                    return true;
                case R.id.bottom_nav_daily_basis:
//                    toolbar.setTitle("Daily Basis Accounts");
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle("Daily Basis Payment");
                    if (getData("user_type", getApplicationContext()).equals("admin")) {
                        FragmentAccountTypeInfo fcdi = FragmentAccountTypeInfo.newInstance(calendar, "0");
                        fragmentTransaction.replace(R.id.fragment_container, fcdi).addToBackStack(null).
                                commit();
                        return true;
                    } else {
                        FragmentAccountTypeInfo fragmentAccountTypeInfo = FragmentAccountTypeInfo.newInstance(calendar, "0");
                        fragmentTransaction.replace(R.id.fragment_container, fragmentAccountTypeInfo).addToBackStack(null).
                                commit();
                        return true;
                    }


                case R.id.bottom_nav_monthly_basis:
                    toolbar.setTitle("Monthly Basis Payment");
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle("Monthly Basis Payment");
                    FragmentAccountTypeInfo fragmentAccountTypeInfo = FragmentAccountTypeInfo.newInstance(calendar, "1");
                    fragmentTransaction.replace(R.id.fragment_container, fragmentAccountTypeInfo).addToBackStack(null).
                            commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        // Toolbar top-right options
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_kyc) {
            // Handle the camera action
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("KYC");
            FragmentKYC fragmentKYC = new FragmentKYC();
            fragmentTransaction.replace(R.id.fragment_container, fragmentKYC).addToBackStack(null).
                    commit();
        } else if (id == R.id.nav_gallery) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Collect Money");
            FragmentSelectAccount fragmentSelectAccount = new FragmentSelectAccount();
            fragmentTransaction.replace(R.id.fragment_container, fragmentSelectAccount).addToBackStack(null).
                    commit();

        } else if (id == R.id.nav_monthly_loan_grant) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Grant Loan");
            FragmentSelectCustomer fragmentSelectCustomer = new FragmentSelectCustomer();
            fragmentTransaction.replace(R.id.fragment_container, fragmentSelectCustomer).addToBackStack(null).
                    commit();
        } else if (id == R.id.nav_send) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Agents Collection");
            Calendar calendar = Calendar.getInstance();
            FragmentDailyInfo fragmentDailyInfo = FragmentDailyInfo.newInstance(calendar);
            fragmentTransaction.replace(R.id.fragment_container, fragmentDailyInfo).addToBackStack(null).
                    commit();
        } else if (id == R.id.nav_agent_register) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Register Agent");
            FragmentAgentRegister far = new FragmentAgentRegister();
            fragmentTransaction.replace(R.id.fragment_container, far).addToBackStack(null).
                    commit();
        } else if (id == R.id.nav_show_customer) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Customers");
            FragmentShowCustomer far = new FragmentShowCustomer();
            fragmentTransaction.replace(R.id.fragment_container, far).addToBackStack(null).
                    commit();
        }else if (id == R.id.nav_acc_agent) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Account Agent");
            FragmentAccountAgent far = new FragmentAccountAgent();
            fragmentTransaction.replace(R.id.fragment_container, far).addToBackStack(null).
                    commit();
        }
//        else if (id == R.id.nav_customer_daily) {
//            if (getSupportActionBar() != null)
//                getSupportActionBar().setTitle("Customer Report");
//            Calendar calendar = Calendar.getInstance();
//            FragmentCustomerDailyInfo far = FragmentCustomerDailyInfo.newInstance(calendar);
//            fragmentTransaction.replace(R.id.fragment_container, far).addToBackStack(null).
//                    commit();
//        }
        else if (id == R.id.nav_logout) {
            SharedPreferences preferences = PreferenceManager.
                    getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putString("logged_out", "true");
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.change_password) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Change Password");
            SetPassword far = new SetPassword();
            fragmentTransaction.replace(R.id.fragment_container, far).addToBackStack(null).
                    commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static String CaltoStringDate(Calendar cal) {
        return cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR);
    }

    public static Calendar StringDateToCal(String date) {
        String[] date1 = date.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date1[2]), Integer.parseInt(date1[1]) - 1, Integer.parseInt(date1[0]));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        CaltoStringDate(cal);
        return cal;
    }

    public static void saveData(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getData(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "ERROR");
    }

}
