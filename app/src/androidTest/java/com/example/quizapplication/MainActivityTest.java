package com.example.quizapplication;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    private Context mockContext;
    @Mock
    private ConnectivityManager mockConnectivityManager;
    @Mock
    private NetworkInfo mockNetworkInfo;
    @Mock
    private TriviaViewModel mockViewModel;
    @Mock
    private LiveData<List<Question>> mockLiveData;
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        mainActivity = Mockito.spy(MainActivity.class);
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(true);
    }

    @Test
    public void testStartNewGame() {
        when(mainActivity.isNetworkConnected()).thenReturn(true);
        when(mockViewModel.getQuestions()).thenReturn(mockLiveData);
        when(mockLiveData.getValue()).thenReturn(getDummyQuestions());

        mainActivity.startNewGame();



        Mockito.verify(mockViewModel).fetchTriviaQuestions(10);
    }

    @Test
    public void testCheckInternetConnection() {
        when(mainActivity.isNetworkConnected()).thenReturn(true);

        mainActivity.checkInternetConnection();

    }

    @Test
    public void testIsNetworkConnected() {
        boolean isConnected = mainActivity.isNetworkConnected();

        assertTrue(isConnected);
    }

    // Helper method to create a list of dummy questions for testing
    private List<Question> getDummyQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Question 1", "Answer 1", "Easy"));
        questions.add(new Question("Question 2", "Answer 2", "Medium"));
        questions.add(new Question("Question 3", "Answer 3", "Hard"));
        return questions;
    }
}
