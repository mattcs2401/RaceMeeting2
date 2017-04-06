package com.mcssoft.racemeetings2.activity;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.interfaces.IAsyncResult;
import com.mcssoft.racemeetings2.network.DownloadData;
import com.mcssoft.racemeetings2.utility.RaceDate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IAsyncResult {

    @Override
    public void result(String s1, String results) {
        if (!checkForException(results)) {
            Toast.makeText(this, "Results downloaded (" + results.length() + ")", Toast.LENGTH_LONG).show();
            // TODO - process the results in a background task, could potentially take a while.
            InputStream instream = new ByteArrayInputStream(results.getBytes());
            String bp = "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.id_nav_menu_races_today) {
            try {
                URL url = new URL(createRaceDayUrl());
                DownloadData dld = new DownloadData(this, url, "Retrieving today's races. \n This may take a minute or two.", null);
                dld.asyncResult = this;
                dld.execute();
            } catch(MalformedURLException ex) {
                Log.d("", ex.getMessage());
            }

        } else if (id == R.id.id_nav_menu_2) {

        } else if (id == R.id.id_nav_menu_3) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String createRaceDayUrl() {
        // TODO - have download base path and RaceDay.xl as string resources.
        RaceDate raceDate = new RaceDate();
        int[] date = raceDate.getDateComponents();

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath("https://tatts.com/pagedata/racing")
               .appendPath(Integer.toString(date[0]))
               .appendPath(Integer.toString(date[1]))
               .appendPath(Integer.toString(date[2]))
               .appendPath("RaceDay.xml");
        builder.build();
        return builder.toString();
    }

    private boolean checkForException(String results) {
        String[] result = results.split(":");
        if((result != null) && (result[0] == "Exception")) {
            Toast.makeText(this, result[1], Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
