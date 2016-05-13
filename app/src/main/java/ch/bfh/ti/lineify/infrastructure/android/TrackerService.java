package ch.bfh.ti.lineify.infrastructure.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.IWayPointStore;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TrackerService","onStartCommand");
        Subscription localPersistSubscription = this.wayPointService.wayPointObservable((Track)intent.getSerializableExtra(Constants.trackerServiceExtraName))
                .doOnNext(wayPoint -> this.broadcastWayPoint(wayPoint))
                .buffer(10, TimeUnit.SECONDS)
                .doOnNext(bufferdWayPoints -> this.wayPointStore.persistWayPoints(bufferdWayPoints))
                .subscribe();

        Subscription syncSubscription = rx.Observable.interval(1, TimeUnit.MINUTES)
                .subscribe(x -> this.wayPointStore.syncWithBackend());

        this.subscriptions.add(localPersistSubscription);
        this.subscriptions.add(syncSubscription);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("TrackerService","onDestroy");
        this.subscriptions.unsubscribe();
    }

    private void broadcastWayPoint(WayPoint wayPoint) {
        Intent broadcastIntent = new Intent(Constants.wayPointBroadcastIntent);
        broadcastIntent.putExtra(Constants.wayPointBroadcastExtraName, wayPoint);

        this.sendBroadcast(broadcastIntent);
    }
}