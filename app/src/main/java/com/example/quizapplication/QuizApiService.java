package com.example.quizapplication;


import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@Singleton
public interface QuizApiService {
    @GET("api.php")
    Call<QuizResponse> getQuizQuestions(
            @Query("amount") int amount
    );
}
