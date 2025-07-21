package org.egyse.tapmasters_core.utils;

public class CurrencyFormatter {
    private static final double[] DIVISORS = new double[72];
    private static final String[] SUFFIXES = new String[72];

    static {
        // Initialize first 12 tiers with original values
        double[] baseDivisors = {
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
        String[] baseSuffixes = {
                "", "K", "M", "B", "T",
                "Q", "Qn", "Sx", "Sp", "Oc", "No", "De"
        };

        // Copy base values
        System.arraycopy(baseDivisors, 0, DIVISORS, 0, 12);
        System.arraycopy(baseSuffixes, 0, SUFFIXES, 0, 12);

        // Hybrid naming system for tiers 12-71 (1e36 to 1e213)
        String[] hybridSuffixes = {
                // Groups 11-19 (undecillion to novemdecillion)
                "UDe", "DDe", "TDe", "QaDe", "QiDe", "SxDe", "SpDe", "OcDe", "NoDe",
                // Groups 20-29 (vigintillion to novemvigintillion)
                "Vi", "UVi", "DVi", "TVi", "QaVi", "QiVi", "SxVi", "SpVi", "OcVi", "NoVi",
                // Groups 30-39 (trigintillion to novemtrigintillion)
                "Tr", "UTr", "DTr", "TTr", "QaTr", "QiTr", "SxTr", "SpTr", "OcTr", "NoTr",
                // Groups 40-49 (quadragintillion to novemquadragintillion)
                "Qag", "UQag", "DQag", "TQag", "QaQag", "QiQag", "SxQag", "SpQag", "OcQag", "NoQag",
                // Groups 50-59 (quinquagintillion to novemquinquagintillion)
                "Qig", "UQig", "DQig", "TQig", "QaQig", "QiQig", "SxQig", "SpQig", "OcQig", "NoQig",
                // Groups 60-70 (sexagintillion to septuagintillion)
                "Seg", "USeg", "DSeg", "TSeg", "QaSeg", "QiSeg", "SxSeg", "SpSeg", "OcSeg", "NoSeg", "Stg"
        };

        // Populate divisors and suffixes
        for (int i = 12; i < 72; i++) {
            DIVISORS[i] = Math.pow(10, 3 * i);
            SUFFIXES[i] = hybridSuffixes[i - 12];
        }
    }

    // Formatting method remains unchanged
    public String formatCurrency(double value) {
        if (value == 0) return "0.00";

        boolean negative = value < 0;
        double absValue = Math.abs(value);
        int magnitude = 0;

        while (magnitude < DIVISORS.length - 1 &&
                absValue >= DIVISORS[magnitude + 1]) {
            magnitude++;
        }

        double divisor = DIVISORS[magnitude];
        double formattedValue = (negative ? -1 : 1) * (absValue / divisor);

        return String.format("%,.2f%s", formattedValue, SUFFIXES[magnitude]);
    }
}