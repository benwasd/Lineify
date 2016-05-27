package ch.bfh.ti.lineify.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Track implements Serializable {
    private UUID id;
    private String userEmail;
    private String identifier;

    public Track(String userEmail, String identifier) {
        this.id = UUID.randomUUID();
        this.userEmail = userEmail;
        this.identifier = identifier;
    }

    public UUID id() {
        return this.id;
    }

    public String userEmail() {
        return this.userEmail;
    }

    public String identifier() {
        return this.identifier;
    }

    public List<WayPoint> getWayPoints() {
        throw new Error("Not implemented");
    }
}