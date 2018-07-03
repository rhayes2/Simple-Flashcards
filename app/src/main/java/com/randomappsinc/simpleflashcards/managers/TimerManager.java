package com.randomappsinc.simpleflashcards.managers;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.randomappsinc.simpleflashcards.constants.TimerState;
import com.randomappsinc.simpleflashcards.utils.TimeUtils;

public class TimerManager {

    public interface Listener {

        // Returns the remaining time in a hh:mm:ss format
        void onTimeUpdated(String time);

        // Called when the time has ran out
        void onTimeUp();
    }

    @TimerState private int timerState = TimerState.NEEDS_ACTIVATION;
    protected Listener listener;
    protected int remainingSeconds;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            remainingSeconds--;
            if (remainingSeconds > 0) {
                scheduleTimeUpdate();
            } else {
                listener.onTimeUp();
            }
            renderRemainingSecondsAndSend();
        }
    };

    public TimerManager(@NonNull Listener listener, int remainingSeconds) {
        this.listener = listener;
        this.remainingSeconds = remainingSeconds;
    }

    public void resumeTimer() {
        if (timerState == TimerState.NEEDS_ACTIVATION) {
            renderRemainingSecondsAndSend();
            timerState = TimerState.RUNNING;
        }
        scheduleTimeUpdate();
    }

    protected void scheduleTimeUpdate() {
        handler.postDelayed(updateTimeRunnable, 1000L);
    }

    public void pauseTimer() {
        timerState = TimerState.PAUSED;
        handler.removeCallbacks(updateTimeRunnable);
    }

    protected void renderRemainingSecondsAndSend() {
        listener.onTimeUpdated(TimeUtils.getSecondsAsCountdown(remainingSeconds));
    }

    public void finish() {
        listener = null;
        handler.removeCallbacksAndMessages(null);
    }
}
