package ch.bfh.ti.lineify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.DateUtil;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.ui.adapter.DividerItemDecoration;
import ch.bfh.ti.lineify.ui.adapter.WayPointRecyclerViewAdapter;

public class TrackDetailList extends AppCompatActivity {

    private IWayPointRepository wayPointRepository;
    private WayPointRecyclerViewAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    public RecyclerView.LayoutManager recyclerViewLayoutManager;
    private Track track;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.wayPointRepository = this.container().resolve(IWayPointRepository.class);
        this.recyclerAdapter = new WayPointRecyclerViewAdapter();
        this.recyclerViewLayoutManager=new LinearLayoutManager(this);
        this.track = (Track) this.getIntent().getSerializableExtra(Constants.TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_track_detail_list);

        this.findViews();
        this.initializeViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.loadWayPoints();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadWayPoints() {
        this.wayPointRepository.getWayPoints(this.track.id()).subscribe(
                result -> {
                    this.recyclerAdapter.setWayPoints(result);
                },
                exception -> {
                    this.handleNetworkError(exception);
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private IDependencyContainer container() {
        return DI.container(this.getApplicationContext());
    }

    private void findViews() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.recyclerView = (RecyclerView) findViewById(R.id.trackRecyclerView);
    }

    private void initializeViews() {
        setSupportActionBar(this.toolbar);

        this.toolbar.setTitle(this.track.identifier());
        this.toolbar.setSubtitle(DateUtil.format(this.track.created()));
        this.setSupportActionBar(this.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.recyclerView.setLayoutManager(this.recyclerViewLayoutManager);
        this.recyclerView.setAdapter(this.recyclerAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrackDetailList.this, TrackDetail.class);
                intent.putExtra(Constants.TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME, track);

                startActivity(intent);
            }
        });

    }

    private void handleNetworkError(Throwable e) {
        Log.e("HistoryFragment", "Error while loading tracks.", e);
        Snackbar.make(this.getWindow().getDecorView(), R.string.load_lines_network_error, Snackbar.LENGTH_LONG).show();
    }
}
