package ch.bfh.ti.lineify.core;

public interface IPermissionRequestor {
    void bindRequestPermissionsResultHandler(rx.functions.Action1<RequestPermissionsResultHandler> handler);
    void requestPermissions(Runnable successRunnable);
    void requestPermissions(Runnable successRunnable, Runnable deniedRunnable);

    interface RequestPermissionsResultHandler {
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }
}