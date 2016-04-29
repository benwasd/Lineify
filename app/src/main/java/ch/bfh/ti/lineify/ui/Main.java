package ch.bfh.ti.lineify.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.IWayPointService;

public class Main extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DI.setup(this.getApplicationContext());

        setContentView(R.layout.activity_main);

        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        findViewById(R.id.fullscreen_content).setSystemUiVisibility(flags);

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
        this.beginLocationShizzel(DI.container().resolve(IWayPointService.class));
    }

    public void beginLocationShizzel(IWayPointService wayPointService) {
        int[] counter = new int[1];

        wayPointService.wayPointObservable()
            .subscribe(wayPoint -> {
                    TextView output = (TextView)findViewById(R.id.fullscreen_content);
                    output.setText("C: " + (counter[0]++) + "\nSIZZL: " + wayPoint.altitude());

                    Log.i("Main", "Altitude " + wayPoint.altitude());
                    Log.i("Main", "Latitude " + wayPoint.latitude());
                    Log.i("Main", "Longitude " + wayPoint.longitude());
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}