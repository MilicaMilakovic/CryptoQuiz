package net.etfbl.krz.cryptoquiz;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.krz.model.Certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public Button loginBtn;
    @FXML
    Label certPath;

    public void login(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        try{
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            Stage thisStage = (Stage) loginBtn.getScene().getWindow();
            thisStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addQuestion(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-question.fxml"));
        try{
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Add Question");
            stage.setScene(scene);
            stage.show();

            Stage thisStage = (Stage) loginBtn.getScene().getWindow();
            thisStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadCertificate(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Photo");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Digital certificates","*.cer"));
        File cert = fileChooser.showOpenDialog(stage);

        if(cert!=null){
            certPath.setText(cert.getAbsolutePath());
            try{
                FileInputStream fileInputStream = new FileInputStream(cert);
                X509Certificate userCertificate = Certificate.loadUserCertificate(fileInputStream);
                String user = Certificate.getCommonName(userCertificate);
                System.out.println("This certificate belongs to:" + user);
            } catch (Exception e){
                e.printStackTrace();
            }
            loginBtn.setDisable(false);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBtn.setDisable(true);
    }
}