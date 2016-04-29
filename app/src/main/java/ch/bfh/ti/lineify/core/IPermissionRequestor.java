package ch.bfh.ti.lineify.core;

public interface IPermissionRequestor {
    void bindRequestPermissionsResultHandler(rx.functions.Action1<RequestPermissionsResultHandler> handler);
    void requestPermissions(android.app.Activity activity, Runnable successRunnable);
    void requestPermissions(android.app.Activity activity,Runnable successRunnable, Runnable deniedRunnable);

    interface RequestPermissionsResultHandler {
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }
}