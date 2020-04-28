package sample;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
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
import javafx.scene.control.*;
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

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RevokeSignatureKey extends Application {
    static Cipher cipher3;
    public static void main() {
        launch();
    }

    public static String encrypt3(String plainText, SecretKey secretKey)
            throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        cipher3.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher3.doFinal(plainTextByte);
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
    public void start(final Stage primaryStage) throws ScriptException, IOException,NoSuchAlgorithmException{
        Extras.cur=1;
        InputStream serviceAccount = new FileInputStream(Extras.path);

        //debug
        //System.out.println("\nSA: " + serviceAccount);

        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        //debug
        //System.out.println("\nGC: " + credentials);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();

        //debug
        //System.out.println("\noptions: " + options);


        final Firestore db = FirestoreClient.getFirestore();

        //debug
        //System.out.println("\ndb: " + db);



        //ScriptEngineManager manager = new ScriptEngineManager();
        //ScriptEngine engine = manager.getEngineByName("javascript");
        //engine.eval("var firebase = require(\"firebase/app\");");
        //engine.eval("require(\"firebase/auth\");");
        //engine.eval("require(\"firebase/firestore\");");
        //engine.eval("<body>\n" +
        //      "  <!-- Insert these scripts at the bottom of the HTML, but before you use any Firebase services -->\n" +
        //    "\n" +
        //   "  <!-- Firebase App (the core Firebase SDK) is always required and must be listed first -->\n" +
        // "  <script src=\"/__/firebase/7.14.1/firebase-app.js\"></script>\n" +
        //"\n" +
        //"  <!-- If you enabled Analytics in your project, add the Firebase SDK for Analytics -->\n" +
        //"  <script src=\"/__/firebase/7.14.1/firebase-analytics.js\"></script>\n" +
        //"\n" +
        //"  <!-- Add Firebase products that you want to use -->\n" +
        //"  <script src=\"/__/firebase/7.14.1/firebase-auth.js\"></script>\n" +
        //"  <script src=\"/__/firebase/7.14.1/firebase-firestore.js\"></script>\n" +
        //"</body>");
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


        Text scenetitle = new Text("Revoke Signature Key");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, 0);

        Label label = new Label("New Passphrase : ");
        grid.add(label, 0, 4);

        final PasswordField tf_passphrase = new PasswordField();
        grid.add(tf_passphrase, 1, 4,5,1);

        Button btn_submit = new Button("Submit");
        HBox hb_submit = new HBox(10);
        hb_submit.setAlignment(Pos.CENTER);
        hb_submit.getChildren().add(btn_submit);
        grid.add(hb_submit, 2, 5);


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


        btn_submit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e){

                if (tf_passphrase.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Please enter new passphrase");
                    return;
                }


                if (tf_passphrase.getText().trim().length() < 8) {
                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Form Error!", "Passphrase length must be in between 8 to 16");
                    return;
                }


                RSA_key keyPairGenerator = null;

                try {
                    keyPairGenerator = new RSA_key();
                } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    showAlert(Alert.AlertType.ERROR,grid.getScene().getWindow(),"ERROR!","Something went wrong");
                }
                String k_pub= Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded());
                String k_pr=Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded());



                DocumentReference docRef_sign = db.collection("sign").document(Extras.email);
                DocumentReference docRef_key_cnt = db.collection("key_count").document(Extras.email);
                DocumentReference docRef_key_cnt_u = db.collection("key_count").document(Extras.email);
                DocumentReference docRef_revoked = db.collection("revoked_sign").document(Extras.email);
// asynchronously retrieve the document
                ApiFuture<DocumentSnapshot> future_sign = docRef_sign.get();
                ApiFuture<DocumentSnapshot> future_key_cnt = docRef_key_cnt.get();
                ApiFuture<DocumentSnapshot> future_revoked = docRef_revoked.get();
// ...
// future.get() blocks on response
                DocumentSnapshot document_sign = null;
                DocumentSnapshot document_key_cnt = null;
                DocumentSnapshot document_revoked = null;

                try {
                    document_sign = future_sign.get();
                    document_key_cnt = future_key_cnt.get();
                    document_revoked = future_revoked.get();

                    //System.out.println(document_sign);
                    //System.out.println(docRef_key_cnt);

                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                }
                if (document_sign.exists() && document_key_cnt.exists()) {

                    String pub_key = document_sign.get("public_key").toString();
                    String key_id = document_sign.get("id").toString();
                    String old_passph = document_sign.get("passphrase").toString();

                    String key_cnt = document_key_cnt.get("sign").toString();

                    String key_id2 = key_id + "_public";

                    int key_cnt_2 = Integer.parseInt(key_cnt);
                    key_cnt_2++;


                    String new_id = "S" + key_cnt_2;
                    String new_pub_key = k_pub;
                    String new_pr_key = "";
                    String new_enc_pr_key = "";
                    String new_passph = MD5.getMd5(tf_passphrase.getText().trim());

                    if (old_passph.equals(new_passph)) {
                        showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                "Form Error!", "Passphrase should be different from older one");
                        return;
                    }

                    //for encrypt2 function
                    try {
                        cipher3 = Cipher.getInstance("AES");
                    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                        noSuchAlgorithmException.printStackTrace();
                    } catch (NoSuchPaddingException noSuchPaddingException) {
                        noSuchPaddingException.printStackTrace();
                    }



                    SecretKey originalKey1 = new SecretKeySpec(Extras.AES_KEY.getBytes(), 0, Extras.AES_KEY.getBytes().length, "AES");
                    try {
                        new_pr_key = encrypt3(k_pr, originalKey1);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    String p1 = tf_passphrase.getText().trim();
                    for(int i=p1.length();i<16;i++)
                    {
                        p1+="0";
                    }
                    SecretKey originalKey2 = new SecretKeySpec(p1.getBytes(), 0, p1.getBytes().length, "AES");
                    try {
                        new_enc_pr_key=encrypt3(k_pr, originalKey2);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }


                    ApiFuture<WriteResult> future_key_cnt_u = docRef_key_cnt_u.update("sign", key_cnt_2);


                    docRef_sign.update("encrypted_private_key",new_enc_pr_key);
                    docRef_sign.update("id",new_id);
                    docRef_sign.update("passphrase",new_passph);
                    docRef_sign.update("private_key",new_pr_key);
                    docRef_sign.update("public_key",new_pub_key);

                    String list = "";
                    if(key_cnt_2 > 2) {
                        list = document_revoked.get("List").toString();
                        list += ",S" + (key_cnt_2 - 1);
                        docRef_revoked.update(key_id2,pub_key);
                        docRef_revoked.update("List",list);
                    }
                    else{
                        list = "S1";
                        Map<String, String> docData = new HashMap();
                        docData.put(key_id2,pub_key);
                        docData.put("List",list);

                        docRef_revoked.set(docData);
                    }



                    //debug
                    //System.out.println("old public : " + pub_key);
                    //System.out.println("encrypted_private_key : "+new_enc_pr_key);
                    //System.out.println("id : "+new_id);
                    //System.out.println("passphrase : "+new_passph);
                    //System.out.println("private_key :" +new_pr_key);
                    //System.out.println("public_key :"+new_pub_key);

                    try {
                        WriteResult result = future_key_cnt_u.get();  //I think it is not needed

                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } catch (ExecutionException executionException) {
                        executionException.printStackTrace();
                    }

                    showAlert(Alert.AlertType.INFORMATION,grid.getScene().getWindow(),"Success!","Passphrase and Key updated");


                } else {

                    showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                            "Error!", "Something went wrong");
                    return;


                }
            }
        });

        btn_home.setOnAction(new EventHandler<ActionEvent>() {

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


        primaryStage.show();



    }
}