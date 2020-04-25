package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class SignUp extends Application {

    public static void main() {
        launch();
    }
    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
    @Override
    public void start(Stage primaryStage)throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        //engine.eval("var firebase = require(\"firebase/app\");");
        //engine.eval("require(\"firebase/auth\");");
        //engine.eval("require(\"firebase/firestore\");");
        engine.eval("<body>\n" +
                "  <!-- Insert these scripts at the bottom of the HTML, but before you use any Firebase services -->\n" +
                "\n" +
                "  <!-- Firebase App (the core Firebase SDK) is always required and must be listed first -->\n" +
                "  <script src=\"/__/firebase/7.14.1/firebase-app.js\"></script>\n" +
                "\n" +
                "  <!-- If you enabled Analytics in your project, add the Firebase SDK for Analytics -->\n" +
                "  <script src=\"/__/firebase/7.14.1/firebase-analytics.js\"></script>\n" +
                "\n" +
                "  <!-- Add Firebase products that you want to use -->\n" +
                "  <script src=\"/__/firebase/7.14.1/firebase-auth.js\"></script>\n" +
                "  <script src=\"/__/firebase/7.14.1/firebase-firestore.js\"></script>\n" +
                "</body>");
        primaryStage.setTitle("Secure Message Transfer");
        GridPane grid = new GridPane();
        //grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Sign Up Page");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, 1);

        Label Name = new Label("Name");
        grid.add(Name, 0, 4);

        TextField nameField = new TextField();
        grid.add(nameField, 1, 4,5,1);

        Label userName = new Label("Email ID");
        grid.add(userName, 0, 6);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 6,5,1);

        Label pw = new Label("Password");
        grid.add(pw, 0, 8);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 8,5,1);

        Button btn = new Button("Sign Up");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 10);

        Button btn1 = new Button("Go to Login page");
        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn1.getChildren().add(btn1);
        grid.add(hbBtn1, 2, 10);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                if(nameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter your Name");
                    return;
                }

                if(userTextField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter your Email address");
                    return;
                }
                if(pwBox.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter your password");
                    return;
                }
            }
        });

        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        try {
                            new Main().start(new Stage());
                        } catch (ScriptException scriptException) {
                            scriptException.printStackTrace();
                        }
                        primaryStage.close();
                    }
                });
            }
        });
        primaryStage.show();

    }
}