package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import net.etfbl.krz.model.Question;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SelectQuestionController implements Initializable {
    public static Question question;

    @FXML
    ToggleGroup toggleGroup = new ToggleGroup();
    @FXML
    Label questionLabel;

    @FXML
    RadioButton option1;
    @FXML
    RadioButton option2;
    @FXML
    RadioButton option3;
    @FXML
    RadioButton option4;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        option1.setToggleGroup(toggleGroup);
        option2.setToggleGroup(toggleGroup);
        option3.setToggleGroup(toggleGroup);
        option4.setToggleGroup(toggleGroup);

        questionLabel.setText(question.getQuestion());

        ArrayList<String> options = question.getOptions();
        
        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));
        option4.setText(options.get(3));

    }
}
