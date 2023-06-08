package com.example.quizapplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.internal.managers.ApplicationComponentManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@InstallIn(ApplicationComponentManager.class)
@Module
public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://opentdb.com/";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    @Provides
    @Singleton
    public static QuizApiService createService() {
        return getRetrofit().create(QuizApiService.class);
    }
}