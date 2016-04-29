package ch.bfh.ti.lineify;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;

public class Main extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        findViewById(R.id.fullscreen_content).setSystemUiVisibility(flags);

        Observable<String> helloMessagesObservable = Observable.create(subscriber -> {
            subscriber.onNext("HI");
            subscriber.onNext("HALLO");
            subscriber.onNext("GRüEZI");
            subscriber.onCompleted();
        });

        helloMessagesObservable.subscribe(s -> Log.i("Main", s));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: Implement explanation dialog, http://developer.android.com/training/permissions/requesting.html
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
        else {
            this.beginLocationShizzel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        this.beginLocationShizzel();
    }

    public void beginLocationShizzel() {
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100);

        int[] counter = new int[1];

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this.getApplicationContext());
        Subscription subscription = locationProvider.getUpdatedLocation(request)
                .subscribe(location -> {
                    TextView output = (TextView)findViewById(R.id.fullscreen_content);
                    output.setText("C: " + (counter[0]++) + "\nSIZZL: " + location.getAltitude());

                    Log.i("Main", "Accuracy " + location.getAccuracy());
                    Log.i("Main", "Altitude " + location.getAltitude());
                    Log.i("Main", "Latitude " + location.getLatitude());
                    Log.i("Main", "Longitude " + location.getLongitude());
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}