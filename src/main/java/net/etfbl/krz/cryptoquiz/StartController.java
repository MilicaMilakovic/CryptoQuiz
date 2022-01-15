package net.etfbl.krz.cryptoquiz;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StartController implements Initializable {
    @FXML
    public Button loginBtn;
    @FXML
    public ImageView triangle;
    @FXML
    public Pane pane;

    public void login(){
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1),pane);
        translateTransition.setToX(-320);
        translateTransition.play();
        // UCITAJ PROZOR ZA LOGIN
        translateTransition.setOnFinished(e -> {
            Parent root = null;
            try{
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login-view.fxml")));
                pane.getChildren().removeAll();
                pane.getChildren().setAll(root);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

    public void register(){
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1),pane);
        translateTransition.setToX(40);
        translateTransition.play();
        // UCITAJ PROZOR ZA REGISTRACIJU
        translateTransition.setOnFinished(e -> {
            Parent root = null;
            try{
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registration-view.fxml")));
                pane.getChildren().removeAll();
                pane.getChildren().setAll(root);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        triangle.setImage(new Image((new File(Main.resources+ File.separator+"triangle.png")).toURI().toString()));
        pane.setLayoutX(350);
        Parent root = null;
        try{
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registration-view.fxml")));
            pane.getChildren().removeAll();
            pane.getChildren().setAll(root);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
