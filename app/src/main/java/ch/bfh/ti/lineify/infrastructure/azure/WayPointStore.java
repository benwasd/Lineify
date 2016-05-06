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

import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class WayPointStore implements IWayPointStore {
    private final Context context;
    private final MobileServiceClient serviceClient;
    private final MobileServiceSyncTable<WayPoint> wayPointTable;

    public WayPointStore(Context context) throws MalformedURLException {
        this.context = context;
        this.serviceClient = new MobileServiceClient("http://lineify.azurewebsites.net/", context);
        this.wayPointTable = this.serviceClient.getSyncTable("WayPoint", WayPoint.class);

        this.initLocalStore();
    }

    @Override
    public void persistWayPoints(List<WayPoint> wayPoints) {
        Log.i("WayPointStore", "persistWayPoints");

        try {
            for (WayPoint wayPoint : wayPoints) {
                this.wayPointTable.insert(wayPoint).get();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void syncWithBackend() {
        Log.i("WayPointStore", "syncWithBackend");

        if (!this.isConnected()) {
            return;
        }

        runAsync(() -> {
            MobileServiceSyncContext syncContext = this.serviceClient.getSyncContext();

            try {
                syncContext.push().get();
            } catch (Exception ex) {
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
                Map<String, ColumnDataType> tableDefinition = new HashMap<>();
                tableDefinition.put("id", ColumnDataType.String);
                tableDefinition.put("trackId", ColumnDataType.String);
                tableDefinition.put("created", ColumnDataType.Date);
                tableDefinition.put("altitude", ColumnDataType.Real);
                tableDefinition.put("longitude", ColumnDataType.Real);
                tableDefinition.put("latitude", ColumnDataType.Real);

                SQLiteLocalStore localStore = new SQLiteLocalStore(this.serviceClient.getContext(), "OfflineWayPointStore", null, 1);
                localStore.defineTable("WayPoint", tableDefinition);

                syncContext.initialize(localStore, new SimpleSyncHandler()).get();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        }

        return ni.isConnected();
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