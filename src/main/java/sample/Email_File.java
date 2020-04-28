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
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.RSA_key.sign;
import static sample.RSA_key.verify;

/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class Email_File extends Application {

    static Cipher cipher;


    public static String readFile(File file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                bufferedReader.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return stringBuffer.toString();
    }

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
        body.setVisible(false);

        final TextArea bodyField = new TextArea();
        grid.add(bodyField, 1, Extras.y+8,6,1);
        //bodyField.setPrefRowCount(10);
        //bodyField.setPrefHeight(700);
        bodyField.setVisible(false);

        final Label cf = new Label("Choose File");
        grid.add(cf, 0, Extras.y+8);


        Button btn6= new Button("Choose File");
        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn1.getChildren().add(btn6);
        grid.add(hbBtn1, 1, Extras.y+8);

        Label label = new Label("no files selected");
        grid.add(label, 2, Extras.y+8);
        Button btn5= new Button("Send Email");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn5);
        grid.add(hbBtn, 1, Extras.y+9);


        btn6.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                //fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(primaryStage);
                if(file != null){
                    bodyField.setText(readFile(file));
                    label.setText(file.getAbsolutePath()
                            + "  selected");
                }
                else
                {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Error!", "No File Chosen");
                    return;
                }

            }
        });

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

                        if(label.getText().equals("no files selected"))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "No File selected");
                            return;
                        }

                        if(bodyField.getText().trim().equals(""))
                        {
                            showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                    "Error!", "File empty");
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
                                    Optional<String> result=td.showAndWait();
                                    if(!result.isPresent())
                                    {
                                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                "Error!", "Message not sent");
                                        return;
                                    }

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
                                    /*DocumentReference docRef9 = db.collection("send_list").document(Extras.email);
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
                                    }*/
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
                                showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                        "Success", "Message sent Successfully");
                            }
                            else
                            {
                                type+="0";
                                String value=nameField.getText();
                                String arr[] = value.split("\n");
                                int l=arr.length;
                                ArrayList<String>a1=new ArrayList<String>();
                                ArrayList<String>a2=new ArrayList<String>();
                                int i;
                                for(i=0;i<l;i++)
                                {
                                    DocumentReference docRef21 = db.collection("asymmetric").document(arr[i]);
                                    ApiFuture<DocumentSnapshot> future21 = docRef21.get();
                                    DocumentSnapshot document21 = null;
                                    try {
                                        document21 = future21.get();
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    } catch (ExecutionException executionException) {
                                        executionException.printStackTrace();
                                    }
                                    if (document21.exists())
                                    {
                                        a1.add(arr[i]);
                                    }
                                    else
                                    {
                                        a2.add(arr[i]);
                                    }
                                }
                                if(a2.size()==l)
                                {
                                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                            "Error", "Message can't be sent\nNone of the recipients have a registered key");
                                    nameField.setText("");
                                    return;
                                }
                                String err="";
                                if(a1.size()!=l)
                                {
                                    for(i=0;i<a2.size()-1;i++)
                                    {
                                        err+=a2.get(i)+",";
                                    }
                                    err+=a2.get(a2.size()-1);
                                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                            "Error", "Message can,t be sent to these recipients as they do not have a registered key\n"+err);
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
                                    /*DocumentReference docRef9 = db.collection("send_list").document(Extras.email);
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
                                    for(int ij=0;ij<a1.size()-1;ij++)
                                    {
                                        tu+=a1.get(ij)+",";
                                    }
                                    tu+=a1.get(a1.size()-1);
                                    if(document9.exists())
                                    {
                                        String p=document9.get("send").toString();
                                        for(int ik=0;ik<a1.size();ik++)
                                        {
                                            if(p.contains(a1.get(ik)))
                                            {

                                            }
                                            else
                                            {
                                                p+=","+a1.get(ik);
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
                                    }*/
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
                                    String abcd="";
                                    for(int y=0;y<a1.size()-1;y++)
                                    {
                                        abcd+=a1.get(y)+",";
                                    }
                                    abcd+=a1.get(a1.size()-1);
                                    docData.put("recipients", abcd);
                                    docData.put("Date", ft.format(dNow));
                                    docData.put("sub",a);
                                    docData.put("body",cs);


                                    ApiFuture<WriteResult> future1 = db.collection("messages").document("M"+c).set(docData);

                                    for(i=0;i<a1.size();i++)
                                    {
                                        DocumentReference docRef22 = db.collection("asymmetric").document(a1.get(i));
                                        ApiFuture<DocumentSnapshot> future22 = docRef22.get();
                                        DocumentSnapshot document22 = null;
                                        try {
                                            document22 = future22.get();
                                        } catch (InterruptedException interruptedException) {
                                            interruptedException.printStackTrace();
                                        } catch (ExecutionException executionException) {
                                            executionException.printStackTrace();
                                        }
                                        if (document22.exists())
                                        {
                                            String encryptedString="";
                                            try {
                                                encryptedString = RSA_key.encrypt(bodyField.getText(), document22.get("public_key").toString());
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
                                            }
                                            DocumentReference docRef4 = db.collection("receive").document(a1.get(i));
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
                                                docRef = db.collection("receive").document(a1.get(i));
                                                ApiFuture<WriteResult>future6 = docRef.update("receive", p);

                                                future6=docRef.update("M"+c+"-id",document22.get("id"));

                                                future6=docRef.update("M"+c+"-body",encryptedString);
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
                                                docData6.put("M"+c+"-id",document22.get("id"));

                                                docData6.put("M"+c+"-body",encryptedString);
                                                ApiFuture<WriteResult> future6 = db.collection("receive").document(a1.get(i)).set(docData6);
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
                                showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                        "Success", "Message sent Successfully");
                            }
                        }
                        else
                        {
                            type="1";

                            TextInputDialog td = new TextInputDialog("Enter passphrase");
                            td.setHeaderText("Enter passphrase for signing");
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
                                        "Error!", "Passphrase not entered");
                                return;
                            }
                            else
                            {
                                String p1=MD5.getMd5(pwd.getText().trim());
                                DocumentReference docRef22 = db.collection("sign").document(Extras.email);
                                ApiFuture<DocumentSnapshot> future22 = docRef22.get();
                                DocumentSnapshot document22 = null;
                                try {
                                    document22 = future22.get();
                                } catch (InterruptedException interruptedException) {
                                    interruptedException.printStackTrace();
                                } catch (ExecutionException executionException) {
                                    executionException.printStackTrace();
                                }
                                if (document22.exists())
                                {
                                    if(p1.equals(document22.get("passphrase").toString()))
                                    {
                                        String hs=MD5.getMd5(bodyField.getText().trim());
                                        String pw=pwd.getText().trim(),signature="";
                                        //System.out.println(document22.get("encrypted_private_key").toString());
                                        try {
                                            cipher = Cipher.getInstance("AES");
                                        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                                            noSuchAlgorithmException.printStackTrace();
                                        } catch (NoSuchPaddingException noSuchPaddingException) {
                                            noSuchPaddingException.printStackTrace();
                                        }
                                        String encryptedString="",kas="",te="";
                                        try {
                                            for(int i=pw.length();i<16;i++)
                                            {
                                                pw+="0";
                                            }
                                            SecretKey originalKey2 = new SecretKeySpec(pw.getBytes(), 0, pw.getBytes().length, "AES");
                                            kas=decrypt(document22.get("encrypted_private_key").toString(),originalKey2);
                                            signature = sign(hs,RSA_key.getPrivateKey( kas));
                                            //System.out.println(signature);

                                            te=signature.length()+"@"+signature+bodyField.getText().trim();

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
                                        if(tv1.equals("You, recipients share a secret"))
                                        {
                                            type+="1";
                                            String value=nameField.getText();
                                            String arr[] = value.split("\n");
                                            String arr1[]=new String[arr.length];
                                            int i;
                                            for(i=0;i<arr.length;i++)
                                            {
                                                td = new TextInputDialog("Enter Key");
                                                td.setHeaderText("Enter key for "+arr[i]);
                                                pwd = new PasswordField();
                                                content = new HBox();
                                                content.setAlignment(Pos.CENTER_LEFT);
                                                content.setSpacing(10);
                                                content.getChildren().addAll(new Label("Password"), pwd);
                                                td.getDialogPane().setContent(content);
                                                Optional<String> result=td.showAndWait();
                                                if(!result.isPresent())
                                                {
                                                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                            "Error!", "Message not sent");
                                                    return;
                                                }

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
                                                /*DocumentReference docRef9 = db.collection("send_list").document(Extras.email);
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
                                                }*/
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
                                                    SecretKey originalKey = new SecretKeySpec(ke.getBytes(), 0, ke.getBytes().length, "AES");
                                                    try {
                                                        //System.out.println(te+"\n"+signature);
                                                        b=encrypt(te, originalKey);
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
                                                        future6=docRef.update("M"+c+"-sid",document22.get("id").toString());
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
                                                        docData6.put("M"+c+"-sid",document22.get("id").toString());
                                                        ApiFuture<WriteResult> future6 = db.collection("receive").document(arr[i]).set(docData6);
                                                    }
                                                }
                                            }
                                            else
                                            {

                                            }
                                            showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                                    "Success", "Message sent Successfully");
                                        }
                                        else
                                        {
                                            type+="0";
                                            String value=nameField.getText();
                                            String arr[] = value.split("\n");
                                            int l=arr.length;
                                            ArrayList<String>a1=new ArrayList<String>();
                                            ArrayList<String>a2=new ArrayList<String>();
                                            int i;
                                            for(i=0;i<l;i++)
                                            {
                                                DocumentReference docRef21 = db.collection("asymmetric").document(arr[i]);
                                                ApiFuture<DocumentSnapshot> future21 = docRef21.get();
                                                DocumentSnapshot document21 = null;
                                                try {
                                                    document21 = future21.get();
                                                } catch (InterruptedException interruptedException) {
                                                    interruptedException.printStackTrace();
                                                } catch (ExecutionException executionException) {
                                                    executionException.printStackTrace();
                                                }
                                                if (document21.exists())
                                                {
                                                    a1.add(arr[i]);
                                                }
                                                else
                                                {
                                                    a2.add(arr[i]);
                                                }
                                            }
                                            if(a2.size()==l)
                                            {
                                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                        "Error", "Message can't be sent\nNone of the recipients have a registered key");
                                                nameField.setText("");
                                                return;
                                            }
                                            String err="";
                                            if(a1.size()!=l)
                                            {
                                                for(i=0;i<a2.size()-1;i++)
                                                {
                                                    err+=a2.get(i)+",";
                                                }
                                                err+=a2.get(a2.size()-1);
                                                showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                        "Error", "Message can,t be sent to these recipients as they do not have a registered key\n"+err);
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
                                                /*DocumentReference docRef9 = db.collection("send_list").document(Extras.email);
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
                                                for(int ij=0;ij<a1.size()-1;ij++)
                                                {
                                                    tu+=a1.get(ij)+",";
                                                }
                                                tu+=a1.get(a1.size()-1);
                                                if(document9.exists())
                                                {
                                                    String p=document9.get("send").toString();
                                                    for(int ik=0;ik<a1.size();ik++)
                                                    {
                                                        if(p.contains(a1.get(ik)))
                                                        {

                                                        }
                                                        else
                                                        {
                                                            p+=","+a1.get(ik);
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
                                                }*/
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
                                                String abcd="";
                                                for(int y=0;y<a1.size()-1;y++)
                                                {
                                                    abcd+=a1.get(y)+",";
                                                }
                                                abcd+=a1.get(a1.size()-1);
                                                docData.put("recipients", abcd);
                                                docData.put("Date", ft.format(dNow));
                                                docData.put("sub",a);
                                                docData.put("body",cs);


                                                ApiFuture<WriteResult> future1 = db.collection("messages").document("M"+c).set(docData);

                                                for(i=0;i<a1.size();i++)
                                                {
                                                    DocumentReference docRef32 = db.collection("asymmetric").document(a1.get(i));
                                                    ApiFuture<DocumentSnapshot> future32 = docRef32.get();
                                                    DocumentSnapshot document32 = null;
                                                    try {
                                                        document32 = future32.get();
                                                    } catch (InterruptedException interruptedException) {
                                                        interruptedException.printStackTrace();
                                                    } catch (ExecutionException executionException) {
                                                        executionException.printStackTrace();
                                                    }
                                                    if (document32.exists())
                                                    {
                                                        encryptedString="";
                                                        try {
                                                            encryptedString = RSA_key.encrypt(te, document32.get("public_key").toString());
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
                                                        }
                                                        DocumentReference docRef4 = db.collection("receive").document(a1.get(i));
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
                                                            docRef = db.collection("receive").document(a1.get(i));
                                                            ApiFuture<WriteResult>future6 = docRef.update("receive", p);

                                                            future6=docRef.update("M"+c+"-id",document32.get("id"));
                                                            future6=docRef.update("M"+c+"-sid",document22.get("id"));

                                                            future6=docRef.update("M"+c+"-body",encryptedString);
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
                                                            docData6.put("M"+c+"-id",document32.get("id"));
                                                            docData6.put("M"+c+"-sid",document22.get("id"));
                                                            docData6.put("M"+c+"-body",encryptedString);
                                                            ApiFuture<WriteResult> future6 = db.collection("receive").document(a1.get(i)).set(docData6);
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
                                            showAlert(Alert.AlertType.INFORMATION, grid.getScene().getWindow(),
                                                    "Success", "Message sent Successfully");
                                        }

                                    }
                                    else
                                    {
                                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                "Error!", "Invalid passphrase");
                                        return;
                                    }
                                }
                                else
                                {

                                }
                            }
                        }

                    }
                });
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
    public static String decrypt(String encryptedText, SecretKey secretKey)
            throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        //System.out.println();
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
}
