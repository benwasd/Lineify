package ch.bfh.ti.lineify.infrastructure.azure;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class WayPointStore implements IWayPointStore {
    private final Context context;
    private final MobileServiceClient serviceClient;
    private final MobileServiceSyncTable<WayPoint> wayPointTable;
    private final MobileServiceSyncTable<Track> trackTable;

    public WayPointStore(Context context) throws MalformedURLException {
        this.context = context;
        this.serviceClient = new MobileServiceClient(Constants.BACKEND_BASE_URL, context);
        this.wayPointTable = this.serviceClient.getSyncTable("WayPoint", WayPoint.class);
        this.trackTable = this.serviceClient.getSyncTable("Track", Track.class);

        this.initLocalStore();
    }

    @Override
    public void persistTrack(Track track) {
        Log.i("WayPointStore", "Persist track");

        try {
            this.trackTable.insert(track).get();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void persistWayPoints(List<WayPoint> wayPoints) {
        Log.i("WayPointStore", "Persist way points");

        try {
            for (WayPoint wayPoint : wayPoints) {
                this.wayPointTable.insert(wayPoint).get();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void syncWithBackend() {
        Log.i("WayPointStore", "Sync with backend");

        if (!this.isConnected()) {
            return;
        }

        runAsync(() -> {
            MobileServiceSyncContext syncContext = this.serviceClient.getSyncContext();

            try {
                Log.i("WayPointStore", "Pending sync operations: " + syncContext.getPendingOperations());
                Log.i("WayPointStore", "Sync");
                syncContext.push().get();
                Log.i("WayPointStore", "Pending sync operations: " + syncContext.getPendingOperations());
            }
            catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    private void initLocalStore() {
        runAsync(() -> {
            MobileServiceSyncContext syncContext = this.serviceClient.getSyncContext();

            if (syncContext.isInitialized()) {
                return;
            }

            try {
                SQLiteLocalStore localStore = new SQLiteLocalStore(this.serviceClient.getContext(), "OfflineStore", null, 1);

                Map<String, ColumnDataType> wayPointTableDefinition = new HashMap<>();
                wayPointTableDefinition.put("id", ColumnDataType.String);
                wayPointTableDefinition.put("trackId", ColumnDataType.String);
                wayPointTableDefinition.put("created", ColumnDataType.Date);
                wayPointTableDefinition.put("altitude", ColumnDataType.Real);
                wayPointTableDefinition.put("longitude", ColumnDataType.Real);
                wayPointTableDefinition.put("latitude", ColumnDataType.Real);
                wayPointTableDefinition.put("accuracy", ColumnDataType.Real);
                localStore.defineTable("WayPoint", wayPointTableDefinition);

                Map<String, ColumnDataType> tableDefinition = new HashMap<>();
                tableDefinition.put("id", ColumnDataType.String);
                tableDefinition.put("created", ColumnDataType.Date);
                tableDefinition.put("identifier", ColumnDataType.String);
                tableDefinition.put("userEmail", ColumnDataType.String);
                localStore.defineTable("Track", tableDefinition);

                syncContext.initialize(localStore, new SimpleSyncHandler()).get();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isConnected();
    }

    private static void runAsync(Runnable runnable) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                runnable.run();
                return null;
            }
        };

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            }
            else {
                task.execute().get();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}