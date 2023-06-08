package com.example.quizapplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TriviaViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    QuizApiService quizApiService;

    @Mock
    Observer<List<Question>> observer;

    TriviaViewModel triviaViewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        triviaViewModel = new TriviaViewModel();
    }

    @Test
    public void testFetchTriviaQuestions() {
        QuizResponse quizResponse = new QuizResponse();
        List<Question> questions = new ArrayList<>();
        quizResponse.setQuestions(questions);

        Call<QuizResponse> mockedCall = Mockito.mock(Call.class);

        doAnswer(invocation -> {
            Callback<QuizResponse> callback = invocation.getArgument(0);
            callback.onResponse(mockedCall, Response.success(quizResponse));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        when(quizApiService.getQuizQuestions(anyInt())).thenReturn(mockedCall);
        triviaViewModel.getQuestions().observeForever(observer);
        triviaViewModel.fetchTriviaQuestions(10);
        verify(quizApiService).getQuizQuestions(10);
        verify(observer).onChanged(questions);
    }
}
