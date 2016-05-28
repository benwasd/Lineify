package ch.bfh.ti.lineify.infrastructure.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import java.util.List;
import java.util.UUID;

import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.model.WayPoint;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

public class WayPointService implements IWayPointService {
    private final Context context;

    public WayPointService(Context context) {
        this.context = context;
    }

    @Override
    public rx.Observable<WayPoint> trackLocation(UUID trackId) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this.context);
        return locationProvider.getUpdatedLocation(this.buildLocationRequest())
            .filter(location -> location.hasAltitude())
            .buffer(8, 5)
            .map(locations -> optimize(locations))
            .map(location -> new WayPoint(trackId, location.getAltitude(), location.getLongitude(), location.getLatitude(), location.getAccuracy()));
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