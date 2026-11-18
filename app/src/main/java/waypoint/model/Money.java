package waypoint.model;

import java.util.*;

/** VALUE OBJECT. Exact money. TODO: make every test in MoneyTest pass. */
public final class Money implements Comparable<Money> {
    
    // FIELDS: paise as long, plus a Currency. Both final.
    private final long paise;
    private final Currency currency;

    // PRIVATE CONSTRUCTOR
    private Money(long paise, Currency currency) {
        this.paise = paise;
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
    }

    // STATIC FACTORIES
    public static Money ofMinor(long paise, Currency currency) {
        return new Money(paise, currency);
    }

    public static Money rupees(long r) {
        // 1 Rupee = 100 paise
        return new Money(r * 100, Currency.getInstance("INR"));
    }

    // MATH OPERATIONS
    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(this.paise + other.paise, this.currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(this.paise - other.paise, this.currency);
    }

    public Money times(int n) {
        return new Money(this.paise * n, this.currency);
    }

    public boolean isNegative() {
        return this.paise < 0;
    }

    // ALLOCATION (From Course Section 3.5)
    // Splits this amount into n shares whose sum is EXACTLY this amount.
    public List<Money> allocate(int n) {
        if (n <= 0) throw new IllegalArgumentException("Must allocate into at least 1 share");
        
        long base = paise / n;
        long rem = paise % n;
        
        List<Money> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            // Distribute the remainder iteratively to the first few shares
            out.add(new Money(base + (i < rem ? 1 : 0), currency));
        }
        return List.copyOf(out);
    }

    // COMPARABLE
    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return Long.compare(this.paise, other.paise);
    }

    // HELPER: Prevent mixing currencies (e.g. adding USD to INR)
    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + this.currency + " vs " + other.currency);
        }
    }

    // EQUALS, HASHCODE (Value Object semantics - Section 4.2)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return paise == money.paise && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paise, currency);
    }

    // TOSTRING (Strictly avoiding floats/doubles for display format)
    @Override
    public String toString() {
        long major = paise / 100;
        long minor = Math.abs(paise % 100);
        return currency.getCurrencyCode() + " " + major + "." + (minor < 10 ? "0" : "") + minor;
    }
}