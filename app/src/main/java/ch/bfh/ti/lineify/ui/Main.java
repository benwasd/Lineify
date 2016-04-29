package ch.bfh.ti.lineify.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.IWayPointService;

public class Main extends Activity {
    private IPermissionRequestor.RequestPermissionsResultHandler permissionResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DI.setup(this.getApplicationContext());

        this.setContentView(R.layout.activity_main);
        this.findViewById(R.id.fullscreen_content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

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
                    TextView output = (TextView)findViewById(R.id.fullscreen_content);
                    output.setText("C: " + (counter[0]++) + "\nSIZZL: " + wayPoint.altitude());

                    Log.i("Main", "Altitude " + wayPoint.altitude());
                    Log.i("Main", "Latitude " + wayPoint.latitude());
                    Log.i("Main", "Longitude " + wayPoint.longitude());
                });
    }
}