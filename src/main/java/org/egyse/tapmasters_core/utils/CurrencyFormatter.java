package org.egyse.tapmasters_core.utils;

public class CurrencyFormatter {
    private static final double[] DIVISORS = {
            1.0,            // 1
            1_000.0,        // K
            1_000_000.0,    // M
            1_000_000_000.0,// B
            1e12,           // T
            1e15,           // Q
            1e18,           // Qn
            1e21,           // Sx
            1e24,           // Sp
            1e27,           // Oc
            1e30,           // No
            1e33            // De
    };

    private static final String[] SUFFIXES = {
            "", "K", "M", "B", "T",
            "Q", "Qn", "Sx", "Sp", "Oc", "No", "De"
    };

    // Always show 2 decimal places for all tiers
    private static final int DECIMALS = 2;

    public String formatCurrency(double value) {
        if (value == 0) return "0.00"; // Handle zero case explicitly

        boolean negative = value < 0;
        double absValue = Math.abs(value);
        int magnitude = 0;

        // Find the largest magnitude we can use
        while (magnitude < DIVISORS.length - 1 &&
                absValue >= DIVISORS[magnitude + 1]) {
            magnitude++;
        }

        double divisor = DIVISORS[magnitude];
        double formattedValue = (negative ? -1 : 1) * (absValue / divisor);

        // Force 2 decimal places with trailing zeros
        return String.format("%,.2f%s", formattedValue, SUFFIXES[magnitude]);
    }
}