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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class ChangePassword extends Application {

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
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Change Password Page");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, Extras.y+1);

        Label Name = new Label("Original Password");
        grid.add(Name, 0, Extras.y+2);

        final PasswordField op = new PasswordField();
        grid.add(op, 1, Extras.y+2,5,1);

        Label userName = new Label("New Password");
        grid.add(userName, 0, Extras.y+3);

        final PasswordField np = new PasswordField();
        grid.add(np, 1, Extras.y+3,5,1);


        Button btn = new Button("Change Password");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, Extras.y+4);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String email= Extras.email;
                if(op.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Original password field empty");
                    return;
                }

                if(np.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "New Password field empty");
                    return;
                }

                if(np.getText().trim().length()<8) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "New Password must be atleast 8 characters long");
                    return;
                }


                DocumentReference docRef = db.collection("login_credentials").document(Extras.email);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                DocumentSnapshot document = null;
                try {
                    document = future.get();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                }
                if (document.exists()) {
                    String dop=document.get("password").toString();
                    if(dop.equals(MD5.getMd5(op.getText().trim())))
                    {
                        docRef = db.collection("login_credentials").document(Extras.email);
                        ApiFuture<WriteResult>future2 = docRef.update("password", MD5.getMd5(np.getText().trim()));
                        try {
                            WriteResult result = future2.get();
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        } catch (ExecutionException executionException) {
                            executionException.printStackTrace();
                        }
                        Firestore db1= FirestoreClient.getFirestore();
                        docRef = db1.collection("change_password").document(Extras.email);
                        ApiFuture<DocumentSnapshot> future3 = docRef.get();
                        document = null;
                        try {
                            document = future3.get();
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        } catch (ExecutionException executionException) {
                            executionException.printStackTrace();
                        }
                        if (document.exists())
                        {
                            String opa = document.get("Original_password_list").toString();
                            opa=opa.substring(0,opa.length()-1);
                            String npa= document.get("New_password_list").toString();
                            npa=npa.substring(0,npa.length()-1);
                            String da=document.get("date_list").toString();
                            da=da.substring(0,da.length()-1);
                            Map<String, String> docData = new HashMap();
                            Date dNow = new Date( );
                            SimpleDateFormat ft =
                                    new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");
                            docData.put("Original_password_list",opa+","+dop+"]");
                            docData.put("New_password_list", npa+","+MD5.getMd5(np.getText().trim())+"]");
                            docData.put("date_list", da+","+ft.format(dNow)+"]");
                            ApiFuture<WriteResult> future1 = db.collection("change_password").document(Extras.email).set(docData);
                        }
                        else
                        {
                            String[] opa= new String[1];
                            opa[0]=dop;
                            //System.out.println(opa[0]);
                            String[] npa= new String[1];
                            npa[0]=MD5.getMd5(np.getText().trim());
                           // System.out.println(npa.toString());
                            Date dNow = new Date( );
                            SimpleDateFormat ft =
                                    new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");
                            String[] da= new String[1];
                            da[0]=ft.format(dNow);
                            Map<String, String> docData = new HashMap();
                            docData.put("Original_password_list",Arrays.toString(opa));
                            docData.put("New_password_list", Arrays.toString(npa));
                            docData.put("date_list", Arrays.toString(da));
                            ApiFuture<WriteResult> future1 = db.collection("change_password").document(Extras.email).set(docData);
                        }
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Alert!", "Password changed successfully");
                        return;
                    }
                    else
                    {
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Error!", "Enter valid old password");
                        return;
                    }

                } else {


                    primaryStage.close();
                }



            }
        });
        primaryStage.show();

    }
}