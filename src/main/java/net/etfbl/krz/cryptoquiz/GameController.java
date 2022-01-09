package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    public Pane questionPane;
    @FXML
    public Button nextQuestionBtn;

    @FXML
    public ImageView q1;
    public ImageView q2;
    public ImageView q3;
    public ImageView q4;
    public ImageView q5;

    public ImageView hex;
    public ImageView arrows;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File(Main.resources+ File.separator+"not_answered.png");
        q1.setImage(new Image(file.toURI().toString()));
        q2.setImage(new Image(file.toURI().toString()));
        q3.setImage(new Image(file.toURI().toString()));
        q4.setImage(new Image(file.toURI().toString()));
        q5.setImage(new Image(file.toURI().toString()));

        hex.setImage(new Image((new File(Main.resources+ File.separator+"hex.png")).toURI().toString()));
        arrows.setImage(new Image((new File(Main.resources+ File.separator+"triangles2.png")).toURI().toString()));
    }
}
