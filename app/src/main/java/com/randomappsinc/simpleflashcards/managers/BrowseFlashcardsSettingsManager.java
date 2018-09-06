package com.randomappsinc.simpleflashcards.managers;

import java.util.ArrayList;
import java.util.List;

public class BrowseFlashcardsSettingsManager {

    public interface Listener {
        void onDefaultSideChanged(boolean showTermsByDefault);
    }

    private static BrowseFlashcardsSettingsManager instance;

    public static BrowseFlashcardsSettingsManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized BrowseFlashcardsSettingsManager getSync() {
        if (instance == null) {
            instance = new BrowseFlashcardsSettingsManager();
        }
        return instance;
    }

    private boolean showTermsByDefault = true;
    private List<Listener> listeners = new ArrayList<>();

    private BrowseFlashcardsSettingsManager() {}

    public boolean getShowTermsByDefault() {
        return showTermsByDefault;
    }

    public void toggleDefaultSide() {
        showTermsByDefault = !showTermsByDefault;
        for (Listener listener : listeners) {
            listener.onDefaultSideChanged(showTermsByDefault);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void shutdown() {
        showTermsByDefault = true;
        listeners.clear();
    }
}
