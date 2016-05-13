package ch.bfh.ti.lineify.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.GitHubService;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.TodoItem;
import ch.bfh.ti.lineify.User;
import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.infrastructure.android.TrackerService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends AppCompatActivity {
    private IPermissionRequestor.RequestPermissionsResultHandler permissionResultHandler;
    private MobileServiceClient mobileServiceClient;
    private Intent trackerServiceIntent;
    private boolean serviceRunning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DI.setup(this.getApplicationContext());

        this.setContentView(R.layout.activity_main);
        trackerServiceIntent = new Intent(this, TrackerService.class);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!serviceRunning){
                    startService(trackerServiceIntent);
                    serviceRunning=true;
                    Snackbar.make(v, "startService(trackerServiceIntent)", Snackbar.LENGTH_LONG).show();
                    Log.i("Main","startService(trackerServiceIntent)");
                }
                else{
                    stopService(trackerServiceIntent);
                    serviceRunning=false;
                    Snackbar.make(v, "stopService(trackerServiceIntent)", Snackbar.LENGTH_LONG).show();
                    Log.i("Main","stopService(trackerServiceIntent)");
                }
            }
        });

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
//        Intent trackerServiceIntent = new Intent(this, TrackerService.class);
//        startService(trackerServiceIntent);

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

        try {
            this.mobileServiceClient = new MobileServiceClient("https://lineify.azurewebsites.net", this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TodoItem item = new TodoItem();
        item.Text = "Awesome item";

        ListenableFuture<TodoItem> asd = this.mobileServiceClient.getTable(TodoItem.class)
                .insert(item);

        Futures.addCallback(asd, new FutureCallback<TodoItem>() {
            @Override
            public void onSuccess(TodoItem result) {
                Log.e("MAIN", "TODOITEM INSERT SUCCESS " + result.Id);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MAIN", "TODOITEM INSERT FAIL", t);
            }
        });

    }
}