package ch.bfh.ti.lineify.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import ch.bfh.ti.lineify.infrastructure.android.TrackerService;

public class Main2Activity extends AppCompatActivity {
    private IPermissionRequestor.RequestPermissionsResultHandler permissionResultHandler;
    private MobileServiceClient mobileServiceClient;
    private Intent trackerServiceIntent;
    private boolean serviceRunning=false;
    private BroadcastReceiver wayPointReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constants.wayPointBroadcastIntent) {
                WayPoint wayPoint = (WayPoint)intent.getSerializableExtra(Constants.wayPointBroadcastExtraName);
                TextView tvCurStatus = (TextView) findViewById(R.id.tv_CurStatusData);
                tvCurStatus.setText(wayPoint.altitude()+"m, " +wayPoint.latitude()+" | " +wayPoint.longitude()+" | "+wayPoint.accuracy());
            }
        }
    };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DI.setup(this.getApplicationContext());

        IPermissionRequestor permissionRequestor = DI.container().resolve(IPermissionRequestor.class);
        IWayPointService wayPointService = DI.container().resolve(IWayPointService.class);
        permissionRequestor.bindRequestPermissionsResultHandler(handler -> this.permissionResultHandler = handler);
        permissionRequestor.requestPermissions(this, () -> this.init(wayPointService));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.wayPointReceiver, new IntentFilter(Constants.wayPointBroadcastIntent));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.wayPointReceiver);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                     rootView = inflater.inflate(R.layout.fragment_lineify_main, container, false);
                     break;
                case 2:
                     rootView = inflater.inflate(R.layout.fragment_lineify_history, container, false);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_lineify_main, container, false);
                    break;
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tracking";
                case 1:
                    return "History";
            }
            return null;
        }
    }

    public void init(IWayPointService wayPointService) {

        setContentView(R.layout.activity_lineify_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Track track = new Track("benwasd@github", "Meine Wanderung");
        trackerServiceIntent = new Intent(this, TrackerService.class);
        trackerServiceIntent.putExtra(Constants.trackerServiceExtraName,track);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_action);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView tvCurStatus = (TextView) findViewById(R.id.tv_CurStatusData);

                if (!serviceRunning){
                    startService(trackerServiceIntent);
                    serviceRunning=true;
                    Snackbar.make(v, "startService(trackerServiceIntent)", Snackbar.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        fab.setImageDrawable(getDrawable(R.drawable.ic_gps_fixed));
                    }
                    tvCurStatus.setText("running");


                }
                else{
                    stopService(trackerServiceIntent);
                    serviceRunning=false;
                    Snackbar.make(v, "stopService(trackerServiceIntent)", Snackbar.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        fab.setImageDrawable(getDrawable(R.drawable.ic_gps_off));
                    }
                    tvCurStatus.setText("stopped");
                }
            }
        });

    }
}
