package sample;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Home extends Application {

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
    public void start(final Stage primaryStage) throws ScriptException, IOException{
        Extras.cur=1;
        InputStream serviceAccount = new FileInputStream(Extras.path);

        //debug
        System.out.println("\nSA: " + serviceAccount);

        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        //debug
        System.out.println("\nGC: " + credentials);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();

        //debug
        System.out.println("\noptions: " + options);


        final Firestore db = FirestoreClient.getFirestore();

        //debug
        System.out.println("\ndb: " + db);



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


        Text scenetitle = new Text("Home");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 6, 0);

        Button btn_send1 = new Button("Send text");
        btn_send1.setStyle("-fx-background-image: url('/icons/locked.png')");
        HBox hb_send1 = new HBox(10);
        hb_send1.setAlignment(Pos.CENTER);
        hb_send1.getChildren().add(btn_send1);
        grid.add(hb_send1,0,4,4,2);

        Button btn_send2 = new Button("Send file");
        HBox hb_send2 = new HBox(10);
        hb_send2.setAlignment(Pos.CENTER);
        hb_send2.getChildren().add(btn_send2);
        grid.add(hb_send2,4,4,4,2);

        Button btn_view_sent = new Button("View sent msg");
        HBox hb_view_sent = new HBox(10);
        hb_view_sent.setAlignment(Pos.CENTER);
        hb_view_sent.getChildren().add(btn_view_sent);
        grid.add(hb_view_sent,8,4,4,2);

        btn_view_sent.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Sender().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        Button btn_received = new Button("Received msg");
        HBox hb_received = new HBox(10);
        hb_received.setAlignment(Pos.CENTER);
        hb_received.getChildren().add(btn_received);
        grid.add(hb_received,12,4,4,2);

        Button btn_ch_pwd = new Button("Change Password");
        HBox hb_ch_pwd = new HBox(10);
        hb_ch_pwd.setAlignment(Pos.CENTER);
        hb_ch_pwd.getChildren().add(btn_ch_pwd);
        grid.add(hb_ch_pwd,0,8,4,2);

        Button btn_key_pair = new Button("Generate key pair");
        HBox hb_key_pair = new HBox(10);
        hb_key_pair.setAlignment(Pos.CENTER);
        hb_key_pair.getChildren().add(btn_key_pair);
        grid.add(hb_key_pair,4,8,4,2);

        Button btn_keyring=new Button("Key ring");
        HBox hb_keyring=new HBox(10);
        hb_keyring.setAlignment(Pos.CENTER);
        hb_keyring.getChildren().add(btn_keyring);
        grid.add(hb_keyring,8,8,4,2);

        Button btn_logout = new Button("Logout");
        HBox hb_logout = new HBox(10);
        hb_logout.setAlignment(Pos.CENTER);
        hb_logout.getChildren().add(btn_logout);
        grid.add(hb_logout,12,8,4,2);

        btn_send1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Email().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });


        btn_ch_pwd.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new ChangePassword().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        btn_received.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Receiver().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        btn_logout.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    Extras.email="";
                    new Main().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        btn_received.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new KeyRing().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        primaryStage.show();



    }
}