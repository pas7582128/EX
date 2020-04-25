package sample;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

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
import javafx.collections.FXCollections;
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

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class Email extends Application {

    static Cipher cipher;
    public static void main() {
        launch();
    }

    public static String encrypt(String plainText, SecretKey secretKey)
            throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
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

        /*FileInputStream serviceAccount =
                new FileInputStream("/home/amogh_agrawal/Downloads/key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://message-transfer-7a8dc.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);*/
        InputStream serviceAccount = new FileInputStream("key.json");
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
        Text scenetitle = new Text("Compose Email");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 5, Extras.y+1);

        Label Name = new Label("Recipients List");
        grid.add(Name, 0, Extras.y+2);


        final TextArea nameField = new TextArea();
        nameField.setPrefRowCount(5);
        grid.add(nameField, 1, Extras.y+2,6,1);
        nameField.setEditable(false);

        Label Name1 = new Label("Select Recipient");
        grid.add(Name1, 0, Extras.y+3);

        final TextField nameField1 = new TextField();
        grid.add(nameField1, 1, Extras.y+3,6,1);

        Button btn2 = new Button("Add Recipient");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.getChildren().add(btn2);
        grid.add(hbBtn2, 8, Extras.y+3);

        btn2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        String value= nameField1.getText();
                        boolean c=false;
                        String added=nameField.getText();
                        String arr[] = added.split("\n");
                        for(int i=0;i<arr.length;i++)
                        {
                            if(arr[i].equals(value))
                            {
                                c=true;
                                break;
                            }
                        }
                        if(c==true)
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "Email id already added to recipient list");
                            return;
                        }
                        else {
                            DocumentReference docRef = db.collection("login_credentials").document(value);
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
                                if(nameField.getText().equals(""))
                                {
                                    nameField.setText(value);
                                }
                                else
                                {
                                    nameField.setText(nameField.getText()+"\n"+value);
                                }
                            }
                            else
                            {
                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                        "Error!", "Enter valid email id");
                                nameField1.setText("");
                                return;
                            }
                        }
                    }
                });
            }
        });

        String users[] = { "Select Recipient", "Amogh", "Rahul",
                "Akash"};
        final ComboBox combo_box =
                new ComboBox(FXCollections
                        .observableArrayList(users));

        grid.add(combo_box,1, Extras.y+4,6,1);
        combo_box.setPrefWidth(500);
        combo_box.getSelectionModel().selectFirst();

        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e)
                    {
                        String value= (String) combo_box.getValue();
                        if(value.equals("Select Recipient"))
                        {

                        }
                        else
                        {
                            nameField1.setText(value);
                        }

                    }
                };
        combo_box.setOnAction(event);

        final Label userName = new Label("Subject");
        grid.add(userName, 0, Extras.y+5);
        final TextArea userTextField = new TextArea();
        grid.add(userTextField, 1, Extras.y+5,6,1);
        userTextField.setPrefRowCount(2);

        final Label body = new Label("Body");
        grid.add(body, 0, Extras.y+6);

        final TextArea bodyField = new TextArea();
        grid.add(bodyField, 1, Extras.y+6,6,1);
        bodyField.setPrefRowCount(10);



        Button btn = new Button("Symmetric encrypt and send");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, Extras.y+7);

        Button btn_back = new Button("Back");
        HBox hb_back = new HBox(10);
        hb_back.setAlignment(Pos.BOTTOM_RIGHT);
        hb_back.getChildren().add(btn_back);
        grid.add(hb_back, 1, Extras.y+7);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        if(nameField.getText().equals(""))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "Recipient List empty");
                            return;
                        }
                        if(userName.getText().equals(""))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "Subject empty");
                            return;
                        }
                        if(body.getText().equals(""))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "Body empty");
                            return;
                        }
                        String value=nameField.getText();
                        String arr[] = value.split("\n");
                        String arr1[]=new String[arr.length];
                        int i;
                        for(i=0;i<arr.length;i++)
                        {
                            TextInputDialog td = new TextInputDialog("Enter Key");
                            //td.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                            td.setHeaderText("Enter key for "+arr[i]);

                            PasswordField pwd = new PasswordField();
                            HBox content = new HBox();
                            content.setAlignment(Pos.CENTER_LEFT);
                            content.setSpacing(10);
                            content.getChildren().addAll(new Label("Password"), pwd);
                            td.getDialogPane().setContent(content);
                            td.showAndWait();

                            if(pwd.getText().trim().length()==0)
                            {
                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                        "Error!", "Enter key");
                                i--;
                            }
                            else if(pwd.getText().trim().length()>16)
                            {
                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                        "Error!", "Key must be atmost 16 characters");
                                i--;
                            }
                            else
                            {
                                arr1[i]=pwd.getText().trim();
                            }


                        }
                        try {
                            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                            noSuchAlgorithmException.printStackTrace();
                        }
                        try {
                            cipher = Cipher.getInstance("AES");
                        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                            noSuchAlgorithmException.printStackTrace();
                        } catch (NoSuchPaddingException noSuchPaddingException) {
                            noSuchPaddingException.printStackTrace();
                        }
                        DocumentReference docRef = db.collection("count").document("msg");
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
                            Long c= (Long) document.get("count");
                            c++;
                            Date dNow = new Date( );
                            SimpleDateFormat ft =
                                    new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");
                            Map<String, Object> docData = new HashMap();
                            docData.put("id", "M"+c);
                            docData.put("sender", Extras.email);
                            docData.put("algorithm", "01");
                            docData.put("recipients", nameField.getText());
                            docData.put("Date", ft.format(dNow));

// Add a new document (asynchronously) in collection "cities" with id "LA"
                            ApiFuture<WriteResult> future1 = db.collection("messages").document("M"+c).set(docData);
// ...
// future.get() blocks on response
                            docRef = db.collection("count").document("msg");
                            ApiFuture<WriteResult>future5 = docRef.update("count", c);
                            try {
                                WriteResult result = future5.get();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            } catch (ExecutionException executionException) {
                                executionException.printStackTrace();
                            }
                            //System.out.println("Current Date: " + ft.format(dNow));

                            /*for(i=0;i<arr.length;i++)
                            {
                                String a,b;
                                SecretKey originalKey = new SecretKeySpec(arr[i].getBytes(), 0, arr[i].getBytes().length, "AES");
                                try {
                                    a=encrypt(userName.getText().trim(), originalKey);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                try {
                                    b=encrypt(body.getText().trim(), originalKey);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }

                            }*/
                        }
                        else
                        {

                        }


                    }
                });
            }
        });

        btn_back.setOnAction(new EventHandler<ActionEvent>() {

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
        /*Button btn1 = new Button("Go to Login page");
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
        });*/
        primaryStage.show();

    }
}
