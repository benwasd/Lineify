package ch.bfh.ti.lineify.core;

import java.util.List;
import java.util.UUID;

import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public interface IWayPointRepository {
    rx.Observable<List<Track>> getTracks(String userEmail);
    rx.Observable<List<WayPoint>> getWayPoints(UUID trackId);
}