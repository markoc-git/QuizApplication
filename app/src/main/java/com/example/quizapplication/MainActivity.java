package com.example.quizapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_INDEX = "current_index";
    private static final String KEY_SCORE = "score";
    private static final String KEY_USED_ANSWER_OPTIONS = "used_answer_options";
    private static final String KEY_PREVIOUS_ANSWERS = "previous_answers";

    private TextView questionTextView;
    private TextView questionNumberTextView;
    private TextView categoryTextView;
    private RadioGroup answerOptionsRadioGroup;
    private Button nextButton;
    private Button finishButton;
    private TextView difficultyTextView;
    private ProgressBar loadingProgressBar;
    private TextView timerTextView;
    private TextView noInternetTextView;

    private int currentIndex = 0;
    private int score = 0;
    private List<Question> allQuestions;
    private List<Question> easyQuestions;
    private List<Question> mediumQuestions;
    private List<Question> hardQuestions;
    private List<String> previousAnswers;
    private Set<String> usedAnswerOptions;
    private CountDownTimer timer;
    private int secondsLeft = 20;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TriviaViewModel viewModel = new ViewModelProvider(this).get(TriviaViewModel.class);

        questionTextView = findViewById(R.id.questionTextView);
        questionNumberTextView = findViewById(R.id.questionNumberTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        answerOptionsRadioGroup = findViewById(R.id.answerOptionsRadioGroup);
        nextButton = findViewById(R.id.nextButton);
        finishButton = findViewById(R.id.finishButton);
        difficultyTextView = findViewById(R.id.difficultyTextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        timerTextView = findViewById(R.id.timerTextView);
        noInternetTextView = findViewById(R.id.noInternetTextView);

        usedAnswerOptions = new HashSet<>();
        previousAnswers = new ArrayList<>();

        checkInternetConnection();

        nextButton.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < easyQuestions.size() + mediumQuestions.size() + hardQuestions.size()) {
                showNextQuestion();
            } else {
                showResult();
            }
        });

        finishButton.setOnClickListener(v -> startNewGame());

        viewModel.getQuestions().observe(this, questions -> {
            allQuestions = questions;
            // Grupišite pitanja prema težini
            easyQuestions = filterQuestionsByDifficulty(allQuestions, "easy");
            mediumQuestions = filterQuestionsByDifficulty(allQuestions, "medium");
            hardQuestions = filterQuestionsByDifficulty(allQuestions, "hard");
            showNextQuestion();
            loadingProgressBar.setVisibility(View.GONE);
        });

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
            score = savedInstanceState.getInt(KEY_SCORE);
            usedAnswerOptions = new HashSet<>((Set<String>) savedInstanceState.getSerializable(KEY_USED_ANSWER_OPTIONS));
            previousAnswers = savedInstanceState.getStringArrayList(KEY_PREVIOUS_ANSWERS);
            showNextQuestion();
        } else {
            startNewGame();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_INDEX, currentIndex);
        outState.putInt(KEY_SCORE, score);
        outState.putStringArrayList(KEY_USED_ANSWER_OPTIONS, new ArrayList<>(usedAnswerOptions));
        outState.putStringArrayList(KEY_PREVIOUS_ANSWERS, new ArrayList<>(previousAnswers));
    }

    private void startNewGame() {
        currentIndex = 0;
        score = 0;
        usedAnswerOptions.clear();
        previousAnswers.clear();
        checkInternetConnection();

        if (isNetworkConnected()) {
            TriviaViewModel viewModel = new ViewModelProvider(this).get(TriviaViewModel.class);
            viewModel.fetchTriviaQuestions(10);
        } else {
            questionNumberTextView.setText("");
            questionTextView.setText("");
            categoryTextView.setText("");
        }
    }

    private void checkInternetConnection() {
        if (isNetworkConnected()) {
            noInternetTextView.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            noInternetTextView.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.GONE);
            answerOptionsRadioGroup.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            finishButton.setVisibility(View.GONE);
            difficultyTextView.setVisibility(View.GONE);
            timerTextView.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @SuppressLint("SetTextI18n")
    private void showNextQuestion() {
        Question question;
        if (currentIndex < easyQuestions.size()) {
            question = easyQuestions.get(currentIndex);
        } else if (currentIndex < easyQuestions.size() + mediumQuestions.size()) {
            question = mediumQuestions.get(currentIndex - easyQuestions.size());
        } else {
            question = hardQuestions.get(currentIndex - easyQuestions.size() - mediumQuestions.size());
        }

        if (currentIndex == easyQuestions.size() + mediumQuestions.size() + hardQuestions.size() - 1) {
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next");
        }

        questionNumberTextView.setText("Question " + (currentIndex + 1) + "/" + (easyQuestions.size() + mediumQuestions.size() + hardQuestions.size()));
        questionTextView.setText(Html.fromHtml(question.getQuestionText(), Html.FROM_HTML_MODE_LEGACY));

        List<String> answerOptions = new ArrayList<>(question.getIncorrectAnswers());
        answerOptions.add(question.getCorrectAnswer());
        Collections.shuffle(answerOptions);

        answerOptionsRadioGroup.removeAllViews();
        for (String answer : answerOptions) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setTextSize(22);
            radioButton.setTextColor(Color.GRAY);
            radioButton.setText(Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY));
            radioButton.setOnClickListener(v -> checkAnswer(radioButton.getText().toString(), question.getCorrectAnswer()));
            answerOptionsRadioGroup.addView(radioButton);
        }

        categoryTextView.setText(question.getCategory());

        String difficulty = question.getDifficulty();
        difficultyTextView.setText("Difficulty: " + difficulty.toUpperCase());
        difficultyTextView.setTextSize(30);
        if (difficulty.equalsIgnoreCase("easy")) {
            difficultyTextView.setTextColor(Color.GREEN);
        } else if (difficulty.equalsIgnoreCase("medium")) {
            difficultyTextView.setTextColor(Color.argb(255, 255, 200, 0));
        } else if (difficulty.equalsIgnoreCase("hard")) {
            difficultyTextView.setTextColor(Color.RED);
        }

        answerOptionsRadioGroup.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        finishButton.setVisibility(View.GONE);
        difficultyTextView.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.VISIBLE);

        // Start the timer
        startTimer();
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        secondsLeft = 20;

        timer = new CountDownTimer(secondsLeft * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsLeft--;
                timerTextView.setText(String.valueOf(secondsLeft));
            }

            @Override
            public void onFinish() {
                // Timer has expired
                // Move to the next question or show the result
                if (currentIndex < easyQuestions.size() + mediumQuestions.size() + hardQuestions.size() - 1) {
                    currentIndex++;
                    showNextQuestion();
                } else {
                    showResult();
                }
            }
        };

        timer.start();
    }

    private void checkAnswer(String selectedAnswer, String correctAnswer) {

        answerOptionsRadioGroup.setEnabled(false); // Disable radio buttons

        boolean isCorrectAnswer = selectedAnswer.equals(correctAnswer);
        if (isCorrectAnswer) {
            if (!usedAnswerOptions.contains(correctAnswer)) {
                score++;
                usedAnswerOptions.add(correctAnswer);
            }
            previousAnswers.add("<font color='#00FF00'>" + Html.fromHtml(selectedAnswer, Html.FROM_HTML_MODE_LEGACY) + "</font>");
        } else {
            previousAnswers.add("<font color='#FF0000'>" + Html.fromHtml(selectedAnswer, Html.FROM_HTML_MODE_LEGACY) + "</font>");
            previousAnswers.add("<font color='#00FF00'>" + Html.fromHtml(correctAnswer, Html.FROM_HTML_MODE_LEGACY) + "</font>");
        }

        nextButton.setEnabled(true); // Enable the next button

        // You can uncomment the following line if you want to automatically move to the next question after selecting an answer
        // showNextQuestion();
    }

    private void showResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Result");
        builder.setMessage("You answered " + score + " out of " + (easyQuestions.size() + mediumQuestions.size() + hardQuestions.size()) + " questions correctly.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            answerOptionsRadioGroup.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
            difficultyTextView.setVisibility(View.GONE);
            timerTextView.setVisibility(View.GONE);
            showCorrectAnswers();
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void showCorrectAnswers() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Correct Answers");

        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Question question : easyQuestions) {
            String correctAnswer = question.getCorrectAnswer();
            sb.append("<b>").append(Html.fromHtml("Question " + index + ": " + question.getQuestionText(), Html.FROM_HTML_MODE_LEGACY)).append("</b><br>");
            sb.append("Correct Answer: ").append(Html.fromHtml(correctAnswer, Html.FROM_HTML_MODE_LEGACY)).append("<br><br>");
            index++;
        }

        for (Question question : mediumQuestions) {
            String correctAnswer = question.getCorrectAnswer();
            sb.append("<b>").append(Html.fromHtml("Question " + index + ": " + question.getQuestionText(), Html.FROM_HTML_MODE_LEGACY)).append("</b><br>");
            sb.append("Correct Answer: ").append(Html.fromHtml(correctAnswer, Html.FROM_HTML_MODE_LEGACY)).append("<br><br>");
            index++;
        }

        for (Question question : hardQuestions) {
            String correctAnswer = question.getCorrectAnswer();
            sb.append("<b>").append(Html.fromHtml("Question " + index + ": " + question.getQuestionText(), Html.FROM_HTML_MODE_LEGACY)).append("</b><br>");
            sb.append("Correct Answer: ").append(Html.fromHtml(correctAnswer, Html.FROM_HTML_MODE_LEGACY)).append("<br><br>");
            index++;
        }

        builder.setMessage(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY));
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();

            questionTextView.setText("");
            questionNumberTextView.setText("");
            categoryTextView.setText("");
            finishButton.setVisibility(View.GONE);

            checkInternetConnection();
            if (isNetworkConnected()) {

                startNewGame();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private List<Question> filterQuestionsByDifficulty(List<Question> questions, String difficulty) {
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question.getDifficulty().equalsIgnoreCase(difficulty)) {
                filteredQuestions.add(question);
            }
        }
        return filteredQuestions;
    }
}