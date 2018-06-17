package com.randomappsinc.simpleflashcards.managers;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.MyApplication;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechManager implements TextToSpeech.OnInitListener {

    private static TextToSpeechManager instance;

    private TextToSpeech textToSpeech;
    private boolean enabled = false;

    public static TextToSpeechManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized TextToSpeechManager getSync() {
        if (instance == null) {
            instance = new TextToSpeechManager();
        }
        return instance;
    }

    private TextToSpeechManager() {
        textToSpeech = new TextToSpeech(MyApplication.getAppContext(), this);
        textToSpeech.setLanguage(Locale.getDefault());
    }

    public void speak(String text) {
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sayTextPostL(text);
            } else {
                sayTextPreL(text);
            }
        } else {
            UIUtils.showLongToast(R.string.text_to_speech_fail);
        }
    }

    @SuppressWarnings("deprecation")
    private void sayTextPreL(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, this.hashCode() + "");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void sayTextPostL(String text) {
        String utteranceId = this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onInit(int status) {
        enabled = (status == TextToSpeech.SUCCESS);
    }

    public void stopSpeaking() {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }
}
