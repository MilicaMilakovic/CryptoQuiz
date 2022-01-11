package net.etfbl.krz.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Question implements Serializable {

    private String question;
    private String correctAnswer;
    private String type;
    private ArrayList<String> options;

    public Question() {
    }

    public Question(String question, String correctAnswer, String type, ArrayList<String> options) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.type = type;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question1 = (Question) o;
        return Objects.equals(question, question1.question) && Objects.equals(correctAnswer, question1.correctAnswer) && Objects.equals(type, question1.type) && Objects.equals(options, question1.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, correctAnswer, type, options);
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", type='" + type + '\'' +
                ", options=" + options +
                '}';
    }
}
