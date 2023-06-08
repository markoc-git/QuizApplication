package com.example.quizapplication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class QuizApiServiceTest {
    @Mock
    QuizApiService quizApiService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetQuizQuestions() throws IOException {
        QuizResponse quizResponse = new QuizResponse();
        Call<QuizResponse> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedCall.execute()).thenReturn(Response.success(quizResponse));

        Mockito.when(quizApiService.getQuizQuestions(Mockito.anyInt())).thenReturn(mockedCall);

        Call<QuizResponse> call = quizApiService.getQuizQuestions(10);
        Response<QuizResponse> response = call.execute();

        assert response.isSuccessful();
        assert response.body() != null;
        assert response.body().equals(quizResponse);
    }
}
