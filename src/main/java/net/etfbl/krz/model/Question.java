package net.etfbl.krz.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {

    private String question;
    private String correctAnswer;
    private String type;
    private ArrayList<String> options;

    private File file;


    public Question() {
    }

    public Question(String question, String correctAnswer, String type, ArrayList<String> options, File file) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.type = type;
        this.options = options;
        this.file = file;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
