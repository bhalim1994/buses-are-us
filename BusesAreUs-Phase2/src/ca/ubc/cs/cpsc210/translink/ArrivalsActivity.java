package ca.ubc.cs.cpsc210.translink;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.ui.ArrivalsListFragment;

/**
 * Activity to show list of arrivals to user
 */
public class ArrivalsActivity extends Activity {
    private static final String LOG_TAG = "Arrivals Tag";
    private static final String AA_TAG = "ArrivalsListFragment";
    private ArrivalsListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.arrivals_list_layout);
        setTitle();
        configureActionBar();

        initFragment(savedInstanceState);

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
    }

    /**
     * Initialize arrivals list fragment
     * @param savedInstanceState  the state saved in previous instance
     */
    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.i(LOG_TAG, "restoring from instance state");
            fragment = (ArrivalsListFragment) getFragmentManager().findFragmentByTag(AA_TAG);
        } else if (fragment == null) {
            Log.i(LOG_TAG, "fragment was null");
            fragment = new ArrivalsListFragment();
            getFragmentManager().beginTransaction().add(R.id.arrivals_list, fragment, AA_TAG).commit();
        }
    }

    /**
     * Set up action bar
     */
    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setSubtitle(R.string.arrivals_activity_subtitle);
        }
    }

    /**
     * Set title
     */
    private void setTitle() {
        Intent i = getIntent();
        int stopNumber = i.getIntExtra(getString(R.string.stop_name_key), 99999);
        Stop stop = StopManager.getInstance().getStopWithNumber(stopNumber);
        setTitle(stop.getNumber() + " " + stop.getName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right);
    }
}
