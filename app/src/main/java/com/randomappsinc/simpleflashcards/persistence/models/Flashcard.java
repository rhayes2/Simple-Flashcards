package com.randomappsinc.simpleflashcards.persistence.models;

import io.realm.RealmObject;

public class Flashcard extends RealmObject {

    private int id;
    private String term;
    private String definition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
