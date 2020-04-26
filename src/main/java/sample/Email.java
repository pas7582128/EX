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
        Text scenetitle = new Text("Compose Email");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 1, Extras.y+1);

        Label Name1 = new Label("ADD Recipient");
        grid.add(Name1, 0, Extras.y+2);

        final TextField nameField1 = new TextField();
        grid.add(nameField1, 1, Extras.y+2,6,1);

        Label Name = new Label("OR Select Recipient From List");
        grid.add(Name, 0, Extras.y+3);


        ArrayList <String> users= new ArrayList<String>();
        users.add("Select Recipient");

        DocumentReference docRef = db.collection("send_list").document(Extras.email);
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
            //al=al.substring(1,al.length()-1);
            String arr[] = al.split(",");
            for(int i=0;i<arr.length;i++)
            {
                users.add(arr[i]);
            }
        }

        final ComboBox combo_box =
                new ComboBox(FXCollections
                        .observableArrayList(users));

        grid.add(combo_box,1, Extras.y+3,6,1);
        combo_box.setPrefWidth(500);
        combo_box.getSelectionModel().selectFirst();


        Label Name3 = new Label("Recipient List");
        grid.add(Name3, 0, Extras.y+4);

        final TextArea nameField = new TextArea();
        nameField.setPrefRowCount(5);
        grid.add(nameField, 1, Extras.y+4,6,1);
        nameField.setEditable(false);
        nameField.setPrefHeight(500);

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
                            String a=nameField.getText();
                            if(a.equals(""))
                            {
                                nameField.setText(value);
                            }
                            else
                            {
                                String[] b = a.split("\n");
                                boolean ab=false;
                                for(int j=0;j<b.length;j++)
                                {
                                    if(b[j].equals(value))
                                    {
                                        ab=true;
                                        break;
                                    }
                                }
                                if(ab==false)
                                {
                                    nameField.setText(nameField.getText()+"\n"+value);
                                }
                                else
                                {
                                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                            "Error!", "Email id already added to recipient list");
                                    return;
                                }
                            }
                        }

                    }
                };
        combo_box.setOnAction(event);

        Button btn2 = new Button("Add Recipient");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.getChildren().add(btn2);
        grid.add(hbBtn2, 8, Extras.y+2);



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

        Label l = new Label("Select appropriate option");
        RadioButton r1 = new RadioButton("You, recipients share a secret");
        RadioButton r2 = new RadioButton("You, recipients do not share a secret");
        ToggleGroup tg = new ToggleGroup();
        r1.setToggleGroup(tg);
        r2.setToggleGroup(tg);
        grid.add(l,0,Extras.y+5);
        r1.setSelected(true);
        grid.add(r1,1,Extras.y+5);
        grid.add(r2,2,Extras.y+5);

        Label l1= new Label("Do you want to sign your email?");
        grid.add(l1,0,Extras.y+6);
        RadioButton r3 = new RadioButton("Yes");
        RadioButton r4 = new RadioButton("No");
        grid.add(r3,1,Extras.y+6);
        grid.add(r4,2,Extras.y+6);
        ToggleGroup tg1 = new ToggleGroup();
        r3.setToggleGroup(tg1);
        r4.setToggleGroup(tg1);
        r3.setSelected(true);


        final Label userName = new Label("Subject");
        grid.add(userName, 0, Extras.y+7);
        final TextArea userTextField = new TextArea();
        grid.add(userTextField, 1, Extras.y+7,6,1);
        userTextField.setPrefRowCount(2);
        userTextField.setPrefHeight(300);

        final Label body = new Label("Body");
        grid.add(body, 0, Extras.y+8);

        final TextArea bodyField = new TextArea();
        grid.add(bodyField, 1, Extras.y+8,6,1);
        //bodyField.setPrefRowCount(10);
        bodyField.setPrefHeight(700);

        Button btn5= new Button("Send Email");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn5);
        grid.add(hbBtn, 1, Extras.y+9);

        btn5.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        //System.out.println("wgew");
                        if(nameField.getText().equals(""))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "Recipient List empty");
                            return;
                        }
                        if(userTextField.getText().equals(""))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "Subject empty");
                            return;
                        }

                        if(bodyField.getText().equals(""))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "Body empty");
                            return;
                        }
                        String type;
                        RadioButton sel = (RadioButton) tg1.getSelectedToggle();
                        String tv = sel.getText();
                        RadioButton sel1 = (RadioButton) tg.getSelectedToggle();
                        String tv1 = sel1.getText();
                        if(tv.equals("No"))
                        {
                            //System.out.println("ewfewf");
                            type="0";
                            if(tv1.equals("You, recipients share a secret"))
                            {
                                type+="1";
                                String value=nameField.getText();
                                String arr[] = value.split("\n");
                                String arr1[]=new String[arr.length];
                                int i;
                                for(i=0;i<arr.length;i++)
                                {
                                    TextInputDialog td = new TextInputDialog("Enter Key");
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
                                    Long c= (Long) document.get("count");
                                    c++;
                                    Date dNow = new Date( );
                                    SimpleDateFormat ft =
                                            new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");

                                    docRef = db.collection("count").document("msg");
                                    ApiFuture<WriteResult>future5 = docRef.update("count", c);
                                    try {
                                        WriteResult result = future5.get();
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    } catch (ExecutionException executionException) {
                                        executionException.printStackTrace();
                                    }
                                    DocumentReference docRef2 = db.collection("send").document(Extras.email);
                                    ApiFuture<DocumentSnapshot> future2 = docRef2.get();
                                    DocumentSnapshot document2 = null;
                                    try {
                                        document2 = future2.get();
                                    }  catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    } catch (ExecutionException executionException) {
                                        executionException.printStackTrace();
                                    }
                                    if(document2.exists())
                                    {
                                        String p=document2.get("send").toString();
                                        p+=","+"M"+c;
                                        Map<String, Object> docData3 = new HashMap();
                                        docData3.put("send", p);

                                        ApiFuture<WriteResult> future3 = db.collection("send").document(Extras.email).set(docData3);
                                    }
                                    else
                                    {
                                        Map<String, Object> docData3 = new HashMap();
                                        docData3.put("send", "M"+c);

                                        ApiFuture<WriteResult> future3 = db.collection("send").document(Extras.email).set(docData3);
                                    }
                                    DocumentReference docRef9 = db.collection("send_list").document(Extras.email);
                                    ApiFuture<DocumentSnapshot> future9 = docRef9.get();
                                    DocumentSnapshot document9 = null;
                                    try {
                                        document9 = future9.get();
                                    }  catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    } catch (ExecutionException executionException) {
                                        executionException.printStackTrace();
                                    }
                                    String tu ="";
                                    for(int ij=0;ij<arr.length-1;ij++)
                                    {
                                        tu+=arr[ij]+",";
                                    }
                                    tu+=arr[arr.length-1];
                                    if(document9.exists())
                                    {
                                        String p=document9.get("send").toString();
                                        for(int ik=0;ik<arr.length;ik++)
                                        {
                                            if(p.contains(arr[ik]))
                                            {

                                            }
                                            else
                                            {
                                                p+=","+arr[ik];
                                            }
                                        }
                                        Map<String, Object> docData9 = new HashMap();
                                        docData9.put("send", p);

                                        ApiFuture<WriteResult> future91 = db.collection("send_list").document(Extras.email).set(docData9);
                                    }
                                    else
                                    {
                                        Map<String, Object> docData9 = new HashMap();
                                        docData9.put("send", tu);

                                        ApiFuture<WriteResult> future91 = db.collection("send_list").document(Extras.email).set(docData9);
                                    }
                                    String a="",cs="";
                                    SecretKey originalKey1 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                                    try {
                                        a=encrypt(userTextField.getText().trim(), originalKey1);
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                    try {
                                        cs=encrypt(bodyField.getText().trim(), originalKey1);
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                    Map<String, Object> docData = new HashMap();
                                    docData.put("id", "M"+c);
                                    docData.put("sender", Extras.email);
                                    docData.put("algorithm", type);
                                    docData.put("recipients", nameField.getText());
                                    docData.put("Date", ft.format(dNow));
                                    docData.put("sub",a);
                                    docData.put("body",cs);

                                    ApiFuture<WriteResult> future1 = db.collection("messages").document("M"+c).set(docData);
                                    for(i=0;i<arr.length;i++)
                                    {
                                        String b="";

                                        String ke=arr1[i];
                                        for(int q=arr1[i].length();q<16;q++)
                                        {
                                            ke+="0";
                                        }
                                        //System.out.println(ke.getBytes().length);
                                        SecretKey originalKey = new SecretKeySpec(ke.getBytes(), 0, ke.getBytes().length, "AES");
                                        try {
                                            b=encrypt(bodyField.getText().trim(), originalKey);
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }

                                        DocumentReference docRef4 = db.collection("receive").document(arr[i]);
                                        ApiFuture<DocumentSnapshot> future4 = docRef4.get();
                                        DocumentSnapshot document4 = null;
                                        try {
                                            document4 = future4.get();
                                        } catch (InterruptedException interruptedException) {
                                            interruptedException.printStackTrace();
                                        } catch (ExecutionException executionException) {
                                            executionException.printStackTrace();
                                        }
                                        if(document4.exists())
                                        {
                                            String p=document4.get("receive").toString();
                                            p+=","+"M"+c;
                                            docRef = db.collection("receive").document(arr[i]);
                                            ApiFuture<WriteResult>future6 = docRef.update("receive", p);
                                            future6=docRef.update("M"+c+"-key",MD5.getMd5(arr1[i]));

                                            future6=docRef.update("M"+c+"-body",b);
                                            try {
                                                WriteResult result = future6.get();
                                            } catch (InterruptedException interruptedException) {
                                                interruptedException.printStackTrace();
                                            } catch (ExecutionException executionException) {
                                                executionException.printStackTrace();
                                            }

                                        }
                                        else
                                        {
                                            Map<String, Object> docData6 = new HashMap();
                                            docData6.put("receive", "M"+c);
                                            docData6.put("M"+c+"-key",MD5.getMd5(arr1[i]));

                                            docData6.put("M"+c+"-body",b);
                                            ApiFuture<WriteResult> future6 = db.collection("receive").document(arr[i]).set(docData6);
                                        }
                                    }
                                }
                                else
                                {

                                }
                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                        "Success", "Message sent Successfully");
                            }
                        }

                    }
                });
            }
        });

        Button btn6= new Button("Home");
        HBox hbBtn6 = new HBox(10);
        hbBtn6.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn6);
        grid.add(hbBtn6, 2, Extras.y+9);

        btn6.setOnAction(new EventHandler<ActionEvent>() {

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
