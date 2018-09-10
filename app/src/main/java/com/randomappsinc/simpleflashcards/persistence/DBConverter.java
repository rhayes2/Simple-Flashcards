package com.randomappsinc.simpleflashcards.persistence;

import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class DBConverter {

    public static List<FlashcardSet> createDeepCopyList(List<FlashcardSet> flashcardSets) {
        List<FlashcardSet> newSets = new ArrayList<>();
        for (FlashcardSet flashcardSet : flashcardSets) {
            newSets.add(createDeepCopy(flashcardSet));
        }
        return newSets;
    }

    private static FlashcardSet createDeepCopy(FlashcardSet flashcardSet) {
        FlashcardSet newSet = new FlashcardSet();
        newSet.setId(flashcardSet.getId());
        newSet.setQuizletSetId(flashcardSet.getQuizletSetId());
        newSet.setName(flashcardSet.getName());

        RealmList<Flashcard> newCards = new RealmList<>();
        for (Flashcard flashcard : flashcardSet.getFlashcards()) {
            newCards.add(createDeepCopy(flashcard));
        }
        newSet.setFlashcards(newCards);
        return newSet;
    }

    private static Flashcard createDeepCopy(Flashcard flashcard) {
        Flashcard newCard = new Flashcard();
        newCard.setId(flashcard.getId());
        newCard.setTerm(flashcard.getTerm());
        newCard.setDefinition(flashcard.getDefinition());
        newCard.setTermImageUrl(flashcard.getTermImageUrl());
        return newCard;
    }
}
