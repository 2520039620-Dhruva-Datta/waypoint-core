package waypoint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

enum Currency {
    EUR(2), USD(2), JPY(0), KWD(3);
    final int minorUnits;
    Currency(int minorUnits) { this.minorUnits = minorUnits; }
}

final class Money {
    private final long minor;
    private final Currency currency;

    private Money(long minor, Currency currency) {
        if (minor < 0) throw new IllegalArgumentException("Money cannot be negative");
        this.minor = minor;
        this.currency = Objects.requireNonNull(currency);
    }

    public static Money of(String decimal, Currency c) {
        BigDecimal bd = new BigDecimal(decimal);
        if (bd.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (bd.scale() > c.minorUnits) {
            throw new IllegalArgumentException("Too many decimal places for " + c);
        }
        long minorUnits = bd.movePointRight(c.minorUnits).longValueExact();
        return new Money(minorUnits, c);
    }

    public Money plus(Money o) {
        requireSame(o);
        return new Money(Math.addExact(this.minor, o.minor), this.currency);
    }

    public Money times(Quantity q) {
        return new Money(Math.multiplyExact(this.minor, q.value()), this.currency);
    }

    public Money minus(Percentage p) {
        BigDecimal current = BigDecimal.valueOf(this.minor);
        BigDecimal discount = current.multiply(BigDecimal.valueOf(p.basisPoints()))
                                     .divide(BigDecimal.valueOf(10_000), 0, RoundingMode.HALF_EVEN);
        long result = Math.max(0, this.minor - discount.longValue());
        return new Money(result, this.currency);
    }

    private void requireSame(Money o) {
        if (this.currency != o.currency) {
            throw new IllegalArgumentException("Currency mismatch: " + this.currency + " vs " + o.currency);
        }
    }

    @Override
    public String toString() {
        return BigDecimal.valueOf(minor).movePointLeft(currency.minorUnits) + " " + currency;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Money m) && minor == m.minor && currency == m.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minor, currency);
    }
}

final class Percentage {
    private final int basisPoints; // 0 to 10000 (100% = 10000)

    private Percentage(int basisPoints) {
        this.basisPoints = basisPoints;
    }

    public static Percentage ofBasisPoints(int bp) {
        if (bp < 0 || bp > 10_000) {
            throw new IllegalArgumentException("Percentage must be between 0% and 100%");
        }
        return new Percentage(bp);
    }

    public int basisPoints() { return basisPoints; }

    public Money applyTo(Money price) {
        return price.minus(this);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Percentage p) && basisPoints == p.basisPoints;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(basisPoints);
    }
}

final class Quantity {
    private final int value;

    private Quantity(int value) {
        this.value = value;
    }

    public static Quantity of(int v) {
        if (v < 1) throw new IllegalArgumentException("Quantity must be at least 1");
        return new Quantity(v);
    }

    public int value() { return value; }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Quantity q) && value == q.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}

// Run this main method to demonstrate to your class!
public class EcommerceDemo {
    public static void main(String[] args) {
        Money price = Money.of("100.00", Currency.USD);
        Percentage discount = Percentage.ofBasisPoints(2000); // 20%
        Quantity qty = Quantity.of(2);

        Money discountedPrice = discount.applyTo(price);
        Money total = discountedPrice.times(qty);

        System.out.println("Original Price: " + price);
        System.out.println("After 20% Discount: " + discountedPrice);
        System.out.println("Total for 2 items: " + total);

        // Uncomment below to show students how illegal states are blocked!
        // Money euros = Money.of("50.00", Currency.EUR);
        // price.plus(euros); // Crashes with Currency mismatch!
    }
}