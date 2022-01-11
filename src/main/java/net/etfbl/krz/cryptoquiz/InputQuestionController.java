package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import net.etfbl.krz.model.Question;

import java.net.URL;
import java.util.ResourceBundle;

public class InputQuestionController implements Initializable {
    public static Question question;

    @FXML
    Label questionLabel;
    @FXML
    TextField answerField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        questionLabel.setText(question.getQuestion());
    }
}
