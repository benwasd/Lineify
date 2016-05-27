package ch.bfh.ti.lineify.core;

import java.util.List;
import java.util.UUID;

import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import retrofit2.Call;

public interface IWayPointBackend {
    Call<List<Track>> getTracks(String userEmail);

    Call<List<WayPoint>> getWayPoints(UUID trackId);
}

