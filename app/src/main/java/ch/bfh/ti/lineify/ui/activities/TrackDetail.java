package ch.bfh.ti.lineify.ui.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class TrackDetail extends AppCompatActivity {
    private IWayPointRepository wayPointRepository;
    private Track track;
    private Toolbar toolbar;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.wayPointRepository = this.container().resolve(IWayPointRepository.class);
        this.track = (Track) this.getIntent().getSerializableExtra(Constants.TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME);

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_track_detail);

        this.findViews();
        this.initializeViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.loadWayPoints();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private IDependencyContainer container() {
        return DI.container(this.getApplicationContext());
    }

    private void findViews() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.lineChart = (LineChart) findViewById(R.id.lineChart);
    }

    private void initializeViews() {
        this.toolbar.setTitle(this.track.identifier());
        this.toolbar.setSubtitle(this.track.created().toString());

        this.setSupportActionBar(this.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.lineChart.setDescription("setDescription");
        this.lineChart.setNoDataTextDescription("setNoDataTextDescription");
        this.lineChart.setTouchEnabled(true);
        this.lineChart.setAutoScaleMinMaxEnabled(false);
        this.lineChart.getLegend().setEnabled(false);

        this.loadWayPoints();
    }

    private void loadWayPoints() {
        this.wayPointRepository.getWayPoints(this.track.id()).subscribe(
            result -> this.buildAndSetChartData(result),
            exception -> this.handleNetworkError(exception)
        );
    }

    private void buildAndSetChartData(List<WayPoint> wayPoints) {
        if (wayPoints.size() < 3) {
            return;
        }

        List<String> xAxisLabels = new ArrayList<>();
        List<Entry> yAxisValues = new ArrayList<>();
        buildAxis(createWayPointMap(wayPoints), xAxisLabels, yAxisValues);

        setData(this.lineChart, xAxisLabels, yAxisValues);
    }

    private void handleNetworkError(Throwable e) {
        Log.e("HistoryFragment", "Error while loading tracks.", e);
        Snackbar.make(this.getWindow().getDecorView(), R.string.load_lines_network_error, Snackbar.LENGTH_LONG).show();
    }

    private static SortedMap<Long, WayPoint> createWayPointMap(List<WayPoint> wayPoints) {
        SortedMap<Long, WayPoint> map = new TreeMap<>();
        for (WayPoint wayPoint : wayPoints) {
            map.put(wayPoint.created().getTime(), wayPoint);
        }

        return map;
    }

    private void buildAxis(SortedMap<Long, WayPoint> map, List<String> labels, List<Entry> values) {
        final Long[] allKeys = map.keySet().<Long>toArray(new Long[0]);
        final int maxKey = allKeys.length - 1;

        final long minMs = map.firstKey();
        final long maxMs = map.lastKey();
        final long fullRangeMs = maxMs - minMs;
        final long rangeMs = fullRangeMs / map.size() / 2;
        final long semiRangeMs = rangeMs / 2;

        final long graphStart = minMs - semiRangeMs;
        final long graphEnd = maxMs + semiRangeMs;

        long start = graphStart;
        long end = graphStart + rangeMs;
        int i = 0;

        for (; end <= graphEnd; start += rangeMs, end += rangeMs) {
            List<WayPoint> wps = new ArrayList<>();

            for (; allKeys[i] < end && i < allKeys.length; i++) {
                wps.add(map.get(allKeys[i]));
            }

            labels.add(new Date(start + semiRangeMs).toString());
            values.add(new Entry((float)WayPoint.averageForChart(wps), labels.size()));

            if (i == maxKey) {
                break;
            }
        }
    }

    private static void setData(LineChart lineChart, List<String> labels, List<Entry> values) {
        LineDataSet lineDataSet = new LineDataSet(values, "VALUES");
        LineData line = new LineData()
        {{
            setXVals(labels);
            addDataSet(lineDataSet);

        }};

        lineChart.setData(line);
        lineChart.getAxisLeft().setAxisMaxValue(700);
        lineChart.getAxisLeft().setAxisMinValue(0);
        lineChart.invalidate();
    }
}