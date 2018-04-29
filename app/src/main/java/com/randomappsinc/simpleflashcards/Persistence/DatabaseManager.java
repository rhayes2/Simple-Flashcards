package com.randomappsinc.simpleflashcards.persistence;

import android.support.annotation.NonNull;

import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DatabaseManager {

    private static final int CURRENT_REALM_VERSION = 1;

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
        Realm.init(MyApplication.getAppContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(CURRENT_REALM_VERSION)
                .migration(migration)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
    }

    private RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();

            // Support for dish tagging
            if (oldVersion == 0) {
                RealmObjectSchema setSchema = schema.get("FlashcardSet");
                if (setSchema != null) {
                    setSchema.removePrimaryKey();
                    setSchema.removeField("position");
                }
            }
        }
    };

    public void addFlashcardSet(String setName) {
        try {
            realm.beginTransaction();
            FlashcardSet set = new FlashcardSet();
            set.setName(setName);
            realm.copyToRealm(set);
            realm.commitTransaction();
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
                    .deleteFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
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
            FlashcardSet setToRemove = realm
                    .where(FlashcardSet.class)
                    .equalTo("name", setName)
                    .findFirst();
            setToRemove.deleteFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public List<String> getAllFlashcardSets() {
        List<FlashcardSet> flashcardSets = realm.where(FlashcardSet.class).findAll();
        List<String> setNames = new ArrayList<>();
        for (FlashcardSet flashcardSet : flashcardSets) {
            setNames.add(flashcardSet.getName());
        }
        return setNames;
    }

    public List<Flashcard> getAllFlashcards(String setName) {
        return realm.where(FlashcardSet.class)
                .equalTo("name", setName)
                .findFirst()
                .getFlashcards();
    }
}
