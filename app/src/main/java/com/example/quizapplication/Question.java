package com.example.quizapplication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {
    @SerializedName("question")
    private String questionText;

    @SerializedName("incorrect_answers")
    private List<String> incorrectAnswers;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("category")
    private String category;

    @SerializedName("difficulty")
    private String difficulty;

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }
}