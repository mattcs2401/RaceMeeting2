package com.mcssoft.racemeetings2.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
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
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.dialog.DeleteDialog;
import com.mcssoft.racemeetings2.dialog.NetworkDialog;
import com.mcssoft.racemeetings2.fragment.DateSelectFragment;
import com.mcssoft.racemeetings2.fragment.MeetingsFragment;
import com.mcssoft.racemeetings2.interfaces.IDeleteDialog;
import com.mcssoft.racemeetings2.interfaces.IDownloadResult;
import com.mcssoft.racemeetings2.interfaces.IDateSelect;
import com.mcssoft.racemeetings2.interfaces.IParseResult;
import com.mcssoft.racemeetings2.interfaces.IWriteResult;
import com.mcssoft.racemeetings2.network.DownloadData;
import com.mcssoft.racemeetings2.utility.ParseResult;
import com.mcssoft.racemeetings2.utility.Preferences;
import com.mcssoft.racemeetings2.utility.RaceDate;
import com.mcssoft.racemeetings2.utility.Resources;
import com.mcssoft.racemeetings2.utility.WriteResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   IDownloadResult,
                   IDateSelect,
                   IParseResult,
                   IWriteResult,
                   IDeleteDialog {

    //<editor-fold defaultstate="collapsed" desc="Region: Interface">
    /**
     * Result from async task DownloadData returns here.
     * @param output Indicator of what the results are for.
     * @param results Results of the operation.
     */
    @Override
    public void downloadResult(String output, String results) {
        String message;
        String[] result = results.split(":");
        if((result != null) && (result[0].equals("Exception"))) {
            // TODO - somesort of alert type dialog that has options.
            Toast.makeText(this, "Error: " + result[1], Toast.LENGTH_LONG).show();
        } else {
            message = Resources.getInstance().getString(R.string.raceday_meetinginfo_dowload_msg);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            message = Resources.getInstance().getString(R.string.raceday_meeting_process_msg);
            ParseResult parseResult = new ParseResult(this, message, results, output);
            parseResult.iResult = this;
            parseResult.execute();
        }
    }

    /**
     * Results of the DateSelectFragment returns here.
     * @param values [0] YYYY, [1] M(M), [2] D(D)
     */
    @Override
    public void iDateValues(String[] values) {
        getMeetingsOnDay(values);
    }

    /**
     * Results of ParseResult return here.
     * @param output     What the operation is associated with..
     * @param resultList A list of parsed objects (from xml feed).
     */
    @Override
    public void parseResult(String output, List resultList) {
        WriteResult wr = null;
        switch(output) {
            case SchemaConstants.MEETINGS_TABLE:
                wr = new WriteResult(this, "write result message meetings",
                        resultList, SchemaConstants.MEETINGS_TABLE);
                break;
            case SchemaConstants.RACES_TABLE:
                wr = new WriteResult(this, "write result message races",
                        resultList, SchemaConstants.RACES_TABLE);
                break;
        }
        wr.writeResult = this;
        wr.execute();
    }

    @Override
    public void writeResult(String output, boolean result) {
        if(result) {
            loadMeetingsFragment();
        }
    }

    /**
     * Result of the delete dialog return here.
     * @param whichDelete The type of delete action
     */
    @Override
    public void iDeleteDialog(int whichDelete) {
        String bp = "";
        if(whichDelete == Resources.getInstance().getInteger(R.integer.rb_delete_all)) {
            // TBA - if meetings list showing then clear.
        } else if(whichDelete == Resources.getInstance().getInteger(R.integer.rb_delete_prev)) {
            // TBA - 
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.getInstance(this); // setup preferenxes access.
        Resources.getInstance(this);   // setup resources access.

        setBaseUI();    // set screen elements.
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!checkForNetwork()) {
            NetworkDialog nd = new NetworkDialog();
            nd.setShowsDialog(true);
            Bundle bundle = new Bundle();
            bundle.putString(Resources.getInstance().getString(R.string.network_dialog_text),
                    Resources.getInstance().getString(R.string.network_connection_error));
            nd.setArguments(bundle);
            nd.show(getSupportFragmentManager(), null);
        } else {
            // TODO - check prefs to load today's meeting information (else display message ?).
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences.getInstance().destroy();
        Resources.getInstance().destroy();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Navigation">
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
        switch (item.getItemId()) {
            case R.id.id_menu_preferences:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.id_menu_delete:
                showDeleteDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.id_nav_menu_races_today) {
            getMeetingsOnDay(null);

        } else if (id == R.id.id_nav_menu_races_select) {
            DialogFragment dateSelectFragment = new DateSelectFragment();
            dateSelectFragment.show(getFragmentManager(),
                    Resources.getInstance().getString(R.string.date_select_fragment_tag));

        } else if (id == R.id.id_nav_menu_3) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void loadMeetingsFragment() {
        String fragment_tag = Resources.getInstance().getString(R.string.meetings_fragment_tag);
        MeetingsFragment meetingsFragment = new MeetingsFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.id_content_main, meetingsFragment, fragment_tag)
                .addToBackStack(fragment_tag)
                .commit();    }

    private void getMeetingsOnDay(@Nullable String[] date) {
        URL url = null;
        String msg = null;
        if(date == null) {
            try {
                msg = Resources.getInstance().getString(R.string.raceday_download_msg) +
                      Resources.getInstance().getString(R.string.raceday_download_msg_warn) ;
                url = new URL(createRaceDayUrl(null));
            } catch (MalformedURLException ex) {
                Log.d("", ex.getMessage());
            }
        } else {
            try {
                msg = Resources.getInstance().getString(R.string.raceday_download_withdate_msg) +
                      " " + (date[2] + "/" + date[1] + "/" + date[0]) +
                      Resources.getInstance().getString(R.string.raceday_download_msg_warn);
                url = new URL(createRaceDayUrl(date));
            } catch (MalformedURLException ex) {
                Log.d("", ex.getMessage());
            }
        }
        DownloadData dld = new DownloadData(this, url, msg, SchemaConstants.MEETINGS_TABLE);
        dld.downloadResult = this;
        dld.execute();

    }

    private void getRacesOnDay(@Nullable String[] date) {
        // TBA
    }

    private String createRaceDayUrl(@Nullable String[] date) {
        if(date == null) {
            RaceDate raceDate = new RaceDate();
            date = raceDate.getDateComponents();
        }
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
               .appendPath(date[0])
               .appendPath(date[1])
               .appendPath(date[2])
               .appendPath(Resources.getInstance().getString(R.string.race_day_listing));
        builder.build();
        return builder.toString();
    }

    private boolean checkForNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }

    private void setBaseUI() {
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

    private void showDeleteDialog() {
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.setShowsDialog(true);
        deleteDialog.show(getSupportFragmentManager(),
                Resources.getInstance().getString(R.string.delete_dialog_fragment_tag));
    }
    //</editor-fold>
}
