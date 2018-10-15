package com.randomappsinc.simpleflashcards.constants;

import android.support.annotation.IntDef;

@IntDef({
        QuestionType.MULTIPLE_CHOICE,
        QuestionType.FREE_FORM_INPUT
})
public @interface QuestionType {
    int MULTIPLE_CHOICE = 0;
    int FREE_FORM_INPUT = 1;
}
