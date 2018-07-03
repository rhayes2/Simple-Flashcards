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

    @TimerState protected int timerState = TimerState.NEEDS_ACTIVATION;
    protected Listener listener;
    private int initialSeconds;
    protected int remainingSeconds;

    private Handler handler = new Handler();
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            remainingSeconds--;
            if (remainingSeconds > 0) {
                scheduleTimeUpdate();
            } else {
                timerState = TimerState.FINISHED;
                listener.onTimeUp();
            }
            renderRemainingSecondsAndSend();
        }
    };

    public TimerManager(@NonNull Listener listener, int initialSeconds) {
        this.listener = listener;
        this.initialSeconds = initialSeconds;
        this.remainingSeconds = initialSeconds;
    }

    public void resumeTimer() {
        if (timerState == TimerState.RUNNING || timerState == TimerState.FINISHED) {
            return;
        }

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
        if (timerState != TimerState.RUNNING) {
            return;
        }

        timerState = TimerState.PAUSED;
        handler.removeCallbacks(updateTimeRunnable);
    }

    public void stopTimer() {
        timerState = TimerState.FINISHED;
        handler.removeCallbacks(updateTimeRunnable);
    }

    protected void renderRemainingSecondsAndSend() {
        listener.onTimeUpdated(TimeUtils.getSecondsAsCountdown(remainingSeconds));
    }

    public void resetAndStart() {
        handler.removeCallbacksAndMessages(null);
        timerState = TimerState.NEEDS_ACTIVATION;
        remainingSeconds = initialSeconds;
        resumeTimer();
    }

    public void finish() {
        listener = null;
        handler.removeCallbacksAndMessages(null);
    }
}
