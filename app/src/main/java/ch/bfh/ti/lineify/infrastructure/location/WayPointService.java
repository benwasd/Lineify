package ch.bfh.ti.lineify.infrastructure.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import java.util.List;

import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import static ch.bfh.ti.lineify.core.model.WayPoint.*;

public class WayPointService implements IWayPointService {
    private final Context context;

    public WayPointService(Context context) {
        this.context = context;
    }

    @Override
    public rx.Observable<WayPoint> trackLocation(Track track) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this.context);
        return locationProvider.getUpdatedLocation(this.buildLocationRequest())
            .buffer(6, 4)
            .map(locations -> optimize(locations))
            .doOnNext(l -> track.incrementWayPointCount())
            .map(location -> new WayPoint(track.id(), getSwissAltitude(location.getAltitude()), location.getLongitude(), location.getLatitude(), location.getAccuracy()))
            .filter(wayPoint -> wayPoint.hasPlausibleAltitude());
    }

    private LocationRequest buildLocationRequest() {
        return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000);
    }

    private Location optimize(List<Location> locations) {
        Location result = null;

        for (Location location : locations) {
            if (result == null || result.getAccuracy() > location.getAccuracy()) {
                result = location;
            }
        }

        return result;
    }
}