package net.etfbl.krz.cryptoquiz;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddQuestionController implements Initializable {

    ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    RadioButton inputBtn;
    @FXML
    RadioButton selectBtn;

    @FXML
    ImageView img;
    @FXML
    TextField option1;
    @FXML
    TextField option2;
    @FXML
    TextField option3;
    @FXML
    TextField option4;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputBtn.setToggleGroup(toggleGroup);
        selectBtn.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                RadioButton selected = (RadioButton)t1.getToggleGroup().getSelectedToggle();

                if(selected.getText().equals("Unos")){
                    option1.setDisable(true);
                    option2.setDisable(true);
                    option3.setDisable(true);
                    option4.setDisable(true);
                } else {
                    option1.setDisable(false);
                    option2.setDisable(false);
                    option3.setDisable(false);
                    option4.setDisable(false);
                }
            }
        });

    }

    public void selectPhoto(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Files","*.jpg"));
        File fileSelected = fileChooser.showOpenDialog(stage);

        if(fileSelected != null){
            img.setImage(new Image(fileSelected.toURI().toString()));
        }
    }
}
