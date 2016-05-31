package ch.bfh.ti.lineify.core.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

    public UUID id() {
        return this.id;
    }

    public UUID trackId() {
        return this.trackId;
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

    public boolean hasPlausibleAltitude() {
        return 400 < this.altitude() && this.altitude() < 10000;
    }

    public boolean isRelevant(List<WayPoint> nearbyPlausibleWayPoints) {
        double altitudeMedian = altitudeMedian(nearbyPlausibleWayPoints);
        double deviation = Math.abs(altitudeMedian - this.altitude());

        Log.d("WayPoint", String.format("WayPoint '%s', altitude='%.3f' accuracy='%.3f' deviation='%.3f' medianPoints='%d'", this.id().toString(), this.altitude(), this.accuracy(), deviation, nearbyPlausibleWayPoints.size()));

        return deviation < 15;
    }

    public static double getSwissAltitude(double wgs84Altitude) {
        // Constant value according to https://www.unavco.org/software/geodetic-utilities/geoid-height-calculator/geoid-height-calculator.html
        return wgs84Altitude - 48.893;
    }

    public static double averageForChart(List<WayPoint> wayPoints) {
        double sum = 0;
        for (WayPoint wayPoint : wayPoints) {
            sum += wayPoint.altitude();
        }

        return sum / wayPoints.size();
    }

    private static double altitudeMedian(List<WayPoint> wayPoints) {
        Collections.sort(new ArrayList<>(wayPoints), (a, b) -> Double.compare(a.altitude(), b.altitude()));

        int middle = wayPoints.size() / 2;
        if (wayPoints.size() % 2 == 1) {
            return wayPoints.get(middle).altitude();
        }
        else {
            return (wayPoints.get(middle-1).altitude() + wayPoints.get(middle).altitude()) / 2.0;
        }
    }
}