package ch.bfh.ti.lineify.infrastructure;

import java.util.List;
import java.util.UUID;

import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class WayPointRepository implements IWayPointRepository {
    private final Retrofit retrofit;
    private final IRestApiDefinition restApiDefinition;

    public WayPointRepository() {
        this.retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BACKEND_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        this.restApiDefinition = this.retrofit.create(IRestApiDefinition.class);
    }

    @Override
    public rx.Observable<List<Track>> getTracks(String userEmail) {
        return rx.Observable.create(observer ->
            this.restApiDefinition.queryTracks("userEmail eq '" + userEmail + "'&$orderby=identifier asc").enqueue(new Callback<List<Track>>() {
                @Override
                public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                    observer.onNext(response.body());
                    observer.onCompleted();
                }

                @Override
                public void onFailure(Call<List<Track>> call, Throwable t) {
                    observer.onError(t);
                }
            })
        );
    }

    @Override
    public rx.Observable<List<WayPoint>> getWayPoints(UUID trackId) {
        return rx.Observable.create(observer ->
            this.restApiDefinition.queryWayPoint("trackid eq '" + trackId + "'&$orderby=created asc").enqueue(new Callback<List<WayPoint>>() {
                @Override
                public void onResponse(Call<List<WayPoint>> call, Response<List<WayPoint>> response) {
                    observer.onNext(response.body());
                    observer.onCompleted();
                }

                @Override
                public void onFailure(Call<List<WayPoint>> call, Throwable t) {
                    observer.onError(t);
                }
            })
        );
    }

    public interface IRestApiDefinition {
        @Headers("ZUMO-API-VERSION: 2.0.0")
        @GET("tables/Track")
        Call<List<Track>> queryTracks(@Query("$filter") String filter);

        @Headers("ZUMO-API-VERSION: 2.0.0")
        @GET("tables/WayPoint")
        Call<List<WayPoint>> queryWayPoint(@Query("$filter") String filter);
    }
}
