package ch.bfh.ti.lineify.core;

import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public interface IWayPointService {
    rx.Observable<WayPoint> trackLocation(Track track);
}