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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    public void start(final Stage primaryStage) throws ScriptException, IOException {

        primaryStage.setTitle("Secure Message Transfer");
        final GridPane grid = new GridPane();
        //grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = visualBounds.getWidth() / 2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);


        Text scenetitle = new Text("Home");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 28));
        grid.add(scenetitle, 6, 1);

        Image sendText_image = new Image(new FileInputStream("res/send_text.png"));

        //Setting the image view q
        ImageView sendText_imageView = new ImageView(sendText_image);

        //Setting the position of the image
        sendText_imageView.setX(50);
        sendText_imageView.setY(25);
        sendText_imageView.setFitHeight(50);
        sendText_imageView.setFitWidth(50);

        Button btn_send1 = new Button("Send text");
        btn_send1.setStyle("-fx-background-image: url('/icons/locked.png')");
        HBox hb_send1 = new HBox(10);
        hb_send1.setAlignment(Pos.CENTER);
        hb_send1.getChildren().add(btn_send1);
        grid.add(hb_send1, 0, 4, 4, 2);
        btn_send1.setGraphic(sendText_imageView);
        Image sendFile_image = new Image(new FileInputStream("res/send_file.png"));

        //Setting the image view q
        ImageView sendFile_imageView = new ImageView(sendFile_image);

        //Setting the position of the image
        sendFile_imageView.setX(50);
        sendFile_imageView.setY(25);
        sendFile_imageView.setFitHeight(50);
        sendFile_imageView.setFitWidth(50);

        Button btn_send2 = new Button("Send file");
        HBox hb_send2 = new HBox(10);
        hb_send2.setAlignment(Pos.CENTER);
        hb_send2.getChildren().add(btn_send2);
        grid.add(hb_send2, 4, 4, 4, 2);
        btn_send2.setGraphic(sendFile_imageView);


        Image viewSent_image = new Image(new FileInputStream("res/view_sent.png"));

        //Setting the image view q
        ImageView viewSent_imageView = new ImageView(viewSent_image);

        //Setting the position of the image
        viewSent_imageView.setX(50);
        viewSent_imageView.setY(25);
        viewSent_imageView.setFitHeight(50);
        viewSent_imageView.setFitWidth(50);

        Button btn_view_sent = new Button("View sent msg");
        HBox hb_view_sent = new HBox(10);
        hb_view_sent.setAlignment(Pos.CENTER);
        hb_view_sent.getChildren().add(btn_view_sent);
        grid.add(hb_view_sent, 8, 4, 4, 2);
        btn_view_sent.setGraphic(viewSent_imageView);

        Image received_msg_image = new Image(new FileInputStream("res/received_msg.png"));

        //Setting the image view q
        ImageView received_msg_imageView = new ImageView(received_msg_image);

        //Setting the position of the image
        received_msg_imageView.setX(50);
        received_msg_imageView.setY(25);
        received_msg_imageView.setFitHeight(50);
        received_msg_imageView.setFitWidth(50);

        Button btn_received = new Button("Received msg");
        HBox hb_received = new HBox(10);
        hb_received.setAlignment(Pos.CENTER);
        hb_received.getChildren().add(btn_received);
        grid.add(hb_received, 12, 4, 4, 2);
        btn_received.setGraphic(received_msg_imageView);


        Image changePassword_image = new Image(new FileInputStream("res/change_password.png"));

        //Setting the image view q
        ImageView changePassword_imageView = new ImageView(changePassword_image);

        //Setting the position of the image
        changePassword_imageView.setX(50);
        changePassword_imageView.setY(25);
        changePassword_imageView.setFitHeight(50);
        changePassword_imageView.setFitWidth(50);

        Button btn_ch_pwd = new Button("Change Password");
        HBox hb_ch_pwd = new HBox(10);
        hb_ch_pwd.setAlignment(Pos.CENTER);
        hb_ch_pwd.getChildren().add(btn_ch_pwd);
        grid.add(hb_ch_pwd, 0, 8, 4, 2);
        btn_ch_pwd.setGraphic(changePassword_imageView);


        Image keyRing_image = new Image(new FileInputStream("res/key_ring.png"));

        //Setting the image view q
        ImageView keyRing_imageView = new ImageView(keyRing_image);

        //Setting the position of the image
        keyRing_imageView.setX(50);
        keyRing_imageView.setY(25);
        keyRing_imageView.setFitHeight(50);
        keyRing_imageView.setFitWidth(50);

        Button btn_keyring = new Button("My Contacts");
        HBox hb_keyring = new HBox(10);
        hb_keyring.setAlignment(Pos.CENTER);
        hb_keyring.getChildren().add(btn_keyring);
        grid.add(hb_keyring, 8, 8, 4, 2);
        btn_keyring.setGraphic(keyRing_imageView);

        Image keyPair_image = new Image(new FileInputStream("res/key_pair.png"));

        //Setting the image view q
        ImageView keyPair_imageView = new ImageView(keyPair_image);

        //Setting the position of the image
        keyPair_imageView.setX(50);
        keyPair_imageView.setY(25);
        keyPair_imageView.setFitHeight(50);
        keyPair_imageView.setFitWidth(50);


        Button btn_revoke;
        btn_revoke = new Button("Change/Forgot Passphrase");
        HBox hb_key_pair = new HBox(10);
        hb_key_pair.setAlignment(Pos.CENTER);
        hb_key_pair.getChildren().add(btn_revoke);
        grid.add(hb_key_pair, 4, 8, 4, 2);
        btn_revoke.setGraphic(keyPair_imageView);

        Image logout_image = new Image(new FileInputStream("res/logout.png"));

        //Setting the image view q
        ImageView logout_imageView = new ImageView(logout_image);

        //Setting the position of the image
        logout_imageView.setX(50);
        logout_imageView.setY(25);
        logout_imageView.setFitHeight(50);
        logout_imageView.setFitWidth(50);

        //Setting the preserve ratio of the image view
        logout_imageView.setPreserveRatio(true);

        Button btn_logout = new Button("Logout");
        HBox hb_logout = new HBox(10);
        hb_logout.setAlignment(Pos.CENTER);
        hb_logout.getChildren().add(btn_logout);
        grid.add(hb_logout, 12, 8, 4, 2);
        btn_logout.setGraphic(logout_imageView);

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
                    Extras.email = "";
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