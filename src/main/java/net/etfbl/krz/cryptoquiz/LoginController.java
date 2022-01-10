package net.etfbl.krz.cryptoquiz;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public Button loginBtn;

    @FXML
    public ImageView triangle;

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
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            Stage thisStage = (Stage) loginBtn.getScene().getWindow();
            thisStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        triangle.setImage(new Image((new File(Main.resources+ File.separator+"triangle.png")).toURI().toString()));
    }
}