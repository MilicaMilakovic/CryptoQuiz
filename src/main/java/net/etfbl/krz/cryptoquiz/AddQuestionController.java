package net.etfbl.krz.cryptoquiz;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.krz.model.Question;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.google.gson.Gson;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
    @FXML
    TextArea questionField;
    @FXML
    TextField correctAnswerField;


    String questionType="";
    File selectedPhoto;

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
                    questionType="input";
                } else {
                    option1.setDisable(false);
                    option2.setDisable(false);
                    option3.setDisable(false);
                    option4.setDisable(false);
                    questionType="select";
                }
            }
        });

    }

    public void selectPhoto(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Files","*.jpg"));
        selectedPhoto = fileChooser.showOpenDialog(stage);

        if(selectedPhoto != null){
            img.setImage(new Image(selectedPhoto.toURI().toString()));
        }
    }

    public void addQuestion(){
        Question question = new Question();

        question.setQuestion(questionField.getText());
        question.setType(questionType);
        question.setCorrectAnswer(correctAnswerField.getText());

        if(questionType.equals("input")){
            question.setOptions(null);
        } else {
            ArrayList<String> options = new ArrayList<>();
            options.add(option1.getText());
            options.add(option2.getText());
            options.add(option3.getText());
            options.add(option4.getText());
            question.setOptions(options);
        }

        Gson gson = new Gson();
        String q = gson.toJson(question);
        System.out.println(q);

        // AES Encryption of the question

        Security.setProperty("crypto.policy", "unlimited");

        try{
            Cipher cipher = Cipher.getInstance("AES");
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecretKey key = keyGenerator.generateKey();

            System.out.println("Keylen: " +Main.stegoKey.getEncoded().length);
            System.out.println("========");
            byte[] output = null;
            cipher.init(Cipher.ENCRYPT_MODE, Main.stegoKey);
            output = cipher.doFinal(q.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sifrat: " );
            System.out.println(new String(output));


            System.out.println("=========");
            byte[] decrypt = null;
            cipher.init(Cipher.DECRYPT_MODE, Main.stegoKey);
            decrypt = cipher.doFinal(output);
            System.out.println("Plaintext: " + new String(decrypt));

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
