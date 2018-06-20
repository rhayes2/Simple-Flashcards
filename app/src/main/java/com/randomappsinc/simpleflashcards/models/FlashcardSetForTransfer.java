package com.randomappsinc.simpleflashcards.models;

import com.randomappsinc.simpleflashcards.constants.FlashcardSetTransferState;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

public class FlashcardSetForTransfer {

    private FlashcardSet flashcardSet;
    @FlashcardSetTransferState private int transferState = FlashcardSetTransferState.NOT_YET_SENT;

    public FlashcardSetForTransfer(FlashcardSet flashcardSet) {
        this.flashcardSet = flashcardSet;
    }

    public FlashcardSet getFlashcardSet() {
        return flashcardSet;
    }

    public int getTransferState() {
        return transferState;
    }

    public void setTransferState(int transferState) {
        this.transferState = transferState;
    }
}
