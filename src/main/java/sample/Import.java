package sample;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Import extends Application {
    public static void main() {
        launch();
    }

    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    @Override
    public void start(final Stage primaryStage) throws IOException, ExecutionException, InterruptedException {

        InputStream serviceAccount = new FileInputStream(Extras.path);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();

        try {
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {

        }
        final Firestore db = FirestoreClient.getFirestore();


        primaryStage.setTitle("Secure Message Transfer");
        final GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = visualBounds.getWidth() / 2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Import email");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, Extras.y + 1);


        ArrayList<String> users= new ArrayList<String>();
        ArrayList<String> alreadyAdded= new ArrayList<String>();

        //asynchronously retrieve multiple documents
        ApiFuture<QuerySnapshot> future =
                db.collection("login_credentials").get();
// future.get() blocks on response
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (DocumentSnapshot document : documents) {
//            System.out.println(document.getId() );
            users.add(document.getId());
        }

        DocumentReference docRef = db.collection("send_list").document(Extras.email);
        ApiFuture<DocumentSnapshot> future2 = docRef.get();
        DocumentSnapshot document = null;
        try {
            document = future2.get();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        } catch (ExecutionException executionException) {
            executionException.printStackTrace();
        }

        if (document.exists()) {
            String al = document.get("send").toString();
            //al=al.substring(1,al.length()-1);
            String arr[] = al.split(",");
            boolean bi=false;
            for(int idx=0;idx<arr.length;idx++)
            {
                if(arr[idx].equals(Extras.email))
                {
                    bi=true;
                }
                users.remove(users.indexOf(arr[idx]));
                alreadyAdded.add(arr[idx]);
            }
            if(bi==false)
            {
                alreadyAdded.add(Extras.email);
                users.remove(users.indexOf(Extras.email));
            }

        }

//        System.out.println(users);

        Label text=new Label("Enter email:");
        grid.add(text, 1, Extras.y+3);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 2, Extras.y+3);

        Button btn = new Button("Add to my keyring");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 3, Extras.y+3);



        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(userTextField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter email address");
                    return;
                }
                String st=userTextField.getText().trim();
                for(int idx=0;idx<alreadyAdded.size();idx++)
                {
                    if(alreadyAdded.contains(st))
                    {
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Form Error!", "This email is already in your contact list.");
                        return;
                    }
                }

                if(users.contains(st))
                {
                    DocumentReference docRef = db.collection("send_list").document(Extras.email);
                    ApiFuture<DocumentSnapshot> future2 = docRef.get();
                    DocumentSnapshot document = null;
                    try {
                        document = future2.get();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } catch (ExecutionException executionException) {
                        executionException.printStackTrace();
                    }
                    String tp="";
                    if(document.exists()) {
                        tp = Objects.requireNonNull(document.get("send")).toString();
                        tp=tp+","+st;
                    }
                    else
                    {
                        tp=st;
                    }
                    Map<String, String> docData = new HashMap();
                    docData.put("send", tp);
                    ApiFuture<WriteResult> future1 = db.collection("send_list").document(Extras.email).set(docData);
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Success!", "Email added to contact list");

                    try {
                        new KeyRing().start(new Stage());
                    } catch (Exception scriptException) {
                        scriptException.printStackTrace();
                    }
                    primaryStage.close();
                }
                else
                {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter correct email address");
                    return;
                }
            }
        });


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

        btn_home.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        try {
                            new Home().start(new Stage());
                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
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
