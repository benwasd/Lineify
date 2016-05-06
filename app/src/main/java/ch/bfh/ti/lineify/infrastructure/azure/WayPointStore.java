package ch.bfh.ti.lineify.infrastructure.azure;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;

import java.net.MalformedURLException;
import java.util.List;

import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class WayPointStore implements IWayPointStore {
    private final Context context;
    private final MobileServiceClient serviceClient;
    private final MobileServiceSyncTable<WayPoint> wayPointTable;

    public WayPointStore(Context context) throws MalformedURLException {
        this.context = context;
        this.serviceClient = new MobileServiceClient("https://lineify.azurewebsites.net/", context);
        this.wayPointTable = this.serviceClient.getSyncTable("WayPoint", WayPoint.class);
    }

    @Override
    public void persistWayPoints(List<WayPoint> wayPoints) {

    }
}