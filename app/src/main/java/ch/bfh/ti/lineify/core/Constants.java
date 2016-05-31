package ch.bfh.ti.lineify.core;

public class Constants {
    public static final String BACKEND_BASE_URL = "https://lineify.azurewebsites.net/";

    public static final String WAY_POINT_BROADCAST = "WAY_POINT_BROADCAST";
    public static final String WAY_POINT_BROADCAST_POINT_EXTRA_NAME = "WAY_POINT_BROADCAST_POINT_EXTRA_NAME";
    public static final String WAY_POINT_BROADCAST_TRACK_EXTRA_NAME = "WAY_POINT_BROADCAST_TRACK_EXTRA_NAME";
    public static final String WAY_POINT_BROADCAST_INTENT_EXTRA_NAME = "WAY_POINT_BROADCAST_INTENT_EXTRA_NAME";

    public static final String TRACKER_SERVICE_TRACK_EXTRA_NAME = "TRACKER_SERVICE_TRACK_EXTRA_NAME";

    public static final String TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME = "TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME";

    public static final int PERMISSION_REQUEST_ID = 1337;
    public static final String[] PERMISSIONS = new String[]
    {
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
    };
}