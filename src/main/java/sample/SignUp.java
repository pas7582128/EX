package sample;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    public void start(final Stage primaryStage) throws IOException {
        Extras.cur=1;

        /*FileInputStream serviceAccount =
                new FileInputStream("/home/amogh_agrawal/Downloads/key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://message-transfer-7a8dc.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);*/
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



        //ScriptEngineManager manager = new ScriptEngineManager();
        //ScriptEngine engine = manager.getEngineByName("javascript");
        //engine.eval("var firebase = require(\"firebase/app\");");
        //engine.eval("require(\"firebase/auth\");");
        //engine.eval("require(\"firebase/firestore\");");
        //engine.eval("<body>\n" +
          //      "  <!-- Insert these scripts at the bottom of the HTML, but before you use any Firebase services -->\n" +
            //    "\n" +
             //   "  <!-- Firebase App (the core Firebase SDK) is always required and must be listed first -->\n" +
               // "  <script src=\"/__/firebase/7.14.1/firebase-app.js\"></script>\n" +
                //"\n" +
                //"  <!-- If you enabled Analytics in your project, add the Firebase SDK for Analytics -->\n" +
                //"  <script src=\"/__/firebase/7.14.1/firebase-analytics.js\"></script>\n" +
                //"\n" +
                //"  <!-- Add Firebase products that you want to use -->\n" +
                //"  <script src=\"/__/firebase/7.14.1/firebase-auth.js\"></script>\n" +
                //"  <script src=\"/__/firebase/7.14.1/firebase-firestore.js\"></script>\n" +
                //"</body>");
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
        Text scenetitle = new Text("Sign Up Page");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, 1);

        Label Name = new Label("Name");
        grid.add(Name, 0, 4);

        final TextField nameField = new TextField();
        grid.add(nameField, 1, 4,5,1);

        Label userName = new Label("Email ID");
        grid.add(userName, 0, 6);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 6,5,1);

        Label pw = new Label("Password");
        grid.add(pw, 0, 8);

        final PasswordField pwBox = new PasswordField();
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

                if(nameField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter your Name");
                    return;
                }

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

                if(pwBox.getText().trim().length()<8) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Password must be atleast 8 characters long");
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
                if (document.exists()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Error!", "Email id already exists");
                    return;
                } else {
                    Map<String, Object> docData = new HashMap();
                    docData.put("name", nameField.getText().trim());
                    docData.put("email_id", userTextField.getText().trim());
                    docData.put("password", MD5.getMd5(pwBox.getText().trim()));

// Add a new document (asynchronously) in collection "cities" with id "LA"
                    ApiFuture<WriteResult> future1 = db.collection("login_credentials").document(userTextField.getText().trim()).set(docData);
// ...
// future.get() blocks on response
                    try {
                        System.out.println("Update time : " + future1.get().getUpdateTime());
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } catch (ExecutionException executionException) {
                        executionException.printStackTrace();
                    }

                    try {

                        Extras.email=userTextField.getText().trim();
                        new Home().start(new Stage());
                    } catch (Exception scriptException) {
                        scriptException.printStackTrace();
                    }
                    primaryStage.close();
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
                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        primaryStage.close();
                    }
                });
            }
        });
        primaryStage.show();

    }
}