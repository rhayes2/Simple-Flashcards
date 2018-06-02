package com.randomappsinc.simpleflashcards.persistence;

import android.support.annotation.NonNull;

import com.randomappsinc.simpleflashcards.api.models.QuizletFlashcard;
import com.randomappsinc.simpleflashcards.api.models.QuizletFlashcardSet;
import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.MyApplication;

import java.util.List;

import io.realm.Case;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DatabaseManager {

    private static final int CURRENT_REALM_VERSION = 4;

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
    protected boolean idMigrationNeeded;

    private DatabaseManager() {
        Realm.init(MyApplication.getAppContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(CURRENT_REALM_VERSION)
                .migration(migration)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();

        if (idMigrationNeeded) {
            addIdsToEverything();
        }
    }

    private final RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();

            // Remove flashcard positioning
            if (oldVersion == 0) {
                RealmObjectSchema setSchema = schema.get("FlashcardSet");
                if (setSchema != null) {
                    setSchema.removePrimaryKey();
                    setSchema.removeField("position");
                }
                oldVersion++;
            }

            // Add IDs to objects
            if (oldVersion == 1) {
                RealmObjectSchema setSchema = schema.get("FlashcardSet");
                if (setSchema != null) {
                    setSchema.addField("id", int.class);
                } else {
                    throw new IllegalStateException("FlashcardSet schema doesn't exist.");
                }
                RealmObjectSchema cardSchema = schema.get("Flashcard");
                if (cardSchema != null) {
                    cardSchema.addField("id", int.class);
                } else {
                    throw new IllegalStateException("Flashcard schema doesn't exist.");
                }
                idMigrationNeeded = true;
                oldVersion++;
            }

            // Rename "question" and "answer" to "term" and "definition"
            if (oldVersion == 2) {
                RealmObjectSchema cardSchema = schema.get("Flashcard");
                if (cardSchema != null) {
                    cardSchema.renameField("question", "term");
                    cardSchema.renameField("answer", "definition");
                } else {
                    throw new IllegalStateException("Flashcard schema doesn't exist.");
                }
                oldVersion++;
            }

            // Rename "question" and "answer" to "term" and "definition"
            if (oldVersion == 3) {
                RealmObjectSchema setSchema = schema.get("FlashcardSet");
                if (setSchema != null) {
                    setSchema.addField("quizletSetId", long.class);
                } else {
                    throw new IllegalStateException("FlashcardSet schema doesn't exist.");
                }
                RealmObjectSchema cardSchema = schema.get("Flashcard");
                if (cardSchema != null) {
                    cardSchema.addField("termImageUrl", String.class);
                } else {
                    throw new IllegalStateException("Flashcard schema doesn't exist.");
                }
            }
        }
    };

    private void addIdsToEverything() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                List<FlashcardSet> flashcardSets = realm.where(FlashcardSet.class).findAll();
                for (FlashcardSet flashcardSet : flashcardSets) {
                    flashcardSet.setId(getNextSetId());
                    for (Flashcard flashcard : flashcardSet.getFlashcards()) {
                        flashcard.setId(getNextFlashcardId());
                    }
                }
            }
        });
    }

    public int addFlashcardSet(String setName) {
        try {
            realm.beginTransaction();
            FlashcardSet set = new FlashcardSet();
            int newSetId = getNextSetId();
            set.setId(newSetId);
            set.setName(setName);
            realm.copyToRealm(set);
            realm.commitTransaction();
            return newSetId;
        } catch (Exception e) {
            realm.cancelTransaction();
        }
        return 0;
    }

    protected int getNextSetId() {
        Number number = realm.where(FlashcardSet.class).findAll().max("id");
        return number == null ? 1 : number.intValue() + 1;
    }

    protected int getNextFlashcardId() {
        Number number = realm.where(Flashcard.class).findAll().max("id");
        return number == null ? 1 : number.intValue() + 1;
    }

    public void renameSet(int setId, String newName) {
        try {
            realm.beginTransaction();
            FlashcardSet set = realm.where(FlashcardSet.class)
                    .equalTo("id", setId)
                    .findFirst();
            set.setName(newName);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void addFlashcard(int setId, String term, String definition) {
        try {
            realm.beginTransaction();
            FlashcardSet set = realm.where(FlashcardSet.class).equalTo("id", setId).findFirst();
            Flashcard flashcard = new Flashcard();
            flashcard.setId(getNextFlashcardId());
            flashcard.setTerm(term);
            flashcard.setDefinition(definition);
            set.getFlashcards().add(flashcard);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void updateFlashcardTerm(int flashcardId, String newTerm) {
        try {
            realm.beginTransaction();
            Flashcard flashcard = realm.where(Flashcard.class)
                    .equalTo("id", flashcardId)
                    .findFirst();
            flashcard.setTerm(newTerm);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void updateFlashcardDefinition(int flashcardId, String newDefinition) {
        try {
            realm.beginTransaction();
            Flashcard flashcard = realm.where(Flashcard.class)
                    .equalTo("id", flashcardId)
                    .findFirst();
            flashcard.setDefinition(newDefinition);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void deleteFlashcard(int flashcardId) {
        try {
            realm.beginTransaction();
            Flashcard flashcard = realm.where(Flashcard.class).equalTo("id", flashcardId).findFirst();
            flashcard.deleteFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void deleteFlashcardSet(int setId) {
        try {
            realm.beginTransaction();
            FlashcardSet setToRemove = realm
                    .where(FlashcardSet.class)
                    .equalTo("id", setId)
                    .findFirst();
            setToRemove.deleteFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public List<FlashcardSet> getFlashcardSets(String searchTerm) {
        List<FlashcardSet> flashcardSets;
        if (searchTerm.trim().isEmpty()) {
            flashcardSets = realm.where(FlashcardSet.class).findAll();
        } else {
            flashcardSets = realm
                    .where(FlashcardSet.class)
                    .contains("name", searchTerm, Case.INSENSITIVE)
                    .findAll();
        }
        return flashcardSets;
    }

    public List<Flashcard> getAllFlashcards(int setId) {
        return realm.where(FlashcardSet.class)
                .equalTo("id", setId)
                .findFirst()
                .getFlashcards();
    }

    public String getSetName(int setId) {
        return realm.where(FlashcardSet.class)
                .equalTo("id", setId)
                .findFirst()
                .getName();
    }

    public Flashcard getFlashcard(int cardId) {
        return realm.where(Flashcard.class)
                .equalTo("id", cardId)
                .findFirst();
    }

    public FlashcardSet getFlashcardSet(int setId) {
        return realm.where(FlashcardSet.class)
                .equalTo("id", setId)
                .findFirst();
    }

    public int getNumFlashcardSets() {
        return realm.where(FlashcardSet.class)
                .findAll()
                .size();
    }

    public void saveQuizletSet(QuizletFlashcardSet quizletFlashcardSet) {
        try {
            realm.beginTransaction();

            FlashcardSet set = new FlashcardSet();
            int newSetId = getNextSetId();
            set.setId(newSetId);
            set.setQuizletSetId(quizletFlashcardSet.getQuizletSetId());
            set.setName(quizletFlashcardSet.getTitle());

            RealmList<Flashcard> flashcards = new RealmList<>();
            int flashcardId = getNextFlashcardId();
            for (QuizletFlashcard quizletFlashcard : quizletFlashcardSet.getFlashcards()) {
                Flashcard flashcard = new Flashcard();
                flashcard.setId(flashcardId++);
                flashcard.setTerm(quizletFlashcard.getTerm());
                flashcard.setDefinition(quizletFlashcard.getDefinition());
                flashcard.setTermImageUrl(quizletFlashcard.getImageUrl());
                flashcards.add(flashcard);
            }
            set.setFlashcards(flashcards);

            realm.copyToRealm(set);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public boolean alreadyHasQuizletSet(long quizletSetId) {
        FlashcardSet set = realm
                .where(FlashcardSet.class)
                .equalTo("quizletSetId", quizletSetId)
                .findFirst();
        return set != null;
    }
}
