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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptException;
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
public class SendMessage extends Application {

    static Cipher cipher;
    public static void main() {
        launch();
    }

    public static String decrypt(String encryptedText, SecretKey secretKey)
            throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
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
        Text scenetitle = new Text("Sent Message");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 4, Extras.y+1);

        Label Name3 = new Label("Recipient List");
        grid.add(Name3, 0, Extras.y+2);

        final TextArea nameField = new TextArea();
        nameField.setPrefRowCount(5);
        grid.add(nameField, 1, Extras.y+2,6,1);
        nameField.setEditable(false);
        nameField.setPrefHeight(500);

        Label ti=new Label("Date Time");
        grid.add(ti,0,Extras.y+3);

        TextField tif = new TextField();
        tif.setEditable(false);
        grid.add(tif,1,Extras.y+3,6,1);

        Label pm=new Label("Protection Method");
        grid.add(pm,0,Extras.y+4);

        TextField pmf = new TextField();
        tif.setEditable(false);
        grid.add(pmf,1,Extras.y+4,6,1);

        final Label userName = new Label("Subject");
        grid.add(userName, 0, Extras.y+5);
        final TextArea userTextField = new TextArea();
        grid.add(userTextField, 1, Extras.y+5,6,1);
        userTextField.setPrefRowCount(2);
        userTextField.setPrefHeight(300);
        userTextField.setEditable(false);

        final Label body = new Label("Body");
        grid.add(body, 0, Extras.y+6);

        final TextArea bodyField = new TextArea();
        grid.add(bodyField, 1, Extras.y+6,6,1);
        //bodyField.setPrefRowCount(10);
        bodyField.setPrefHeight(500);
        bodyField.setEditable(false);


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

        DocumentReference docRef = db.collection("send").document(Extras.email);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        } catch (ExecutionException executionException) {
            executionException.printStackTrace();
        }
        if (document.exists())
        {
            String al = document.get("send").toString();
            String [] arr=al.split(",");
            String Mi=arr[Extras.curm];
            DocumentReference docRef1 = db.collection("messages").document(Mi);
            ApiFuture<DocumentSnapshot> future1 = docRef1.get();
            DocumentSnapshot document1 = null;
            try {
                document1 = future1.get();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } catch (ExecutionException executionException) {
                executionException.printStackTrace();
            }
            if (document1.exists())
            {
                String [] arr1=document1.get("recipients").toString().split(",");
                String users="";

                for(int i=0;i<arr1.length-1;i++)
                {
                    users+=arr1[i]+"\n";
                }
                users+=arr1[arr1.length-1];
                nameField.setText(users);
                tif.setText(document1.get("Date").toString());
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
                String abc="";
                SecretKey originalKey1 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                try {
                    abc=decrypt(document1.get("sub").toString(), originalKey1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                userTextField.setText(abc);
                String a = "";
                SecretKey originalKey2 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                try {
                    a = decrypt(document1.get("body").toString(), originalKey2);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                bodyField.setText(a);
                if(document1.get("algorithm").toString().equals("01")) {
                    pmf.setText("Not Signed  Secret Key");

                }
                else if(document1.get("algorithm").toString().equals("00"))
                {
                    pmf.setText("Not Signed  No Secret Key");
                }
                else if(document1.get("algorithm").toString().equals("10"))
                {
                    pmf.setText("Signed  No Secret Key");
                }
                else
                {
                    pmf.setText("Signed  Secret Key");
                }
            }

        }

        primaryStage.show();

    }
}
