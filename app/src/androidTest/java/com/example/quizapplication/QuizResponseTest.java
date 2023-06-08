package com.example.quizapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QuizResponseTest {
    private QuizResponse quizResponse;

    @Before
    public void setUp() {
        quizResponse = new QuizResponse();
    }

    @Test
    public void testGetQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Question 1"));
        questions.add(new Question("Question 2"));

        quizResponse.setQuestions(questions);

        List<Question> retrievedQuestions = quizResponse.getQuestions();

        assertNotNull(retrievedQuestions);
        assertEquals(2, retrievedQuestions.size());
        assertEquals("Question 1", retrievedQuestions.get(0).getQuestionText());
        assertEquals("Question 2", retrievedQuestions.get(1).getQuestionText());
    }
}
