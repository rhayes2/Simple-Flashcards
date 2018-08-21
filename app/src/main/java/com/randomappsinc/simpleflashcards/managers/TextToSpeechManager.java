package com.randomappsinc.simpleflashcards.managers;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechManager implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private boolean enabled;
    protected AudioManager audioManager;

    // Oreo audio focus shenanigans
    private AudioFocusRequest audioFocusRequest;

    public TextToSpeechManager(Context context) {
        textToSpeech = new TextToSpeech(context, this);
        textToSpeech.setLanguage(Locale.getDefault());
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeOAudioFocusParams();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initializeOAudioFocusParams() {
        AudioAttributes ttsAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(ttsAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener, new Handler())
                .build();
    }

    public void speak(String text) {
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestAudioFocusPostO(text);
            } else {
                requestAudioFocusPreO(text);
            }
        } else {
            UIUtils.showLongToast(R.string.text_to_speech_fail);
        }
    }

    private void playTts(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sayTextPostL(text);
        } else {
            sayTextPreL(text);
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

    @SuppressWarnings("deprecation")
    private void requestAudioFocusPreO(String text) {
        int result = audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            playTts(text);
        } else {
            UIUtils.showLongToast(R.string.text_to_speech_fail);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void requestAudioFocusPostO(String text) {
        int res = audioManager.requestAudioFocus(audioFocusRequest);
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            playTts(text);
        }
    }

    @Override
    public void onInit(int status) {
        enabled = (status == TextToSpeech.SUCCESS);
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setUtteranceListenerPost15();
            } else {
                setUtteranceListenerPre15();
            }
        }
    }

    @SuppressWarnings("SuppressWarnings")
    private void setUtteranceListenerPre15() {
        textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String utteranceId) {
                audioManager.abandonAudioFocus(audioFocusChangeListener);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setUtteranceListenerPost15() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                audioManager.abandonAudioFocus(audioFocusChangeListener);
            }

            @Override
            public void onError(String utteranceId) {
                UIUtils.showLongToast(R.string.text_to_speech_fail);
            }
        });
    }

    protected final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        stopSpeaking();
                    }
                }
            };

    public void stopSpeaking() {
        if (textToSpeech.isSpeaking()) {
            audioManager.abandonAudioFocus(audioFocusChangeListener);
            textToSpeech.stop();
        }
    }
}
