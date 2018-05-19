package com.randomappsinc.simpleflashcards.constants;

import android.support.annotation.IntDef;

@IntDef({
        QuizScore.GOOD,
        QuizScore.OKAY,
        QuizScore.BAD,
})
public @interface QuizScore {
    int GOOD = 1;
    int OKAY = 2;
    int BAD = 3;
}
