package ch.bfh.ti.lineify.core;

import java.util.UUID;

import ch.bfh.ti.lineify.core.model.WayPoint;

public interface IWayPointService {
    rx.Observable<WayPoint> trackLocation(UUID trackId);
}