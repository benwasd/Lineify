package ch.bfh.ti.lineify.infrastructure.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.ListUtils;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class TrackerService extends Service {
    private final IWayPointService wayPointService;
    private final IWayPointStore wayPointStore;
    private final CompositeSubscription subscriptions;

    public TrackerService() {
        this.wayPointService = DI.container().resolve(IWayPointService.class);
        this.wayPointStore = DI.container().resolve(IWayPointStore.class);
        this.subscriptions = new CompositeSubscription();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent startStopIntent, int flags, int startId) {
        Track track = (Track)startStopIntent.getSerializableExtra(Constants.TRACKER_SERVICE_TRACK_EXTRA_NAME);

        this.wayPointStore.persistTrack(track);

        Subscription localPersistSubscription = this.wayPointService.trackLocation(track)
            .doOnNext(wayPoint -> this.broadcastWayPoint(track, wayPoint, startStopIntent))
            .buffer(40, TimeUnit.SECONDS)
            .doOnNext(bufferdWayPoints -> this.wayPointStore.persistWayPoints(ListUtils.filter(bufferdWayPoints, w -> w.isRelevant(bufferdWayPoints))))
            .subscribe();

        Subscription syncSubscription = rx.Observable.interval(1, TimeUnit.MINUTES)
            .subscribe(x -> this.wayPointStore.syncWithBackend());

        this.subscriptions.add(localPersistSubscription);
        this.subscriptions.add(syncSubscription);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        this.subscriptions.unsubscribe();
    }

    private void broadcastWayPoint(Track track, WayPoint wayPoint, Intent startStopIntent) {
        Intent broadcastIntent = new Intent(Constants.WAY_POINT_BROADCAST);
        broadcastIntent.putExtra(Constants.WAY_POINT_BROADCAST_TRACK_EXTRA_NAME, track);
        broadcastIntent.putExtra(Constants.WAY_POINT_BROADCAST_POINT_EXTRA_NAME, wayPoint);
        broadcastIntent.putExtra(Constants.WAY_POINT_BROADCAST_INTENT_EXTRA_NAME, startStopIntent);

        this.sendBroadcast(broadcastIntent);
    }
}