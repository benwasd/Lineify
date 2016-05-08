package ch.bfh.ti.lineify.core.model;

import java.util.Date;
import java.util.UUID;

public class WayPoint {
    private UUID id;
    private UUID trackId;
    private Date created;
    private double altitude;
    private double longitude;
    private double latitude;

    public WayPoint(UUID trackId, double altitude, double longitude, double latitude) {
        this.id = UUID.randomUUID();
        this.trackId = trackId;
        this.created = new Date();
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Date created() {
        return this.created;
    }

    public double altitude() {
        return this.altitude;
    }

    public double longitude() {
        return this.longitude;
    }

    public double latitude() {
        return this.latitude;
    }
}
