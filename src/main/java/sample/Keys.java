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

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static sample.RSA_key.sign;
import static sample.RSA_key.verify;


/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class Keys extends Application {
    static Cipher cipher;

    public static void main(String[] args) {
        launch(args);
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
        //grid.setAlignment(Pos.TOP_CENTER);

        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Passphrase");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, 1);

        Label sign_label = new Label("Enter passphrase for signing");
        grid.add(sign_label, 0, 4);


        final PasswordField pw_sign = new PasswordField();
        grid.add(pw_sign, 1, 4,5,1);

        Label decrypt_label = new Label("Enter passphrase for decrypting received message");
        grid.add(decrypt_label, 0, 6);


        final PasswordField pw_dec = new PasswordField();
        grid.add(pw_dec, 1, 6,5,1);


        Button btn = new Button("Register");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 8);

        RSA_key keyPairGenerator = new RSA_key();
        RSA_key keyPairGenerator1 = new RSA_key();
        //System.out.println(keyPairGenerator.getPublicKey());
        //System.out.println(keyPairGenerator1.getPublicKey());
        //System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded()));
        //System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded()));
        String k1=Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded());
        String k2=Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded());
        String k3=Base64.getEncoder().encodeToString(keyPairGenerator1.getPublicKey().getEncoded());
        String k4=Base64.getEncoder().encodeToString(keyPairGenerator1.getPrivateKey().getEncoded());
        /*try {
            String encryptedString = Base64.getEncoder().encodeToString(RSA_key.encrypt("Dhiraj is the author", k1));
            System.out.println(encryptedString);
            String decryptedString = RSA_key.decrypt(encryptedString, k2);
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
        }*/

        /*String signature = sign("foobar", keyPairGenerator.getPrivateKey());

//Let's check the signature
        boolean isCorrect = verify("foobar", signature, keyPairGenerator.getPublicKey());
        System.out.println("Signature correct: " + isCorrect);*/
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(pw_sign.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Enter signing passphrase");
                    return;
                }
                if(pw_dec.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Enter decrypting passphrase");
                    return;
                }
                if(pw_sign.getText().trim().length()<8||pw_sign.getText().trim().length()>16) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Signing passphrase must be between 8 and 16characters");
                    return;
                }
                if(pw_dec.getText().trim().length()<8||pw_dec.getText().trim().length()>16) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Decrypting passphrase must be between 8 and 16characters");
                    return;
                }
                Map<String, Object> docData = new HashMap();
                docData.put("sign", 1);
                docData.put("asymmetric", 1);
                //docData.put("password", MD5.getMd5(pwBox.getText().trim()));

// Add a new document (asynchronously) in collection "cities" with id "LA"
                ApiFuture<WriteResult> future3 = db.collection("key_count").document(Extras.email).set(docData);


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
                String si="";
                String p1=pw_sign.getText().trim();
                String p2=pw_dec.getText().trim();
                SecretKey originalKey1 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                try {
                    si=encrypt(k2, originalKey1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                Map<String, String> docData1 = new HashMap();
                docData1.put("id", "S1");
                docData1.put("public_key", k1);
                docData1.put("passphrase", MD5.getMd5(pw_sign.getText().trim()));
                docData1.put("private_key",si);
                for(int i=p1.length();i<16;i++)
                {
                    p1+="0";
                }
                SecretKey originalKey2 = new SecretKeySpec(p1.getBytes(), 0, p1.getBytes().length, "AES");
                try {
                    si=encrypt(k2, originalKey2);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                docData1.put("encrypted_private_key",si);
                //docData.put("password", MD5.getMd5(pwBox.getText().trim()));

// Add a new document (asynchronously) in collection "cities" with id "LA"
                ApiFuture<WriteResult> future4 = db.collection("sign").document(Extras.email).set(docData1);

                originalKey1 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                try {
                    si=encrypt(k4, originalKey1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                Map<String, String> docData2 = new HashMap();
                docData2.put("id", "A1");
                docData2.put("public_key", k3);
                docData2.put("passphrase", MD5.getMd5(pw_dec.getText().trim()));
                docData2.put("private_key",si);
                for(int i=p2.length();i<16;i++)
                {
                    p2+="0";
                }
                originalKey2 = new SecretKeySpec(p2.getBytes(), 0, p2.getBytes().length, "AES");
                try {
                    si=encrypt(k4, originalKey2);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                docData2.put("encrypted_private_key",si);
                //docData.put("password", MD5.getMd5(pwBox.getText().trim()));

// Add a new document (asynchronously) in collection "cities" with id "LA"
                ApiFuture<WriteResult> future5 = db.collection("asymmetric").document(Extras.email).set(docData2);
                try {
                    new Home().start(new Stage());
                } catch (ScriptException scriptException) {
                    scriptException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                primaryStage.close();

            }
        });



        primaryStage.show();

    }
}