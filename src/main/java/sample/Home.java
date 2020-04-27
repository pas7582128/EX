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

        Button btn_keyring=new Button("Key ring");
        HBox hb_keyring=new HBox(10);
        hb_keyring.setAlignment(Pos.CENTER);
        hb_keyring.getChildren().add(btn_keyring);
        grid.add(hb_keyring,4,8,4,2);

        Button btn_revoke = new Button("Revoke Keys");
        HBox hb_revoke = new HBox(10);
        hb_revoke.setAlignment(Pos.CENTER);
        hb_revoke.getChildren().add(btn_revoke);
        grid.add(hb_revoke,8,8,4,2);

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

        btn_send2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Email_File().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

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

        btn_keyring.setOnAction(new EventHandler<ActionEvent>() {

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

        btn_revoke.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Revoke().start(new Stage());
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

        primaryStage.show();

    }
}