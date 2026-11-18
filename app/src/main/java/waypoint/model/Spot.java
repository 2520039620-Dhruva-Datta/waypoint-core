package waypoint.model;

import java.util.Objects;

/** ENTITY. Identity = id. Invariant: a spot cannot be assigned while occupied. */
public final class Spot {
    private final SpotId id;
    private VehicleId occupant;   // the ONLY mutable field

    // Constructor validating id
    public Spot(SpotId id) {
        this.id = Objects.requireNonNull(id, "SpotId cannot be null");
    }

    // true only if this call took it
    public boolean tryAssign(VehicleId v) {
        Objects.requireNonNull(v, "VehicleId cannot be null");
        
        if (!isFree()) {
            return false; // Cannot be assigned while occupied
        }
        
        this.occupant = v;
        return true;
    }

    // idempotent; releasing a free spot is not an error
    public void release(VehicleId v) {
        if (this.occupant != null && this.occupant.equals(v)) {
            this.occupant = null;
        }
    }

    public boolean isFree() {
        return this.occupant == null;
    }

    // equals/hashCode on id ALONE (entity identity, not state)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spot spot = (Spot) o;
        return id.equals(spot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}