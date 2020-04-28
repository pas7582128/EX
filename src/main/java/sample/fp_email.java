package sample;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
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

import javax.mail.MessagingException;
import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;


/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class fp_email extends Application {

    public static void main(String[] args) {
        launch(args);
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
        //grid.setAlignment(Pos.TOP_CENTER);

        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Forgot Password");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, 1);

        Label userName = new Label("Email ID");
        grid.add(userName, 0, 4);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 4,5,1);

        Label pw = new Label("OTP");
        grid.add(pw, 0, 4);
        pw.setVisible(false);

        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 4,5,1);
        pwBox.setVisible(false);

        Label pw1 = new Label("New_Password");
        grid.add(pw1, 0, 4);
        pw1.setVisible(false);

        final PasswordField pwBox1 = new PasswordField();
        grid.add(pwBox1, 1, 4,5,1);
        pwBox1.setVisible(false);

        Button btn = new Button("Submit");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);

        Button btn2 = new Button("Resend OTP");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(btn2);
        grid.add(hbBtn2, 1, 5);
        btn2.setVisible(false);

        Button btn3 = new Button("Go To Login Page");
        HBox hbBtn3 = new HBox(10);
        hbBtn3.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn3.getChildren().add(btn3);
        grid.add(hbBtn3, 1, 6);

        btn3.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Main().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });

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
                if(userName.getText().trim().equals("verified")) {
                    if(pwBox.getText().trim().length()==0)
                    {
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Form Error!", "Please enter your OTP");
                        return;
                    }
                    if(pw.getText().equals("verified"))
                    {
                        if(pwBox1.getText().trim().length()==0)
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Form Error!", "Please enter your new password");
                            return;
                        }
                        else if(pwBox1.getText().trim().length()<8)
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Form Error!", "New Password must be atleast 8 characters");
                            return;
                        }
                        else
                        {
                            DocumentReference docRef = db.collection("login_credentials").document(userTextField.getText().trim());
                            ApiFuture<WriteResult>future2 = docRef.update("password", MD5.getMd5(pwBox1.getText().trim()));
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Alert!", "Password Changed Successfully");
                            try {
                                new Main().start(new Stage());
                            } catch (Exception scriptException) {
                                scriptException.printStackTrace();
                            }
                            primaryStage.close();
                        }
                    }
                    else
                    {
                        DocumentReference docRef = db.collection("forgot_password").document(userTextField.getText().trim());
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
                            if(MD5.getMd5(pwBox.getText().trim()).equals(document.get("otp").toString()))
                            {
                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                        "Alert!", "OTP verified successfully");
                                pw.setText("verified");
                                pw.setVisible(false);
                                pwBox.setVisible(false);
                                pw1.setVisible(true);
                                pwBox1.setVisible(true);
                                btn2.setVisible(false);
                                return;
                            }
                            else
                            {
                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                        "Error!", "Invalid OTP");
                                return;
                            }
                        }
                    }

                }
                else 
                {
                    DocumentReference docRef = db.collection("login_credentials").document(userTextField.getText().trim());
                    ApiFuture<DocumentSnapshot> future = docRef.get();

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
                                "Error!", "Invalid emailid");
                        return;
                    }
                    else
                    {
                        Random random=new Random();
                        String id = String.format("%04d", random.nextInt(10000));
                        Map<String, Object> docData = new HashMap();
                        docData.put("otp", MD5.getMd5(id));
                        ApiFuture<WriteResult> future1 = db.collection("forgot_password").document(userTextField.getText().trim()).set(docData);
                        pw.setVisible(true);
                        pwBox.setVisible(true);
                        btn2.setVisible(true);
                        userTextField.setVisible(false);
                        userName.setVisible(false);
                        try {
                            //System.out.println(userTextField.getText().toString());
                            Send_OTP.sendPlainTextEmail("nsssvnit2020@gmail.com","Nss@svnit@2020",userTextField.getText().trim(),"Forgot Password From Secure Message Transfer","Your OTP is "+id);
                        } catch (MessagingException messagingException) {
                            messagingException.printStackTrace();
                        }
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Alert!", "An OTP has been sent to your email id");
                        userName.setText("verified");
                        return;
                    }
                }

            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Random random=new Random();
                String id = String.format("%04d", random.nextInt(10000));
                Map<String, Object> docData = new HashMap();
                docData.put("otp", MD5.getMd5(id));
                ApiFuture<WriteResult> future1 = db.collection("forgot_password").document(userTextField.getText().trim()).set(docData);

                try {
                    //System.out.println(userTextField.getText().toString());
                    Send_OTP.sendPlainTextEmail("nsssvnit2020@gmail.com","Nss@svnit@2020",userTextField.getText().trim(),"Forgot Password From Secure Message Transfer","Your OTP is "+id);
                } catch (MessagingException messagingException) {
                    messagingException.printStackTrace();
                }
                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                        "Alert!", "OTP has been resent to your email id");
                return;

            }
        });


        primaryStage.show();

    }
}