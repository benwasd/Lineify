package ch.bfh.ti.lineify.ui.activities;

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

import java.util.ArrayList;
import java.util.List;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import ch.bfh.ti.lineify.infrastructure.android.TrackerService;
import ch.bfh.ti.lineify.ui.fragments.HistoryFragment;
import ch.bfh.ti.lineify.ui.fragments.StartTrackingDialog;
import ch.bfh.ti.lineify.ui.fragments.TrackingFragment;

public class Main extends AppCompatActivity {
    private IPermissionRequestor permissionRequestor;
    private IPermissionRequestor.RequestPermissionsResultHandler permissionResultHandler;
    private TrackingFragment trackingFragment;
    private HistoryFragment historyFragment;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager containerViewPager;
    private FloatingActionButton floatingActionButton;
    private BroadcastReceiver wayPointReceiver;
    private Intent trackerServiceStartStopIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.permissionRequestor = this.container().resolve(IPermissionRequestor.class);
        this.permissionRequestor.bindRequestPermissionsResultHandler(handler -> this.permissionResultHandler = handler);
        this.trackingFragment = this.container().resolve(TrackingFragment.class);
        this.historyFragment = this.container().resolve(HistoryFragment.class);

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.permissionRequestor.requestPermissions(this, () -> {
            this.findViews();
            this.initializeViews();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.wayPointReceiver != null) {
            this.registerReceiver(this.wayPointReceiver, new IntentFilter(Constants.WAY_POINT_BROADCAST));
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

    private IDependencyContainer container() {
        return DI.container(this.getApplicationContext());
    }

    private void findViews() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.tabLayout = (TabLayout) this.findViewById(R.id.tabLayout);
        this.containerViewPager = (ViewPager) this.findViewById(R.id.containerViewPager);
        this.floatingActionButton = (FloatingActionButton) this.findViewById(R.id.floatingActionButton);
    }

    private void initializeViews() {
        this.setSupportActionBar(this.toolbar);
        this.containerViewPager.setAdapter(this.createPagerAdapter());
        this.tabLayout.setupWithViewPager(this.containerViewPager);

        this.containerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                handleFloatingActionButtonVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        this.floatingActionButton.setOnClickListener(v -> {
            if (this.trackerServiceStartStopIntent == null) {
                StartTrackingDialog dialog = new StartTrackingDialog(this);
                dialog.show(this.getFragmentManager(), "StartTrackingDialog");
            }
            else {
                this.stopTracker();
            }
        });

        this.wayPointReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.WAY_POINT_BROADCAST)) {
                    Intent startStopIntent = intent.getParcelableExtra(Constants.WAY_POINT_BROADCAST_INTENT_EXTRA_NAME);
                    Track track = (Track) intent.getSerializableExtra(Constants.WAY_POINT_BROADCAST_TRACK_EXTRA_NAME);
                    WayPoint wayPoint = (WayPoint) intent.getSerializableExtra(Constants.WAY_POINT_BROADCAST_POINT_EXTRA_NAME);

                    Main.this.onReciveStartStopIntent(startStopIntent);
                    Main.this.trackingFragment.onReceiveWayPoint(track, wayPoint);
                }
            }
        };
    }

    private PagerAdapter createPagerAdapter() {
        PagerAdapter pagerAdapter = new PagerAdapter(this.getSupportFragmentManager());
        pagerAdapter.addFragment(this.trackingFragment, "Tracking");
        pagerAdapter.addFragment(this.historyFragment, "History");

        return pagerAdapter;
    }

    public void startTracker(Track track) {
        this.trackerServiceStartStopIntent = new Intent(this, TrackerService.class);
        this.trackerServiceStartStopIntent.putExtra(Constants.TRACKER_SERVICE_TRACK_EXTRA_NAME, track);
        this.handleFloatingActionButtonIcon();

        this.startService(trackerServiceStartStopIntent);
        this.trackingFragment.onServiceStart(track);
    }

    private void stopTracker() {
        this.trackingFragment.onServiceStop();
        this.stopService(this.trackerServiceStartStopIntent);

        this.trackerServiceStartStopIntent = null;
        this.handleFloatingActionButtonIcon();
    }

    private void onReciveStartStopIntent(Intent startStopIntent) {
        if (this.trackerServiceStartStopIntent != startStopIntent && this.floatingActionButton != null) {
            this.trackerServiceStartStopIntent = startStopIntent;
            this.handleFloatingActionButtonIcon();
        }
    }

    private void handleFloatingActionButtonIcon() {
        if (this.trackerServiceStartStopIntent == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_gps_fixed));
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_gps_off));
            }
        }
    }

    private void handleFloatingActionButtonVisibility(int position) {
        switch (position) {
            case 0:
                floatingActionButton.show();
                break;
            default:
                floatingActionButton.hide();
                break;
        }
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            this.fragmentList.add(fragment);
            this.fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return this.fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return this.fragmentTitleList.get(position);
        }
    }
}