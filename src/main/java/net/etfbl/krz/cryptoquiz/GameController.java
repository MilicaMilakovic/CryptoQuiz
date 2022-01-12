package net.etfbl.krz.cryptoquiz;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.etfbl.krz.model.Question;
import net.etfbl.krz.steganography.Steganography;

import javax.crypto.Cipher;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameController implements Initializable {
    @FXML
    public Pane questionPane;
    @FXML
    public Button nextQuestionBtn;
    @FXML
    public Button resultsButton;

    @FXML
    public ImageView q1;
    public ImageView q2;
    public ImageView q3;
    public ImageView q4;
    public ImageView q5;

    public ImageView hex;
    public ImageView arrows;

    ArrayList<Question> questions = new ArrayList<>();
    Question question;

    public static String answer="";
    String correctAnswer="";
    int correctAnswers = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nextQuestionBtn.setVisible(true);
        resultsButton.setVisible(false);

        File file = new File(Main.resources+ File.separator+"not_answered.png");
        q1.setImage(new Image(file.toURI().toString()));
        q2.setImage(new Image(file.toURI().toString()));
        q3.setImage(new Image(file.toURI().toString()));
        q4.setImage(new Image(file.toURI().toString()));
        q5.setImage(new Image(file.toURI().toString()));

        hex.setImage(new Image((new File(Main.resources+ File.separator+"hex.png")).toURI().toString()));
        arrows.setImage(new Image((new File(Main.resources+ File.separator+"triangles2.png")).toURI().toString()));

        loadQuestion();

        Collections.shuffle(questions);
//        for(Question q : questions)
//            System.out.println(q);

        // Load First Question
        question = questions.remove(0);
        correctAnswer = question.getCorrectAnswer();
        if (question.getType().equals("input")) loadInputQuestion();
        else loadSelectQuestion();

    }


    public void loadQuestion(){

        File inputQuestionsDir = new File(Main.questionsDir+ File.separator+"input");
        File selectQuestionsDir = new File(Main.questionsDir+File.separator+"select");

        File[] inputFiles = inputQuestionsDir.listFiles();
        File[] selectFiles = selectQuestionsDir.listFiles();

        int i = new Random().nextInt(2)+2;
        int s = 5 - i;

        assert inputFiles != null;
        addToArrays(i,questions,inputFiles.length,inputFiles);

        assert selectFiles != null;
        addToArrays(s,questions,selectFiles.length,selectFiles);

        System.out.println("Questions selected!!");

    }

    private void addToArrays(int n, ArrayList<Question> questions, int count, File[] files){

        for(int i=0; i<n; i++){
            try{
                int m = new Random().nextInt(count);
                Question q = decodeQuestion(files[m]);
                if(!questions.contains(q))
                    questions.add(q);
                else --i;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Question decodeQuestion(File stegoFile) throws Exception{

       Cipher cipher = Cipher.getInstance("AES");
       String decoded = Steganography.decode(stegoFile);

       byte[] decrypt = null;
       cipher.init(Cipher.DECRYPT_MODE, Main.stegoKey);
       decrypt = cipher.doFinal(Base64.getDecoder().decode(decoded.getBytes(StandardCharsets.UTF_8)));

       String questionJSON = new String(decrypt);
       Gson gson = new Gson();

        return gson.fromJson(questionJSON,Question.class);
    }


    public void nextQuestion(){
//        System.out.println("answer :" + answer);
        setImage();
        if(questions.size()>0) {
            question = questions.remove(0);
            correctAnswer = question.getCorrectAnswer();

            if (question.getType().equals("input")) loadInputQuestion();
            else loadSelectQuestion();
        } else {
            System.out.println("Kraj");
            loadFinishScreen();
        }
    }


    private void setImage(){
        ImageView[] images = {q1,q2,q3,q4,q5};
        int i = 5-questions.size()-1;
        if(correctAnswer.equals(answer.trim())){
            images[i].setImage(new Image((new File(Main.resources+ File.separator+"correct.png").toURI().toString())));
            ++correctAnswers;
        } else {
            images[i].setImage(new Image((new File(Main.resources+ File.separator+"wrong.png").toURI().toString())));
        }
    }

    public void loadInputQuestion(){
        Parent root = null;
        InputQuestionController.question = question;
        try{
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("question-input.fxml")));
            questionPane.getChildren().removeAll();
            questionPane.getChildren().setAll(root);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadSelectQuestion(){
        Parent root = null;
        SelectQuestionController.question = question;
        try{
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("question-select.fxml")));
            questionPane.getChildren().removeAll();
            questionPane.getChildren().setAll(root);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadFinishScreen(){
        Parent root = null;
        EndGameController.score = correctAnswers;
        nextQuestionBtn.setVisible(false);
        resultsButton.setVisible(true);
        try{
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("end-game.fxml")));
            questionPane.getChildren().removeAll();
            questionPane.getChildren().setAll(root);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
