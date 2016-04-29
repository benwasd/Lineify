package ch.bfh.ti.lineify.core.model;

import java.util.Date;

public class WayPoint {
    private final Date timestamp;
    private final double altitude;
    private final double longitude;
    private final double latitude;

    public WayPoint(Date timestamp, double altitude, double longitude, double latitude) {
        this.timestamp = timestamp;
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Date timestamp() {
        return this.timestamp;
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
