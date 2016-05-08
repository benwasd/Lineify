package ch.bfh.ti.lineify.infrastructure.android;

import android.app.IntentService;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.model.Track;

public class TrackerService extends IntentService {
    private final IWayPointService wayPointService;
    private final IWayPointStore wayPointStore;

    public TrackerService() {
        super("TrackerService");
        this.wayPointService = DI.container().resolve(IWayPointService.class);
        this.wayPointStore = DI.container().resolve(IWayPointStore.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.wayPointService.wayPointObservable(new Track("benwasd@github", "Meine Wanderung"))
                .buffer(10, TimeUnit.SECONDS)
                .doOnNext(bufferdWayPoints -> this.wayPointStore.persistWayPoints(bufferdWayPoints))
                .subscribe();

        rx.Observable.interval(1, TimeUnit.MINUTES)
                .subscribe(x -> this.wayPointStore.syncWithBackend());
    }
}