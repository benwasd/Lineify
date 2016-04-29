package ch.bfh.ti.lineify.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.GitHubService;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.User;
import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.IWayPointService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends AppCompatActivity {
    private IPermissionRequestor.RequestPermissionsResultHandler permissionResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DI.setup(this.getApplicationContext());

        this.setContentView(R.layout.activity_main);

        IPermissionRequestor permissionRequestor = DI.container().resolve(IPermissionRequestor.class);
        IWayPointService wayPointService = DI.container().resolve(IWayPointService.class);
        permissionRequestor.bindRequestPermissionsResultHandler(handler -> this.permissionResultHandler = handler);
        permissionRequestor.requestPermissions(this, () -> this.beginLocationShizzel(wayPointService));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionResultHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void beginLocationShizzel(IWayPointService wayPointService) {
        int[] counter = new int[1];

        wayPointService.wayPointObservable()
                .subscribe(wayPoint -> {
                    TextView output = (TextView)findViewById(R.id.txtContentMain);
                    output.setText("C: " + (counter[0]++) + "\nAltitude: " + wayPoint.altitude());

                    Log.i("Main", "Altitude " + wayPoint.altitude());
                    Log.i("Main", "Latitude " + wayPoint.latitude());
                    Log.i("Main", "Longitude " + wayPoint.longitude());
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(GitHubService.class).users("benwasd")
            .enqueue(
                new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                      Log.i("MAIN", "GitHubService Id: " + response.body().getId());
                      Log.i("MAIN", "GitHubService Name: " + response.body().getName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                      Log.e("MAIN", "GitHubService FAIL", t);
                    }
                }
            );
    }
}