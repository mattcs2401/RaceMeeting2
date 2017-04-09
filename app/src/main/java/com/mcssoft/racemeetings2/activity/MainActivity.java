package com.mcssoft.racemeetings2.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mcssoft.racemeetings2.fragment.DateSelectFragment;
import com.mcssoft.racemeetings2.interfaces.IDownloadResult;
import com.mcssoft.racemeetings2.interfaces.IDateSelect;
import com.mcssoft.racemeetings2.interfaces.IProcessResult;
import com.mcssoft.racemeetings2.network.DownloadData;
import com.mcssoft.racemeetings2.utility.ProcessResult;
import com.mcssoft.racemeetings2.utility.RaceDate;
import com.mcssoft.racemeetings2.utility.Resources;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   IDownloadResult,
                   IDateSelect,
                   IProcessResult {

    //<editor-fold defaultstate="collapsed" desc="Region: Interface">
    /**
     * Result from async task DownloadData return here.
     * @param s1
     * @param results Results of the operation.
     */
    @Override
    public void downloadResult(String s1, String results) {
        String message;
        String[] result = results.split(":");
        if((result != null) && (result[0].equals("Exception"))) {
            // TODO - somesort of alert type dialog that has options.
            Toast.makeText(this, "Error: " + result[1], Toast.LENGTH_LONG).show();
        } else {
            message = Resources.getInstance().getString(R.string.raceday_raceinfo_dowload_msg);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            message = Resources.getInstance().getString(R.string.raceday_process_msg);
            ProcessResult processResult = new ProcessResult(this, message, results, null);
            processResult.processResult = this;
            processResult.execute();
        }
    }

    /**
     * Results of the DateSelectFragment return here.
     * @param values [0] YYYY, [1] M(M), [2] D(D)
     */
    @Override
    public void iDateValues(String[] values) {
        getRacesOnDay(values);
    }

    /**
     * Result from async task ProcessResult return here.
     * @param s1
     * @param results Results of the operation.
     */
    @Override
    public void processResult(String s1, String results) {
        // TBA
        String bp = "";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.getInstance(this);   // setup resources access.
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
    protected void onStart() {
        super.onStart();
        // TODO - check for network.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            getRacesOnDay(null);

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
    private void getRacesOnDay(@Nullable String[] date) {
        URL url = null;
        if(date == null) {
            try {
                url = new URL(createRaceDayUrl(null));
            } catch (MalformedURLException ex) {
                Log.d("", ex.getMessage());
            }
        } else {
            try {
                url = new URL(createRaceDayUrl(date));
            } catch (MalformedURLException ex) {
                Log.d("", ex.getMessage());
            }
        }
        DownloadData dld = new DownloadData(this, url,
                Resources.getInstance().getString(R.string.raceday_download_msg), null);
        dld.downloadResult = this;
        dld.execute();

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
    //</editor-fold>
}
