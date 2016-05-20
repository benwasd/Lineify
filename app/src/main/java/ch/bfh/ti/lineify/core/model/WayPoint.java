package ch.bfh.ti.lineify.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class WayPoint implements Serializable {
    private UUID id;
    private UUID trackId;
    private Date created;
    private double altitude;
    private double longitude;
    private double latitude;
    private double accuracy;

    public WayPoint(UUID trackId, double altitude, double longitude, double latitude, double accuracy) {
        this.id = UUID.randomUUID();
        this.trackId = trackId;
        this.created = new Date();
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
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

    public double accuracy() {
        return this.accuracy;
    }
}
