package com.randomappsinc.simpleflashcards.utils;

import android.text.TextUtils;

import com.randomappsinc.simpleflashcards.persistence.models.Flashcard;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;

public class JSONUtils {

    private static final String QUIZLET_SET_ID_KEY = "quizletSetId";
    private static final String NAME_KEY = "name";
    private static final String FLASHCARDS_KEY = "flashcards";
    private static final String TERM_KEY = "term";
    private static final String TERM_IMAGE_URL_KEY = "termImageUrl";
    private static final String DEFINITION_KEY = "definition";

    public static String serializeFlashcardSet(FlashcardSet flashcardSet) {
        try {
            JSONObject flashcardSetJson = new JSONObject();
            flashcardSetJson.put(QUIZLET_SET_ID_KEY, flashcardSet.getQuizletSetId());
            flashcardSetJson.put(NAME_KEY, flashcardSet.getName());

            JSONArray flashcards = new JSONArray();
            for (Flashcard flashcard : flashcardSet.getFlashcards()) {
                JSONObject flashcardJson = new JSONObject();
                flashcardJson.put(TERM_KEY, flashcard.getTerm());
                flashcardJson.put(TERM_IMAGE_URL_KEY, flashcard.getTermImageUrl());
                flashcardJson.put(DEFINITION_KEY, flashcard.getDefinition());
                flashcards.put(flashcardJson);
            }
            flashcardSetJson.put(FLASHCARDS_KEY, flashcards);

            return flashcardSetJson.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public static FlashcardSet deserializeFlashcardSet(String flashcardSetJsonText) {
        if (TextUtils.isEmpty(flashcardSetJsonText)) {
            return null;
        }

        FlashcardSet flashcardSet = new FlashcardSet();
        try {
            JSONObject flashcardSetJson = new JSONObject(flashcardSetJsonText);
            flashcardSet.setQuizletSetId(flashcardSetJson.getLong(QUIZLET_SET_ID_KEY));
            flashcardSet.setName(flashcardSetJson.getString(NAME_KEY));

            RealmList<Flashcard> flashcards = new RealmList<>();
            JSONArray flashcardsArray = flashcardSetJson.getJSONArray(FLASHCARDS_KEY);
            for (int i = 0; i < flashcardsArray.length(); i++) {
                JSONObject flashcardJson = flashcardsArray.getJSONObject(i);
                Flashcard flashcard = new Flashcard();
                flashcard.setTerm(flashcardJson.getString(TERM_KEY));
                flashcard.setTermImageUrl(flashcardJson.getString(TERM_IMAGE_URL_KEY));
                flashcard.setDefinition(flashcardJson.getString(DEFINITION_KEY));
                flashcards.add(flashcard);
            }
            flashcardSet.setFlashcards(flashcards);
        }
        catch (JSONException ignored) {}
        return flashcardSet;
    }
}
