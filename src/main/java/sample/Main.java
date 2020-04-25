package sample;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;


/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
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
    public void start(final Stage primaryStage) throws IOException {
        Extras.cur=1;
        InputStream serviceAccount = new FileInputStream(Extras.path);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        try {
            FirebaseApp.initializeApp(options);
        }
        catch(Exception e)
        {

        }


        final Firestore db = FirestoreClient.getFirestore();

        primaryStage.setTitle("Secure Message Transfer");
        final GridPane grid = new GridPane();
        //grid.setAlignment(Pos.TOP_CENTER);

        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Login Page");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, 1);

        Label userName = new Label("Email ID");
        grid.add(userName, 0, 4);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 4,5,1);

        Label pw = new Label("Password");
        grid.add(pw, 0, 6);

        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 6,5,1);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 8);

        Button btn1 = new Button("New User? Sign Up");
        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn1.getChildren().add(btn1);
        grid.add(hbBtn1, 2, 8);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(userTextField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter your Email address");
                    return;
                }
                if(pwBox.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter your password");
                    return;
                }
                DocumentReference docRef = db.collection("login_credentials").document(userTextField.getText().trim());
// asynchronously retrieve the document
                ApiFuture<DocumentSnapshot> future = docRef.get();
// ...
// future.get() blocks on response
                DocumentSnapshot document = null;
                try {
                    document = future.get();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                }
                if (!document.exists()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Error!", "Invalid emailid or password");
                    return;
                }
                else
                {
                    String pass= (String) document.get("password");
                    if(pass.equals(MD5.getMd5(pwBox.getText().trim())))
                    {
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Error!", "Successful login");
                        try {
                            Extras.email=userTextField.getText().trim();
                            new Home().start(new Stage());
                        } catch (Exception scriptException) {
                            scriptException.printStackTrace();
                        }
                        primaryStage.close();
                    }
                    else
                    {
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Error!", "Invalid emailid or password");
                        return;
                    }
                }
            }
        });

        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new SignUp().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        primaryStage.show();

    }
}