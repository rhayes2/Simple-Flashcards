package com.randomappsinc.simpleflashcards.persistence.models;

import io.realm.RealmObject;

public class Flashcard extends RealmObject {

    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
