package ch.bfh.ti.lineify.playground;

import android.util.Log;

import java.util.List;

import ch.bfh.ti.lineify.core.IWayPointRestApiDefinition;
import ch.bfh.ti.lineify.core.model.WayPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class retrofitShizzle {
    public static void run(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://lineify.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        retrofit.create(IWayPointRestApiDefinition.class).queryWayPoint("trackid eq '82b49fbd-5a10-4ff1-b971-c09b180dcd87'")
                .enqueue(
                        new Callback<List<WayPoint>>() {
                            @Override
                            public void onResponse(Call<List<WayPoint>> call, Response<List<WayPoint>> response) {
                                Log.i("MAIN", "IWayPointBackend size: "+response.body().size());
                                for (WayPoint wayPoint : response.body()){
                                    Log.i("MAIN","IWayPointBackend created:"+ wayPoint.created() +"alti: " +wayPoint.altitude() );
                                }

                            }

                            @Override
                            public void onFailure(Call<List<WayPoint>> call, Throwable t) {
                                Log.e("MAIN", "IWayPointBackend FAIL", t);
                            }
                        }
                );
    }
}
