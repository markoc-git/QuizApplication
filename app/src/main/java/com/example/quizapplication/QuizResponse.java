package com.example.quizapplication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuizResponse {
    @SerializedName("results")
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
