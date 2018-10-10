package com.randomappsinc.simpleflashcards.utils;

import android.view.View;
import android.view.ViewTreeObserver;

public class ViewUtils {

    public static void runOnPreDraw(final View view, final Runnable runnable) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                runnable.run();
                return false;
            }
        });
    }
}
