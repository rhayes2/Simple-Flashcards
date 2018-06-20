package com.randomappsinc.simpleflashcards.constants;

import android.support.annotation.IntDef;

@IntDef({
        FlashcardSetTransferState.NOT_YET_SENT,
        FlashcardSetTransferState.SENDING,
        FlashcardSetTransferState.SENT,
})
public @interface FlashcardSetTransferState {
    int NOT_YET_SENT = 0;
    int SENDING = 1;
    int SENT = 2;
}
