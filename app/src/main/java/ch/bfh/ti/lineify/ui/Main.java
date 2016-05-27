package ch.bfh.ti.lineify.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import ch.bfh.ti.lineify.infrastructure.android.TrackerService;

public class Main extends AppCompatActivity {
    private IPermissionRequestor.RequestPermissionsResultHandler permissionResultHandler;
    private BroadcastReceiver wayPointReceiver;

    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DI.setup(this.getApplicationContext());

        IPermissionRequestor permissionRequestor = DI.container().resolve(IPermissionRequestor.class);
        IWayPointService wayPointService = DI.container().resolve(IWayPointService.class);
        IWayPointStore wayPointStore = DI.container().resolve(IWayPointStore.class);
        permissionRequestor.bindRequestPermissionsResultHandler(handler -> this.permissionResultHandler = handler);

        this.setContentView(R.layout.activity_lineify_main);

        permissionRequestor.requestPermissions(this, () -> {
            this.initializeUi();
            this.initialize(wayPointService, wayPointStore);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.wayPointReceiver != null) {
            this.registerReceiver(this.wayPointReceiver, new IntentFilter(Constants.wayPointBroadcastIntent));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (this.wayPointReceiver != null) {
            this.unregisterReceiver(this.wayPointReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionResultHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initializeUi() {
        this.viewPager = (ViewPager) this.findViewById(R.id.container);
        this.floatingActionButton = (FloatingActionButton) this.findViewById(R.id.fab_action);
        this.tabLayout = (TabLayout) this.findViewById(R.id.tabs);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager());
        this.viewPager.setAdapter(sectionsPagerAdapter);

        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    private void initialize(IWayPointService wayPointService, IWayPointStore wayPointStore) {
        final Intent[] trackerServiceIntent = new Intent[1];
        this.floatingActionButton.setOnClickListener(v -> {
            if (trackerServiceIntent[0] == null) {
                Track track = new Track("benwasd@github", "Meine Wanderung");
                trackerServiceIntent[0] = new Intent(this, TrackerService.class);
                trackerServiceIntent[0].putExtra(Constants.trackerServiceExtraName,track);

                startService(trackerServiceIntent[0]);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_gps_fixed));
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_gps_off));
                }

                stopService(trackerServiceIntent[0]);

                trackerServiceIntent[0] = null;
            }
        });

        this.wayPointReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == Constants.wayPointBroadcastIntent) {
                    WayPoint wayPoint = (WayPoint)intent.getSerializableExtra(Constants.wayPointBroadcastExtraName);
                    TextView tvCurStatus = (TextView) findViewById(R.id.tv_CurStatusData);
                    tvCurStatus.setText(wayPoint.altitude()+"m, " +wayPoint.latitude()+" | " +wayPoint.longitude()+" | "+wayPoint.accuracy());
                }
            }
        };
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
}