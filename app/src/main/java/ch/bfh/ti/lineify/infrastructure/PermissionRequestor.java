package ch.bfh.ti.lineify.infrastructure;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

import ch.bfh.ti.lineify.core.IPermissionRequestor;
import rx.functions.Action1;

public class PermissionRequestor implements IPermissionRequestor {
    private static final int PERMISSION_REQUEST_ID = 1337;
    private static final String[] PERMISSIONS = new String[]
    {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
    };

    private final Context context;
    private Action1<RequestPermissionsResultHandler> handler;

    public PermissionRequestor(Context context) {
        this.context = context;
    }

    @Override
    public void bindRequestPermissionsResultHandler(Action1<RequestPermissionsResultHandler> handler) {
        this.handler = handler;
    }

    @Override
    public void requestPermissions(Activity activity, Runnable successRunnable) {
        Runnable deniedRunnable = () -> {}; // TODO: Implement explanation dialog, http://developer.android.com/training/permissions/requesting.html
        this.requestPermissions(activity, successRunnable, deniedRunnable);
    }

    @Override
    public void requestPermissions(Activity activity,Runnable successRunnable, Runnable deniedRunnable) {
        if (this.allRequiredPermissionsGranted()) {
            successRunnable.run();
        }
        else {
            if (this.shouldShowRequestPermissionRationale(activity)) {
                deniedRunnable.run();
            }
            else {
                ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQUEST_ID);

                this.handler.call((requestCode, permissions, grantResults) -> {
                    if (requestCode == PERMISSION_REQUEST_ID) {
                        this.handlePermissionRequest(Arrays.asList(permissions), grantResults, successRunnable, deniedRunnable);
                    }
                });
            }
        }
    }

    public boolean allRequiredPermissionsGranted() {
        boolean result = true;
        for (String permission : PERMISSIONS) {
            result &= ContextCompat.checkSelfPermission(this.context, permission) == PackageManager.PERMISSION_GRANTED;
        }

        return result;
    }

    public boolean shouldShowRequestPermissionRationale(Activity activity) {
        boolean result = false;
        for (String permission : PERMISSIONS) {
            result |= ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }

        return result;
    }

    private void handlePermissionRequest(List<String> permissions, int[] grantResults, Runnable successRunnable, Runnable deniedRunnable) {
        boolean success = true;
        for (String permission : PERMISSIONS) {
            int index = permissions.indexOf(permission);
            success &= grantResults[index] == PackageManager.PERMISSION_GRANTED;
        }

        if (success) {
            successRunnable.run();
        }
        else {
            deniedRunnable.run();
        }
    }
}
