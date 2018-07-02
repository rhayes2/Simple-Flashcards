package com.randomappsinc.simpleflashcards.managers;

import android.os.Handler;
import android.support.annotation.NonNull;

public class TimerManager {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;

    public interface Listener {

        // Returns the remaining time in a hh:mm:ss format
        void onTimeUpdated(String time);

        // Called when the time has ran out
        void onTimeUp();
    }

    @NonNull private Listener listener;
    protected int remainingSeconds;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            remainingSeconds--;
            renderRemainingSecondsAndSend();
            scheduleTimeUpdate();
        }
    };

    public TimerManager(@NonNull Listener listener, int remainingSeconds) {
        this.listener = listener;
        this.remainingSeconds = remainingSeconds;
    }

    public void startTimer() {
        renderRemainingSecondsAndSend();
        scheduleTimeUpdate();
    }

    public void resumeTimer() {
        scheduleTimeUpdate();
    }

    protected void scheduleTimeUpdate() {
        handler.postDelayed(updateTimeRunnable, 1000L);
    }

    public void stopTimer() {
        handler.removeCallbacks(updateTimeRunnable);
    }

    protected void renderRemainingSecondsAndSend() {
        int remainingHours = remainingSeconds / SECONDS_PER_HOUR;
        int withHoursRemoved = remainingSeconds % SECONDS_PER_HOUR;
        int remainingMinutes = withHoursRemoved / SECONDS_PER_MINUTE;
        int remainingSeconds = withHoursRemoved % SECONDS_PER_MINUTE;
        String remainingTimeText = String.valueOf(remainingHours)
                + ":" + String.valueOf(remainingMinutes)
                + ":" + String.valueOf(remainingSeconds);
        listener.onTimeUpdated(remainingTimeText);
    }
}
