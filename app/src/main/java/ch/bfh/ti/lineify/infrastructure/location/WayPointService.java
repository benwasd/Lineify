package ch.bfh.ti.lineify.infrastructure.location;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;

import java.util.Date;

import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.model.WayPoint;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

public class WayPointService implements IWayPointService {
    private final Context context;

    public WayPointService(Context context) {
        this.context = context;
    }

    @Override
    public Observable<WayPoint> wayPointObservable() {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this.context);
        return locationProvider.getUpdatedLocation(this.buildLocationRequest())
            .map(location -> new WayPoint(new Date(), location.getAltitude(), location.getLongitude(), location.getLatitude()));
    }

    private LocationRequest buildLocationRequest() {
        return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(100);
    }
}