package ch.bfh.ti.lineify.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.TimeZone;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class TrackDetail extends AppCompatActivity {
    private ListView listView;
    private Track track;
    private IWayPointRepository wayPointRepository;
    private ArrayList<WayPoint> wpList;
    private Toolbar toolbar;
    private java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance();
    private LineDataSet dataset;
    private LineData lineData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        this.wayPointRepository = DI.container(this.getApplicationContext()).resolve(IWayPointRepository.class);
        this.track = (Track) this.getIntent().getSerializableExtra(Constants.TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME);

        this.findViews();
        this.initializeUi();
        this.loadWayPoints();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initializeUi() {
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));

        this.toolbar.setTitle(this.track.identifier());
        this.toolbar.setSubtitle(dateFormat.format(this.track.created()));


        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadWayPoints(){

        LineChart chart = (LineChart) findViewById(R.id.lineChart);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<String>();
        dataset = new LineDataSet(entries, getString(R.string.MeterUeberMeer));
        lineData = new LineData(xVals, dataset);
        lineData.addDataSet(dataset);
        chart.setData(lineData);
        this.wayPointRepository.getWayPoints(this.track.id()).subscribe(
                w -> {
                    for (WayPoint wayPoint : w) {
                        Log.i("TrackDetailActivity", "Waypoint Received:"+wayPoint.altitude());
                        dataset.addEntry(new Entry((float)wayPoint.altitude(),(int)wayPoint.created().getTime()));
                        dataset.notifyDataSetChanged(); // let the data know a dataSet changed
                        chart.notifyDataSetChanged(); // let the chart know it's data changed
                        chart.invalidate();
                    }
                },
                e -> {
                    Log.e("TrackDetailActivity", "Error while loading WayPoints.", e);
                }
        );
        chart.setDescription("");
        chart.setNoDataTextDescription(getString(R.string.NoDataTextDescription));
        chart.setTouchEnabled(false);

    }
}