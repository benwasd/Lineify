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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import ch.bfh.ti.lineify.infrastructure.android.TrackerService;
import ch.bfh.ti.lineify.playground.retrofitShizzle;
import ch.bfh.ti.lineify.ui.fragments.HistoryFragment;
import ch.bfh.ti.lineify.ui.fragments.MainFragment;

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

        this.setContentView(R.layout.activity_main);

        permissionRequestor.requestPermissions(this, () -> {
            this.initToolbar();
            this.initializeUi();
            this.initialize(wayPointService, wayPointStore);
            retrofitShizzle.run();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.wayPointReceiver != null) {
            this.registerReceiver(this.wayPointReceiver, new IntentFilter(Constants.WAY_POINT_BROADCAST_INTENT));
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
    }

    private void initializeUi() {
        this.viewPager = (ViewPager) this.findViewById(R.id.container);
        this.floatingActionButton = (FloatingActionButton) this.findViewById(R.id.fab_action);
        this.tabLayout = (TabLayout) this.findViewById(R.id.tabs);

        PagerAdapter pagerAdapter = new PagerAdapter(this.getSupportFragmentManager());
        pagerAdapter.addFragment(MainFragment.newInstance(0), getString(R.string.Tracking));
        pagerAdapter.addFragment(HistoryFragment.newInstance(1), getString(R.string.History));
        this.viewPager.setAdapter(pagerAdapter);

        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    private void initialize(IWayPointService wayPointService, IWayPointStore wayPointStore) {
        final Intent[] trackerServiceIntent = new Intent[1];
        this.floatingActionButton.setOnClickListener(v -> {
            if (trackerServiceIntent[0] == null) {
                Track track = new Track("benwasd@github", "Meine Wanderung");
                trackerServiceIntent[0] = new Intent(this, TrackerService.class);
                trackerServiceIntent[0].putExtra(Constants.TRACKER_SERVICE_EXTRA_NAME,track);

                startService(trackerServiceIntent[0]);

                TextView tvCurStatus = (TextView) findViewById(R.id.tv_CurStatusData);
                tvCurStatus.setText(R.string.running);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_gps_fixed));
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_gps_off));
                }

                TextView tvCurStatus = (TextView) findViewById(R.id.tv_CurStatusData);
                tvCurStatus.setText(R.string.not_running);

                stopService(trackerServiceIntent[0]);

                trackerServiceIntent[0] = null;
            }
        });

        this.wayPointReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.WAY_POINT_BROADCAST_INTENT)) {
                    WayPoint wayPoint = (WayPoint)intent.getSerializableExtra(Constants.WAY_POINT_BROADCAST_EXTRA_NAME);
                    TextView tvLatitude = (TextView) findViewById(R.id.tv_Latitude);
                    TextView tvLongitude = (TextView) findViewById(R.id.tv_Longitude);
                    TextView tvMetersAboveSealevel = (TextView) findViewById(R.id.tv_metersAboveSealevel);
                    TextView tvAccuracy = (TextView) findViewById(R.id.tv_accuracy);
                    double accuracy = wayPoint.accuracy();
                    tvLatitude.setText("N" + wayPoint.latitude());
                    tvLongitude.setText("E" + wayPoint.longitude());
                    tvAccuracy.setText("±" + accuracy);
                    tvMetersAboveSealevel.setText(wayPoint.altitude() + "");
                }
            }
        };
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}