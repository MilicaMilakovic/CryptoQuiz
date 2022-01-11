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
import java.util.Base64;
import java.util.Objects;
import java.util.ResourceBundle;
import com.google.gson.Gson;
import net.etfbl.krz.steganography.Steganography;

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

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg","*.png"));
        selectedPhoto = fileChooser.showOpenDialog(stage);

        if(selectedPhoto != null){
            img.setImage(new Image(selectedPhoto.toURI().toString()));
        }
    }

    public void addQuestion() throws Exception{
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

        System.out.println("========");

        // AES Encryption of the question

        // Pitanje (u JSON formatu) se kriptuje AES algoritmom, zatim se vrsi base64 enkodovanje
        // i ubacuje se u sliku

        Security.setProperty("crypto.policy", "unlimited");
        System.out.println("Kljuc:");
        System.out.println(new String(Main.stegoKey.getEncoded()));

        Cipher cipher = Cipher.getInstance("AES");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        System.out.println("Keylen: " +Main.stegoKey.getEncoded().length);
        System.out.println("========");
        byte[] output = null;
        cipher.init(Cipher.ENCRYPT_MODE, Main.stegoKey);
        output = cipher.doFinal(q.getBytes(StandardCharsets.UTF_8));
        System.out.println("Sifrat: " );
        String out = Base64.getEncoder().encodeToString(output);
        System.out.println(out);

        File stegoFile = createStegoFile();
        Steganography.encode(selectedPhoto,out,stegoFile);

        System.out.println("Encoded..." );

        String decoded = Steganography.decode(stegoFile);
        System.out.println("Decoded:");
        System.out.println(decoded);

       System.out.println("=========");
       byte[] decrypt = null;
       cipher.init(Cipher.DECRYPT_MODE, Main.stegoKey);
       decrypt = cipher.doFinal(Base64.getDecoder().decode(decoded.getBytes(StandardCharsets.UTF_8)));
       System.out.println("Plaintext: ");
       System.out.println(new String(decrypt));

    }

    private File createStegoFile(){
        File stegoFile;
        String path = questionType.equals("input") ? Main.questionsDir+File.separator+"input" : Main.questionsDir+File.separator+"select";
        int n = Objects.requireNonNull(new File(path).listFiles()).length;
        stegoFile = new File(path+File.separator+"question"+(n+1)+".bmp");
        return stegoFile;
    }
}
