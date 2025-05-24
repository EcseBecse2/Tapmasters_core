package org.egyse.tapmasters_core.utils;

public class TimerUtil {
    public String formatTime(int s) {
        return s < 3600 ?
                String.format("%d:%02d", s/60, s%60) :
                String.format("%d:%02d:%02d", s/3600, (s/60)%60, s%60);
    }
}
