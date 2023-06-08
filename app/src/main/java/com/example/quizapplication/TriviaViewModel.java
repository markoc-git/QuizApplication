package com.example.quizapplication;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class TriviaViewModel extends ViewModel {
    private final MutableLiveData<List<Question>> questionsLiveData;
    private final QuizApiService apiService;

    @Inject
    public TriviaViewModel() {
        questionsLiveData = new MutableLiveData<>();
        apiService = RetrofitClient.createService();
    }

    public LiveData<List<Question>> getQuestions() {
        return questionsLiveData;
    }

    public void fetchTriviaQuestions(int amount) {
        Call<QuizResponse> call = apiService.getQuizQuestions(amount);
        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                if (response.isSuccessful()) {
                    QuizResponse quizResponse = response.body();
                    if (quizResponse != null) {
                        List<Question> questions = quizResponse.getQuestions();
                        questionsLiveData.setValue(questions);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                // Handle API call failure
            }
        });
    }
}
