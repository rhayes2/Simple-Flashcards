package com.randomappsinc.simpleflashcards.constants;

import android.support.annotation.IntDef;

@IntDef({
        FlashcardSetTransferState.NOT_YET_SENT,
        FlashcardSetTransferState.SENT,
})
public @interface FlashcardSetTransferState {
    int NOT_YET_SENT = 0;
    int SENT = 1;
}
