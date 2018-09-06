package com.randomappsinc.simpleflashcards.utils;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;

import io.realm.Realm;

public final class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Iconify.with(new IoniconsModule())
                .with(new FontAwesomeModule());
    }
}
