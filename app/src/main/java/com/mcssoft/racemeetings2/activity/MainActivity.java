package com.mcssoft.racemeetings2.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
import com.mcssoft.racemeetings2.utility.Preferences;
import com.mcssoft.racemeetings2.utility.Resources;
import com.mcssoft.racemeetings2.utility.Url;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   Response.ErrorListener, Response.Listener,
                   IDateSelect, IDeleteDialog {

    //<editor-fold defaultstate="collapsed" desc="Region: Interface">
    /**
     * Results of the DateSelectFragment returns here.
     * @param values [0] YYYY, [1] M(M), [2] D(D)
     */
    @Override
    public void iDateValues(String[] values) {
        getMeetingsOnDay(values);
    }

    /**
     * Result of the delete dialog return here.
     * @param whichDelete The type of delete action
     */
    @Override
    public void iDeleteDialog(int whichDelete) {
        if(whichDelete == Resources.getInstance().getInteger(R.integer.rb_delete_all)) {
            // 'Delete all' option selected.
            loadMeetingsFragment(setEmptyView());
        } else if(whichDelete == Resources.getInstance().getInteger(R.integer.rb_delete_prev)) {
            // TBA - 
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();            // register network broadcast receiver.
        DownloadRequestQueue.getInstance(this);
        Preferences.getInstance(this); // setup preferenxes access.
        Resources.getInstance(this);   // setup resources access.
        setBaseUI();                   // set screen elements.
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if(!receiver.isConnected()) {
            showNetworkDialog();
        } else {*/
            DatabaseOperations dbOper = new DatabaseOperations(this);
            if(dbOper.checkTableRowCount(SchemaConstants.MEETINGS_TABLE)) {
                loadMeetingsFragment(null);
            } else {
                loadMeetingsFragment(setEmptyView());
            }
            // TODO - check prefs to load today's meeting information (else display message ?).
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // De-register the network state broadcast receiver.
        unRegisterReceiver();

        // Close off static references.
        Preferences.getInstance().destroy();
        Resources.getInstance().destroy();
        DownloadRequestQueue.getInstance().destroy();

        // Basically just ensure database is closed.
        DatabaseOperations dbOper = new DatabaseOperations(this);
        if(dbOper.getDbHelper() != null) {
            dbOper.getDbHelper().onDestroy();
        }
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

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    /**
     *
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        // TODO - what if some Volley error happened ? Generic error dialog with option to retry ?
        loadMeetingsFragment(null);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null || (!networkInfo.isAvailable() && !networkInfo.isConnected())) {
            showNetworkDialog();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
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
        DownloadRequest dlReq = new DownloadRequest(Request.Method.GET, uri, this, this, this, SchemaConstants.MEETINGS_TABLE);
        DownloadRequestQueue.getInstance().addToRequestQueue(dlReq);
    }

    private void setBaseUI() {
        setContentView(R.layout.content_view_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_main_toolbar);
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
        isEmptyView = true;
        Bundle args = new Bundle();
        args.putBoolean("meetings_empty_view_key", isEmptyView);
        return args;
    }
    //</editor-fold>

    private boolean isEmptyView;
    private NetworkReceiver receiver = new NetworkReceiver();
}
