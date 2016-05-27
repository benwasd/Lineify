package ch.bfh.ti.lineify.core;

import java.util.List;

import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public interface IWayPointStore {
    void persistTrack(Track track);
    void persistWayPoints(List<WayPoint> wayPoints);
    void syncWithBackend();
}
