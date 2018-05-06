package com.randomappsinc.simpleflashcards.persistence.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class FlashcardSet extends RealmObject {

    private int id;

    @Required
    private String name;

    private RealmList<Flashcard> flashcards;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
