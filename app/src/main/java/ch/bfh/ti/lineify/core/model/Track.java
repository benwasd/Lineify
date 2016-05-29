package ch.bfh.ti.lineify.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class Track implements Serializable {
    private UUID id;
    private Date created;
    private String userEmail;
    private String identifier;
    private int wayPointCount;

    public Track(String userEmail, String identifier) {
        this.id = UUID.randomUUID();
        this.created = new Date();
        this.userEmail = userEmail;
        this.identifier = identifier;
    }

    public UUID id() {
        return this.id;
    }

    public Date created() {
        return this.created;
    }

    public String userEmail() {
        return this.userEmail;
    }

    public String identifier() {
        return this.identifier;
    }

    public static String defaultIdentifier() {
        return String.format("Line vom %1$td.%1$tm.%1$tY %1$tH:%1$tm", new GregorianCalendar());
    }

    public int wayPointCount() {
        return this.wayPointCount;
    }

    public void incrementWayPointCount() {
        this.wayPointCount++;
    }
}