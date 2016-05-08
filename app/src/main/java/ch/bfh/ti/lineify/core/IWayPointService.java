package ch.bfh.ti.lineify.core;

import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import rx.Observable;

public interface IWayPointService {
    Observable<WayPoint> wayPointObservable(Track track);
}