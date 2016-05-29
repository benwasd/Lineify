package ch.bfh.ti.lineify.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class TrackDetailActivity extends AppCompatActivity {
    private ListView listView;
    private Track track;
    private IWayPointRepository wayPointRepository;
    private List<WayPoint> wpList;
    private EditText multiText;
    private Toolbar toolbar;
    private java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        this.wayPointRepository = DI.container().resolve(IWayPointRepository.class);
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
        this.multiText = (EditText) findViewById(R.id.editText);
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
        this.multiText.setText("");
        this.wayPointRepository.getWayPoints(this.track.id()).subscribe(
                w -> {
                    Log.i("TrackDetailActivity", "Loading Waypoints..");
                    for(WayPoint wayPoint : w){
                        this.multiText.append(wayPoint.created()+"|"+wayPoint.altitude()+"\n");
                    }
                },
                e -> {
                    Log.e("TrackDetailActivity", "Error while loading WayPoints.", e);
                    this.multiText.setText("Error while loading WayPoints.");
                }
        );

    }
}