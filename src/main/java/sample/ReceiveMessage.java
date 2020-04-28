package sample;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import java.io.*;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.*;

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
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptException;
import javax.swing.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.concurrent.ExecutionException;

import static sample.RSA_key.verify;

/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class ReceiveMessage extends Application {

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
    public void start(final Stage primaryStage) throws Exception {
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
        Text scenetitle = new Text("Received Message");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 3, Extras.y+1);

        Label s=new Label("Sender");
        grid.add(s,0,Extras.y+2);

        TextField sf = new TextField();
        sf.setEditable(false);
        grid.add(sf,1,Extras.y+2,6,1);

        Label Name3 = new Label("Recipient List");
        grid.add(Name3, 0, Extras.y+3);

        final TextArea nameField = new TextArea();
        nameField.setPrefRowCount(5);
        grid.add(nameField, 1, Extras.y+3,6,1);
        nameField.setEditable(false);
        nameField.setPrefHeight(500);

        Label ti=new Label("Date Time");
        grid.add(ti,0,Extras.y+4);

        TextField tif = new TextField();
        tif.setEditable(false);
        grid.add(tif,1,Extras.y+4,6,1);

        Label p=new Label("Protection Method");
        grid.add(p,0,Extras.y+5);

        TextField pf = new TextField();
        pf.setEditable(false);
        grid.add(pf,1,Extras.y+5,6,1);

        final Label userName = new Label("Subject");
        grid.add(userName, 0, Extras.y+6);
        final TextArea userTextField = new TextArea();
        grid.add(userTextField, 1, Extras.y+6,6,1);
        userTextField.setPrefRowCount(2);
        userTextField.setPrefHeight(300);
        userTextField.setEditable(false);

        final Label body = new Label("Body");
        grid.add(body, 0, Extras.y+7);

        final TextArea bodyField = new TextArea();
        grid.add(bodyField, 1, Extras.y+7,6,1);
        //bodyField.setPrefRowCount(10);
        bodyField.setPrefHeight(500);
        bodyField.setEditable(false);

        Button btn = new Button("Save to File");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, Extras.y+8);

        DocumentReference docRef = db.collection("receive").document(Extras.email);
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
            String al = document.get("receive").toString();
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
                if(document1.get("algorithm").toString().equals("01")) {
                    for (int i = Extras.user_key.length(); i < 16; i++) {
                        Extras.user_key += "0";
                    }
                    String a = "";
                    SecretKey originalKey2 = new SecretKeySpec(Extras.user_key.getBytes(), 0, Extras.user_key.getBytes().length, "AES");
                    try {
                        a = decrypt(document.get(Mi + "-body").toString(), originalKey2);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    bodyField.setText(a);
                }
                else if(document1.get("algorithm").toString().equals("00"))
                {
                    DocumentReference docRef12 = db.collection("asymmetric").document(Extras.email);
                    ApiFuture<DocumentSnapshot> future12 = docRef12.get();
                    DocumentSnapshot document12 = null;
                    try {
                        document12 = future12.get();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } catch (ExecutionException executionException) {
                        executionException.printStackTrace();
                    }
                    if (document12.exists())
                    {
                        if(document.get(Mi+"-id").toString().equals(document12.get("id").toString()))
                        {
                            String decryptedString="";
                            try {
                                for(int i=Extras.user_key.length();i<16;i++)
                                {
                                    Extras.user_key+="0";
                                }
                                SecretKey originalKey2 = new SecretKeySpec(Extras.user_key.getBytes(), 0, Extras.user_key.getBytes().length, "AES");
                                String kas=decrypt(document12.get("encrypted_private_key").toString(),originalKey2);
                                decryptedString = RSA_key.decrypt(document.get(Mi + "-body").toString(), kas);
                                //System.out.println(decryptedString);
                            } catch (NoSuchAlgorithmException e) {
                                System.err.println(e.getMessage());
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bodyField.setText(decryptedString);
                        }
                        else
                        {
                            try {
                                cipher = Cipher.getInstance("AES");
                            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                                noSuchAlgorithmException.printStackTrace();
                            } catch (NoSuchPaddingException noSuchPaddingException) {
                                noSuchPaddingException.printStackTrace();
                            }
                            DocumentReference docRef31 = db.collection("revoked_asymmetric").document(Extras.email);
                            ApiFuture<DocumentSnapshot> future31 = docRef31.get();
                            DocumentSnapshot document31 = null;
                            try {
                                document31 = future31.get();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            } catch (ExecutionException executionException) {
                                executionException.printStackTrace();
                            }
                            if (document31.exists())
                            {
                                String ab=document31.get(document.get(Mi+"-id").toString()+"_private").toString();
                                String abcd="";
                                SecretKey originalKey2 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                                try {
                                    abcd=decrypt(ab, originalKey2);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                String decryptedString = RSA_key.decrypt(document.get(Mi + "-body").toString(), abcd);
                                bodyField.setText(decryptedString);
                            }
                        }

                    }
                    else
                    {

                    }

                }
                else if(document1.get("algorithm").toString().equals("10"))
                {

                    DocumentReference docRef12 = db.collection("asymmetric").document(Extras.email);
                    ApiFuture<DocumentSnapshot> future12 = docRef12.get();
                    DocumentSnapshot document12 = null;
                    try {
                        document12 = future12.get();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } catch (ExecutionException executionException) {
                        executionException.printStackTrace();
                    }
                    if (document12.exists())
                    {
                        String decryptedString="",kas="";
                        if(document.get(Mi+"-sid").toString().equals(document12.get("id").toString()))
                        {
                            for(int i=Extras.user_key.length();i<16;i++)
                            {
                                Extras.user_key+="0";
                            }
                            SecretKey originalKey2 = new SecretKeySpec(Extras.user_key.getBytes(), 0, Extras.user_key.getBytes().length, "AES");
                            kas=decrypt(document12.get("encrypted_private_key").toString(),originalKey2);
                        }
                        else
                        {
                            DocumentReference docRef42 = db.collection("revoked_asymmetric").document(Extras.email);
                            ApiFuture<DocumentSnapshot> future42 = docRef42.get();
                            DocumentSnapshot document42 = null;
                            try {
                                document42 = future42.get();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            } catch (ExecutionException executionException) {
                                executionException.printStackTrace();
                            }
                            if (document42.exists())
                            {
                                SecretKey originalKey2 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                                kas=decrypt(document42.get(document.get(Mi+"-id").toString()+"_private").toString(),originalKey2);
                            }
                            else
                            {

                            }
                        }
                        try {


                            decryptedString = RSA_key.decrypt(document.get(Mi + "-body").toString(), kas);

                            String sk=document.get(Mi+"-sid").toString();
                            DocumentReference docRef92 = db.collection("sign").document(document1.get("sender").toString());
                            ApiFuture<DocumentSnapshot> future92 = docRef92.get();
                            DocumentSnapshot document92 = null;
                            try {
                                document92 = future92.get();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            } catch (ExecutionException executionException) {
                                executionException.printStackTrace();
                            }
                            if (document92.exists())
                            {
                                if(document92.get("id").toString().equals(sk))
                                {
                                    int w=decryptedString.indexOf('@');
                                    int signature_l=Integer.parseInt(decryptedString.substring(0,w));
                                    String signature=decryptedString.substring(w+1,w+1+signature_l);
                                    String mess=decryptedString.substring(w+1+signature_l);
                                    String md_mess=MD5.getMd5(mess);

                                    boolean isCorrect = verify(md_mess, signature, RSA_key.getPublicKey(document92.get("public_key").toString()));
                                    if(isCorrect==true)
                                    {

                                        showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                                "Success!", "Signature verified successfully");
                                        bodyField.setText(mess);
                                    }
                                    else
                                    {
                                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                "Error!", "Invalid signature");
                                    }
                                }
                                else
                                {
                                    showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                            "Alert!", "Passphrase for verifying signature changed");
                                    int w=decryptedString.indexOf('@');
                                    int signature_l=Integer.parseInt(decryptedString.substring(0,w));
                                    String signature=decryptedString.substring(w+1,w+1+signature_l);
                                    String mess=decryptedString.substring(w+1+signature_l);
                                    String md_mess=MD5.getMd5(mess);

                                    DocumentReference docRef82 = db.collection("revoked_sign").document(document1.get("sender").toString());
                                    ApiFuture<DocumentSnapshot> future82 = docRef82.get();
                                    DocumentSnapshot document82 = null;
                                    try {
                                        document82 = future82.get();
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    } catch (ExecutionException executionException) {
                                        executionException.printStackTrace();
                                    }
                                    if (document82.exists())
                                    {
                                        boolean isCorrect = verify(md_mess, signature, RSA_key.getPublicKey(document82.get(sk+"_public").toString()));
                                        if(isCorrect==true)
                                        {

                                            showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                                    "Success!", "Signature verified successfully");
                                            bodyField.setText(mess);
                                        }
                                        else
                                        {
                                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                    "Error!", "Invalid signature");
                                        }
                                    }
                                    else
                                    {

                                    }
                                }
                            }
                            else
                            {

                            }
                            //System.out.println(decryptedString);
                            //bodyField.setText(decryptedString);
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println(e.getMessage());
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {

                    }

                }
                else
                {
                    for (int i = Extras.user_key.length(); i < 16; i++) {
                        Extras.user_key += "0";
                    }
                    String a = "";
                    SecretKey originalKey2 = new SecretKeySpec(Extras.user_key.getBytes(), 0, Extras.user_key.getBytes().length, "AES");
                    try {
                        a = decrypt(document.get(Mi + "-body").toString(), originalKey2);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                        String decryptedString=a;
                        try {
                            String sk=document.get(Mi+"-sid").toString();
                            DocumentReference docRef92 = db.collection("sign").document(document1.get("sender").toString());
                            ApiFuture<DocumentSnapshot> future92 = docRef92.get();
                            DocumentSnapshot document92 = null;
                            try {
                                document92 = future92.get();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            } catch (ExecutionException executionException) {
                                executionException.printStackTrace();
                            }
                            if (document92.exists())
                            {

                                if(document92.get("id").toString().equals(sk))
                                {
                                    int w=decryptedString.indexOf('@');
                                    int signature_l=Integer.parseInt(decryptedString.substring(0,w));
                                    String signature=decryptedString.substring(w+1,w+1+signature_l);
                                    String mess=decryptedString.substring(w+1+signature_l);
                                    String md_mess=MD5.getMd5(mess);

                                    boolean isCorrect = verify(md_mess, signature, RSA_key.getPublicKey(document92.get("public_key").toString()));
                                    if(isCorrect==true)
                                    {

                                        showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                                "Success!", "Signature verified successfully");
                                        bodyField.setText(mess);
                                    }
                                    else
                                    {
                                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                "Error!", "Invalid signature");
                                    }
                                }
                                else
                                {
                                    showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                            "Alert!", "Passphrase for verifying signature changed");
                                    int w=decryptedString.indexOf('@');
                                    int signature_l=Integer.parseInt(decryptedString.substring(0,w));
                                    String signature=decryptedString.substring(w+1,w+1+signature_l);
                                    String mess=decryptedString.substring(w+1+signature_l);
                                    String md_mess=MD5.getMd5(mess);

                                    DocumentReference docRef82 = db.collection("revoked_sign").document(document1.get("sender").toString());
                                    ApiFuture<DocumentSnapshot> future82 = docRef82.get();
                                    DocumentSnapshot document82 = null;
                                    try {
                                        document82 = future82.get();
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    } catch (ExecutionException executionException) {
                                        executionException.printStackTrace();
                                    }
                                    if (document82.exists())
                                    {
                                        boolean isCorrect = verify(md_mess, signature, RSA_key.getPublicKey(document82.get(sk+"_public").toString()));
                                        if(isCorrect==true)
                                        {

                                            showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                                    "Success!", "Signature verified successfully");
                                            bodyField.setText(mess);
                                        }
                                        else
                                        {
                                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                    "Error!", "Invalid signature");
                                        }
                                    }
                                    else
                                    {

                                    }

                                }
                            }
                            else
                            {

                            }
                            //System.out.println(decryptedString);
                            //bodyField.setText(decryptedString);
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println(e.getMessage());
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                }
                sf.setText(document1.get("sender").toString());
                if(document1.get("algorithm").toString().equals("01")) {
                    pf.setText("Not Signed  Secret Key");

                }
                else if(document1.get("algorithm").toString().equals("00"))
                {
                    pf.setText("Not Signed  No Secret Key");
                }
                else if(document1.get("algorithm").toString().equals("10"))
                {
                    pf.setText("Signed  No Secret Key");
                }
                else
                {
                    pf.setText("Signed  Secret Key");
                }
                btn.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent e) {
                        FileChooser fil_chooser = new FileChooser();
                        File file = fil_chooser.showSaveDialog(primaryStage);

                        if (file != null) {
                            showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                    "Alert!", file.getAbsolutePath()
                                            + "  selected");


                            try {
                                FileWriter myWriter = new FileWriter(file.getAbsolutePath());
                                String write="";
                                write+="Sender - "+sf.getText()+"\n\n";
                                write+="Recipients - "+nameField.getText()+"\n\n";
                                write+="DateTime - "+tif.getText()+"\n\n";
                                write+="Subject - "+userTextField.getText()+"\n\n";
                                write+="Body - "+bodyField.getText()+"\n\n";
                                myWriter.write(write);
                                myWriter.close();

                                showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                        "Success!", "Successfully written to file "+file.getAbsolutePath());
                            } catch (IOException ef) {
                                System.out.println("An error occurred.");
                                ef.printStackTrace();
                            }
                        }
                        else
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Alert!","No file selected");
                        }



                    }
                });
            }

        }

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
