package ch.bfh.ti.lineify.core;

import java.util.List;

import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface IWayPointRestApiDefinition {
    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("tables/Track")
    Call<List<Track>> queryTracks(@Query("$filter") String filter);

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("tables/WayPoint")
    Call<List<WayPoint>> queryWayPoint(@Query("$filter") String filter);
}
