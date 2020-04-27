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
import javafx.scene.layout.*;
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

import java.util.concurrent.ExecutionException;

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
                        String decryptedString="";
                        try {
                            for(int i=Extras.user_key.length();i<16;i++)
                            {
                                Extras.user_key+="0";
                            }
                            SecretKey originalKey2 = new SecretKeySpec(Extras.user_key.getBytes(), 0, Extras.user_key.getBytes().length, "AES");
                            String kas=decrypt(document12.get("encrypted_private_key").toString(),originalKey2);
                            decryptedString = RSA_key.decrypt(document.get(Mi + "-body").toString(), kas);
                            System.out.println(decryptedString);
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
                        String name;
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Alert!", "Files will be created in current directory");
                        while(true)
                        {
                            TextInputDialog td = new TextInputDialog("Enter file name");
                            td.setHeaderText("Enter file name without extension");
                            td.showAndWait();
                            name=td.getEditor().getText().trim();
                            if(name.equals(""))
                            {
                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                        "Error!", "Enter File name");
                            }
                            else
                            {
                                break;
                            }
                        }
                        try {

                            File myObj = new File(name+".txt");
                            if (myObj.createNewFile()) {
                                //System.out.println("File created: " + myObj.getName());
                            } else {
                                //System.out.println("File already exists.");
                            }
                        } catch (IOException ef) {
                            System.out.println("An error occurred.");
                            ef.printStackTrace();
                        }
                        try {
                            FileWriter myWriter = new FileWriter(name+".txt");
                            String write="";
                            write+="Sender - "+sf.getText()+"\n\n";
                            write+="Recipients - "+nameField.getText()+"\n\n";
                            write+="DateTime - "+tif.getText()+"\n\n";
                            write+="Subject - "+userTextField.getText()+"\n\n";
                            write+="Body - "+bodyField.getText()+"\n\n";
                            myWriter.write(write);
                            myWriter.close();

                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Success!", "Successfully written to file "+name+".txt");
                        } catch (IOException ef) {
                            System.out.println("An error occurred.");
                            ef.printStackTrace();
                        }

                    }
                });
            }

        }

        primaryStage.show();

    }
}
