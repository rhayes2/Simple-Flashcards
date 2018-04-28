package com.randomappsinc.simpleflashcards.persistence.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FlashcardSet extends RealmObject {

    @PrimaryKey
    private String name;

    private RealmList<Flashcard> flashcards;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(RealmList<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }
}
