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

public class Revoke extends Application {

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

        Text scenetitle = new Text("Revoke Keys");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 6, 0);

        Button btn_rev_msg = new Button("Revoke Message key");
        HBox hb_rev_msg = new HBox(10);
        hb_rev_msg.setAlignment(Pos.CENTER);
        hb_rev_msg.getChildren().add(btn_rev_msg);
        grid.add(hb_rev_msg,4,4,4,2);

        Button btn_rev_sign = new Button("Revoke Signature key");
        HBox hb_rev_sign = new HBox(10);
        hb_rev_sign.setAlignment(Pos.CENTER);
        hb_rev_sign.getChildren().add(btn_rev_sign);
        grid.add(hb_rev_sign,4,8,4,2);

        Image viewSent_image = new Image(new FileInputStream("res/home.png"));

        //Setting the image view q
        ImageView viewSent_imageView = new ImageView(viewSent_image);

        //Setting the position of the image
        viewSent_imageView.setX(50);
        viewSent_imageView.setY(25);
        viewSent_imageView.setFitHeight(50);
        viewSent_imageView.setFitWidth(50);

        Button btn_home = new Button("Home");
        HBox hb_view_sent = new HBox(10);
        hb_view_sent.setAlignment(Pos.TOP_CENTER);
        hb_view_sent.getChildren().add(btn_home);
        grid.add(hb_view_sent, 10, 0);
        btn_home.setGraphic(viewSent_imageView);

        btn_rev_msg.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new RevokeMessageKey().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        btn_rev_sign.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new RevokeSignatureKey().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

        btn_home.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Home().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });


        primaryStage.show();



    }
}