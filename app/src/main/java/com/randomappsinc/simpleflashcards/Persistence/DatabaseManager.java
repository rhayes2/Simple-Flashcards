package com.randomappsinc.simpleflashcards.Persistence;

import android.content.Context;

import com.randomappsinc.simpleflashcards.Persistence.DataObjects.Flashcard;
import com.randomappsinc.simpleflashcards.Persistence.DataObjects.FlashcardSet;
import com.randomappsinc.simpleflashcards.Utils.MyApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by alexanderchiou on 11/20/15.
 */
public class DatabaseManager {
    private static DatabaseManager instance;

    public static DatabaseManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized DatabaseManager getSync() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private Realm realm;

    private DatabaseManager() {
        Context context = MyApplication.get().getApplicationContext();
        realm = Realm.getInstance(context);
    }

    public void addFlashcardSet(String setName, int position) {
        try {
            realm.beginTransaction();
            FlashcardSet set = new FlashcardSet();
            set.setName(setName);
            set.setPosition(position);
            realm.copyToRealm(set);
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void renameSet(String oldName, String newName) {
        try {
            realm.beginTransaction();
            FlashcardSet set = realm.where(FlashcardSet.class)
                    .equalTo("name", oldName)
                    .findFirst();
            set.setName(newName);
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void swapSetsAtPositions(int positionOne, int positionTwo) {
        try {
            realm.beginTransaction();
            FlashcardSet setOne = realm.where(FlashcardSet.class).equalTo("position", positionOne).findFirst();
            FlashcardSet setTwo = realm.where(FlashcardSet.class).equalTo("position", positionTwo).findFirst();
            setOne.setPosition(positionTwo);
            setTwo.setPosition(positionOne);
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public boolean doesSetExist(String setName) {
        return realm.where(FlashcardSet.class).equalTo("name", setName).findFirst() != null;
    }

    public void addFlashcard(String question, String answer, String setName) {
        try {
            realm.beginTransaction();
            FlashcardSet set = realm.where(FlashcardSet.class).equalTo("name", setName).findFirst();
            Flashcard flashcard = new Flashcard();
            flashcard.setQuestion(question);
            flashcard.setAnswer(answer);
            set.getFlashcards().add(flashcard);
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void updateFlashcard(String oldQuestion, String oldAnswer, String newQuestion,
                                String newAnswer, String setName) {
        try {
            realm.beginTransaction();
            Flashcard flashcard = realm.where(FlashcardSet.class)
                    .equalTo("name", setName)
                    .findFirst()
                    .getFlashcards()
                    .where()
                    .equalTo("question", oldQuestion)
                    .equalTo("answer", oldAnswer)
                    .findFirst();
            flashcard.setQuestion(newQuestion);
            flashcard.setAnswer(newAnswer);
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void deleteFlashcard(String question, String answer, String setName) {
        try {
            realm.beginTransaction();
            FlashcardSet set = realm.where(FlashcardSet.class).equalTo("name", setName).findFirst();
            set.getFlashcards().where()
                    .equalTo("question", question)
                    .equalTo("answer", answer)
                    .findFirst()
                    .removeFromRealm();
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public boolean doesFlashcardExist(String setName, String question, String answer) {
        FlashcardSet set = realm.where(FlashcardSet.class).equalTo("name", setName).findFirst();
        return set.getFlashcards().where()
                .equalTo("question", question)
                .equalTo("answer", answer)
                .findFirst() != null;
    }

    public void deleteFlashcardSet(String setName) {
        try {
            realm.beginTransaction();
            FlashcardSet setToRemove = realm.where(FlashcardSet.class).equalTo("name", setName).findFirst();
            Map<String, Integer> mappings = getNewPositions(setToRemove.getPosition());
            setToRemove.removeFromRealm();
            realm.commitTransaction();
            redoPositions(mappings);
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    private Map<String, Integer> getNewPositions(int deletedPosition) {
        Map<String, Integer> newPositions = new HashMap<>();
        List<FlashcardSet> lowerSets = realm.where(FlashcardSet.class)
                .greaterThan("position", deletedPosition)
                .findAll();
        for (FlashcardSet lowerSet : lowerSets) {
            newPositions.put(lowerSet.getName(), lowerSet.getPosition());
        }
        return newPositions;
    }

    private void redoPositions(Map<String, Integer> mappings) {
        try {
            realm.beginTransaction();
            for (String setName : mappings.keySet()) {
                FlashcardSet set = realm.where(FlashcardSet.class).equalTo("name", setName).findFirst();
                set.setPosition(mappings.get(setName) - 1);
            }
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public List<FlashcardSet> getAllFlashcardSets() {
        return realm.where(FlashcardSet.class).findAll();
    }

    public List<Flashcard> getAllFlashcards(String setName) {
        return realm.where(FlashcardSet.class)
                .equalTo("name", setName)
                .findFirst()
                .getFlashcards();
    }
}
