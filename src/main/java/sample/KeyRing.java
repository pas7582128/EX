package sample;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;

import static com.google.auth.oauth2.GoogleCredentials.fromStream;

public class KeyRing {

    public void start(Stage primaryStage) {



        primaryStage.setTitle("Secure Message Transfer");
        final GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button btn_import = new Button("Import");
        HBox hb_import = new HBox(10);
        hb_import.setAlignment(Pos.CENTER);
        hb_import.getChildren().add(btn_import);
        grid.add(hb_import,0,8,4,2);

        btn_import.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Import().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

    }


}
