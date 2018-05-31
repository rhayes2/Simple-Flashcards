package com.randomappsinc.simpleflashcards.api;

import android.os.Handler;
import android.os.HandlerThread;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuizletRestClient {

    private static QuizletRestClient instance;

    private QuizletService quizletService;
    private Handler handler;

    public static QuizletRestClient getInstance() {
        if (instance == null) {
            instance = new QuizletRestClient();
        }
        return instance;
    }

    private QuizletRestClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new QuizletAuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(QuizletApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        quizletService = retrofit.create(QuizletService.class);

        HandlerThread backgroundThread = new HandlerThread("");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());
    }
}
