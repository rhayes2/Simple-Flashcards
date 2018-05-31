package com.randomappsinc.simpleflashcards.api;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class QuizletAuthInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request
                .url()
                .newBuilder()
                .addQueryParameter(QuizletApiConstants.CLIENT_ID_KEY, QuizletAuthConstants.CLIENT_ID)
                .build();
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
