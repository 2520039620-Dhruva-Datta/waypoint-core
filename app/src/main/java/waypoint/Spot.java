package waypoint;

import java.util.Objects;

// Remove 'public' so Java allows it inside Spot.java
record SpotId(String value) {
    public SpotId {
        if (value == null || !value.matches("L[0-9]-[0-9]{1,3}")) {
            throw new IllegalArgumentException("Invalid SpotId format: " + value);
        }
    }
}

enum SpotType { COMPACT, LARGE, EV }
enum SpotStatus { FREE, RESERVED, OCCUPIED }

public final class Spot {
    private final SpotId id;
    private final SpotType type;
    private SpotStatus status = SpotStatus.FREE;

    public Spot(SpotId id, SpotType type) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
    }

    public void reserve() {
        if (status != SpotStatus.FREE) {
            throw new IllegalStateException("Cannot reserve spot in status: " + status);
        }
        this.status = SpotStatus.RESERVED;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Spot s && id.equals(s.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}