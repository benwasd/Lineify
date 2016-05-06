package ch.bfh.ti.lineify.infrastructure.android;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.model.Track;

public class TrackerService extends IntentService {
    private final IWayPointService wayPointService;

    public TrackerService() {
        super("TrackerService");
        this.wayPointService = DI.container().resolve(IWayPointService.class);

        Log.i("TrackerService", "instanciated");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int[] counter = new int[1];

        Log.i("TrackerService", "onHandleIntent");

        this.wayPointService.wayPointObservable(new Track("benwasd@github", "Meine Wanderung"))
                .subscribe(wayPoint -> {
                    Log.i("TrackerService", "Altitude " + wayPoint.altitude());
                    Log.i("TrackerService", "Latitude " + wayPoint.latitude());
                    Log.i("TrackerService", "Longitude " + wayPoint.longitude());
                });


    }
}