package com.example.calorietracker12;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mapquest.mapping.MapQuest;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView)
                findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Home");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new Mainfragment()).commit();

    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment nextFragment = null;
        switch (id) {
            case R.id.nav_homepage:
                nextFragment = new Mainfragment();
                getSupportActionBar().setTitle("Home");
                break;
            case R.id.nav_diet:
                nextFragment = new DailyDiet();
                getSupportActionBar().setTitle("Daily Diet");
                break;
            case R.id.nav_steps:
                nextFragment = new Step();
                getSupportActionBar().setTitle("Step");
                break;
            case R.id.nav_tracker:
                nextFragment = new CalorieTracker();
                getSupportActionBar().setTitle("Calorie Tracker");
                break;
            case R.id.nav_report:
                nextFragment = new Report();
                getSupportActionBar().setTitle("Report");
                break;
            case R.id.nav_map:
                nextFragment = new Map();
                getSupportActionBar().setTitle("Map");
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                nextFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
