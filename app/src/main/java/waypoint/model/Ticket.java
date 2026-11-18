package waypoint.model;

import java.time.Instant;
import java.util.Objects;

/** ENTITY. Invariants:
 *   I1  entry is always present
 *   I2  exit is null, or not before entry
 *   I3  fare is null, or not negative
 *   I4  fare exists only if exit exists
 *   I5  a closed ticket never reopens
 */
public final class Ticket {
    
    // The three final fields (never change after creation)
    private final TicketId id;
    private final SpotId spotId;
    private final Instant entry;

    // The two non-final fields (mutate exactly once during close)
    private Instant exit;
    private Money fare;

    // PRIVATE CONSTRUCTOR
    private Ticket(TicketId id, SpotId spotId, Instant entry) {
        this.id = Objects.requireNonNull(id, "TicketId cannot be null");
        this.spotId = Objects.requireNonNull(spotId, "SpotId cannot be null");
        this.entry = Objects.requireNonNull(entry, "Entry cannot be null");
    }

    // STATIC FACTORY
    public static Ticket open(TicketId id, SpotId spotId, Instant entry) {
        return new Ticket(id, spotId, entry);
    }

    // LIFECYCLE CHECK
    public boolean isOpen() {
        return this.exit == null;
    }

    // STATE TRANSITION
    public Money close(Instant at, FarePolicy policy) {
        Objects.requireNonNull(at, "Closing time cannot be null");
        Objects.requireNonNull(policy, "FarePolicy cannot be null");

        // I5: a closed ticket never reopens
        if (!isOpen()) {
            throw new IllegalStateException("TicketAlreadyClosed");
        }
        
        // I2: exit is not before entry
        if (at.isBefore(this.entry)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }

        // I4: set exit and fare together
        this.exit = at;
        this.fare = policy.fareFor(this.entry, this.exit, this.spotId); 
        
        return this.fare;
    }

    // EQUALS & HASHCODE: Based on identity (id) alone for Entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id.equals(ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}