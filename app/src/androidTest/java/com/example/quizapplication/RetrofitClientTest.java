package com.example.quizapplication;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class RetrofitClientTest {
    RetrofitClient retrofitClient;

    @Before
    public void setUp(){
        retrofitClient = new RetrofitClient();
    }

    @Test
    public void testCreateService() {
        QuizApiService quizApiService = RetrofitClient.createService();

        assertNotNull(quizApiService);
    }
}
