package org.egyse.tapmasters_core.utils;

public class TimerUtil {
    public String formatTime(int s) {
        return s < 3600 ?
                String.format("%d:%02d", s/60, s%60) :
                String.format("%d:%02d:%02d", s/3600, (s/60)%60, s%60);
    }

    public String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        if (totalSeconds < 0) totalSeconds = 0;  // Ensure non-negative

        if (totalSeconds < 3600) {
            // Format as MM:SS
            return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
        } else {
            // Format as HH:MM:SS
            return String.format("%d:%02d:%02d",
                    totalSeconds / 3600,
                    (totalSeconds % 3600) / 60,
                    totalSeconds % 60);
        }
    }
}
