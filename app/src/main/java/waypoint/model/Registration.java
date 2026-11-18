package waypoint.model;

import java.util.Objects;

/** VALUE OBJECT. Validated and normalised so an invalid instance cannot exist. */
public record Registration(String value) {
    
    // Compact constructor
    public Registration {
        // 1. requireNonNull
        Objects.requireNonNull(value, "Registration cannot be null");
        
        // 2. trim and upper-case (normalisation)
        value = value.trim().toUpperCase();
        
        // 3. validate format: two letters, two digits, one or two letters, four digits
        if (!value.matches("^[A-Z]{2}\\d{2}[A-Z]{1,2}\\d{4}$")) {
            throw new IllegalArgumentException("Invalid registration format: " + value);
        }
    }
}