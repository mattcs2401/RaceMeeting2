package com.mcssoft.racemeetings2.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mcssoft.racemeetings2.R;
import com.mcssoft.racemeetings2.database.DatabaseOperations;
import com.mcssoft.racemeetings2.database.SchemaConstants;
import com.mcssoft.racemeetings2.dialog.DeleteDialog;
import com.mcssoft.racemeetings2.dialog.NetworkDialog;
import com.mcssoft.racemeetings2.fragment.DateSelectFragment;
import com.mcssoft.racemeetings2.fragment.MeetingsFragment;
import com.mcssoft.racemeetings2.interfaces.IDeleteDialog;
import com.mcssoft.racemeetings2.interfaces.IDateSelect;
import com.mcssoft.racemeetings2.network.DownloadRequest;
import com.mcssoft.racemeetings2.network.DownloadRequestQueue;
import com.mcssoft.racemeetings2.network.NetworkReceiver;
import com.mcssoft.racemeetings2.utility.DateTime;
import com.mcssoft.racemeetings2.utility.Preferences;
import com.mcssoft.racemeetings2.utility.Resources;
import com.mcssoft.racemeetings2.utility.Url;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements IDateSelect,
                   IDeleteDialog,
                   Response.Listener,
                   Response.ErrorListener,
                   NavigationView.OnNavigationItemSelectedListener {

    //<editor-fold defaultstate="collapsed" desc="Region: Interface">
    /**
     * Results of the DateSelectFragment returns here.
     * @param dateVals [0] YYYY, [1] M(M), [2] D(D)
     */
    @Override
    public void iDateValues(String[] dateVals) {
        Bundle bundle = new Bundle();
        String date = dateVals[0] + "-" + dateVals[1] + "-" + dateVals[2];
        bundle.putString("meetings_show_day_key", date);
        this.bundle = bundle;
        getMeetingsOnDay(dateVals);
    }

    /**
     * Result of the delete dialog return here.
     * @param whichDelete The type of delete action
     */
    @Override
    public void iDeleteDialog(int whichDelete) {
        if(whichDelete == Resources.getInstance().getInteger(R.integer.rb_delete_all)) {
            // 'Delete all' option selected.
            Snackbar.make(findViewById(R.id.id_content_main), Resources.getInstance()
                    .getString(R.string.all_meetings_removed), Snackbar.LENGTH_SHORT).show();
            loadMeetingsFragment(setEmptyView());
        } else if(whichDelete == Resources.getInstance().getInteger(R.integer.rb_delete_prev)) {
            // TBA - 
        }
    }

    /**
     * The Volley download will return here.
     * @param response The response object (list of Meetings).
     */
    @Override
    public void onResponse(Object response) {
        doProgressDialog(false);
        if(((ArrayList) response).size() == 0) {
            this.bundle.clear();
            this.bundle.putString("meetings_show_empty_key", null);
        }
        loadMeetingsFragment(this.bundle);
    }

    /**
     * A Volley error will return here.
     * @param error The Volley error.
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        NetworkResponse networkResponse = error.networkResponse;
        if(networkResponse == null) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || (!networkInfo.isAvailable() && !networkInfo.isConnected())) {
                showNetworkDialog();
            }
        } else {
            // Some sort of network error, e.g. 404 page not found etc.
//            Map<String,String> headers = networkResponse.headers;
            this.bundle.clear();
            this.bundle.putString("meetings_show_empty_key", null);
            loadMeetingsFragment(this.bundle);
            // TODO - some generic error dialog ?
        }
    }
    //</editor-fold>//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBaseUI();    // set screen elements.
    }

    @Override
    protected void onStart() {
        super.onStart();

        initialise();
        Bundle bundle = new Bundle();

        if(Preferences.getInstance().getMeetingsShowToday()) {
            // Preference to show today's meetings is set.
            DateTime dt = new DateTime();
            String today = dt.getCurrentDateYearFirst();
            meetingsExist = dbOper.checkMeetingDate(today);

            if(meetingsExist) {
                // Meetings for day exist in the database.
                bundle.putString("meetings_show_day_key", today);
                loadMeetingsFragment(bundle);
            } else {
                // Meetings for day will need to be downloaded.
                bundle.putString("meetings_show_day_key", today);
                this.bundle = bundle;
                getMeetingsOnDay(today.split("-"));
            }
        } else { //if (!Preferences.getInstance().getMeetingsShowToday()) {
            // Preference to show today's meetings is not set (show all if exists).
            meetingsExist = dbOper.checkTableRowCount(SchemaConstants.MEETINGS_TABLE);
            if(meetingsExist) {
                bundle.putString("meetings_show_all_key", null);
                loadMeetingsFragment(bundle);
            } else {
                loadMeetingsFragment(setEmptyView());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finalise();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Navigation">
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
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

        if(id == R.id.id_nav_menu_show_all) {
            Bundle bundle = new Bundle();
            bundle.putString("meetings_show_all_key", null);
            loadMeetingsFragment(bundle);
        }
        else if (id == R.id.id_nav_menu_races_today) {
            // Get today's date and set bundle args.
            DateTime dt = new DateTime();
            String today = dt.getCurrentDateYearFirst();
            Bundle bundle = new Bundle();
            bundle.putString("meetings_show_day_key", today);

            // Check if Meetings already exist for today.
            DatabaseOperations dbOper = new DatabaseOperations(this);
            if(dbOper.checkMeetingDate(today)) {
                loadMeetingsFragment(bundle);
            } else {
                // download today's Meetings.
                this.bundle = bundle;
                getMeetingsOnDay(today.split("-"));
            }

        } else if (id == R.id.id_nav_menu_races_select) {
            DialogFragment dateSelectFragment = new DateSelectFragment();
            dateSelectFragment.show(getFragmentManager(),
                    Resources.getInstance().getString(R.string.date_select_fragment_tag));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    public Toolbar getToolbar() {
        return this.toolbar;
    }

    private void loadMeetingsFragment(@Nullable Bundle args) {
        String fragment_tag = Resources.getInstance().getString(R.string.meetings_fragment_tag);
        MeetingsFragment meetingsFragment = new MeetingsFragment();
        if(args != null) {
            meetingsFragment.setArguments(args);
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.id_content_main, meetingsFragment, fragment_tag)
                .addToBackStack(fragment_tag)
                .commit();
    }

    private void getMeetingsOnDay(@Nullable String[] date) {
        String uri = null;
        Url url = new Url();
        if(date == null) {
            uri = url.createRaceDayUrl(null);
        } else {
            uri = url.createRaceDayUrl(date);
        }
        doProgressDialog(true);
        DownloadRequest dlReq = new DownloadRequest(Request.Method.GET, uri, this, this, this, SchemaConstants.MEETINGS_TABLE);
        DownloadRequestQueue.getInstance().addToRequestQueue(dlReq);
    }

    private void setBaseUI() {
        setContentView(R.layout.content_view_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar_base_toolbar);
        //toolbar.setSubtitle("Today's Meetings"); // testing
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setCustomView(R.layout.actionbar_view);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.id_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showDeleteDialog() {
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.setShowsDialog(true);
        deleteDialog.show(getSupportFragmentManager(),
                Resources.getInstance().getString(R.string.delete_dialog_fragment_tag));
    }

    private void showNetworkDialog() {
        NetworkDialog nd = new NetworkDialog();
        nd.setShowsDialog(true);
        Bundle bundle = new Bundle();
        bundle.putString(Resources.getInstance().getString(R.string.network_dialog_text),
                Resources.getInstance().getString(R.string.network_connection_error));
        nd.setArguments(bundle);
        nd.show(getSupportFragmentManager(), null);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

    private void unRegisterReceiver() {
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    private Bundle setEmptyView() {
        Bundle args = new Bundle();
        args.putString("meetings_show_empty_key", null);
        return args;
    }

    private void doProgressDialog(boolean doProgress) {
        if(doProgress) {
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Getting Meetings information.");
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    private void initialise() {
        registerReceiver();            // register network broadcast receiver.
        DownloadRequestQueue.getInstance(this);
        Preferences.getInstance(this); // setup preferenxes access.
        Resources.getInstance(this);   // setup resources access.
        dbOper = new DatabaseOperations(this);
    }

    private void finalise() {

        // Check cache preference.
        if(!Preferences.getInstance().getSaveMeetings()) {
            dbOper.deleteAllFromTable(SchemaConstants.RUNNERS_TABLE);
            dbOper.deleteAllFromTable(SchemaConstants.RACES_TABLE);
            dbOper.deleteAllFromTable(SchemaConstants.MEETINGS_TABLE);
        }

        // De-register the network state broadcast receiver.
        unRegisterReceiver();

        // Close off static references.
        Preferences.getInstance().destroy();
        Resources.getInstance().destroy();
        DownloadRequestQueue.getInstance().destroy();

        // Basically just ensure database is closed.
        if(dbOper.getDbHelper() != null) {
            dbOper.getDbHelper().onDestroy();
        }
        dbOper = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private Bundle bundle;                  // contains meeting action key, used by fragment.
    private Toolbar toolbar;                // to get access to the toolbar.
    private boolean meetingsExist;          // flag meetings exist in database.
    private NetworkReceiver receiver;       // for network availability check.
    private DatabaseOperations dbOper;      // database related methods.
    private ProgressDialog progressDialog;  // used by Volley download to show something happening.
    //</editor-fold>
}
