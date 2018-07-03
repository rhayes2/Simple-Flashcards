package com.randomappsinc.simpleflashcards.constants;

import android.support.annotation.IntDef;

@IntDef({
        TimerState.NEEDS_ACTIVATION,
        TimerState.RUNNING,
        TimerState.PAUSED,
})
public @interface TimerState {
    int NEEDS_ACTIVATION = 0;
    int RUNNING = 1;
    int PAUSED = 2;
}
