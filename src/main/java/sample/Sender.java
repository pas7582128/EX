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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class Sender extends Application {

    static Cipher cipher;
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

    public static String decrypt(String encryptedText, SecretKey secretKey)
            throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {

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



        //Scene scene = new Scene(sp, 300, 50);
        //sc.setFitToWidth(true);



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Sent Messages");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, Extras.y+1);


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
        grid.add(hb_view_sent, 5, 0);
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
            Label[] s=new Label[Extras.page];
            Label[] e=new Label[Extras.page];
            Label[] d=new Label[Extras.page];
            Button [] p=new Button[Extras.page];
            TextArea[] su=new TextArea[Extras.page];
            Button prev= new Button("Previous Page");
            Button next= new Button("Next Page");
            Label curp=new Label("Current Page - "+Extras.cur );
            int i,j,k,co;
            String re=document.get("send").toString();
            String[] arr=re.split(",");
            co=arr.length;
            int max=co/Extras.page;
            if(co%Extras.page>0)
            {
                max++;
            }
            Label[] ini = new Label[5];
            ini[0]=new Label("Sr.No");
            ini[1]=new Label("Sender");
            ini[2]=new Label("Subject");
            ini[3]=new Label("Date Time");
            ini[4]=new Label("View");

            grid.add(ini[0], 0, Extras.y+2);
            grid.add(ini[1], 1, Extras.y+2);
            grid.add(ini[2], 2, Extras.y+2);
            grid.add(ini[3], 3, Extras.y+2);
            grid.add(ini[4], 4, Extras.y+2);
            j=0;

            DocumentReference[] docRef1=new DocumentReference[Extras.page];
            ApiFuture<DocumentSnapshot>[] future1=new ApiFuture[Extras.page];
            DocumentSnapshot[] document1=new DocumentSnapshot[Extras.page];
            for(i=(Extras.cur-1)*Extras.page;i<=Math.min(Extras.cur*Extras.page-1,co-1);i++)
            {
                s[j]=new Label(Integer.toString(i+1));
                grid.add(s[j], 0, Extras.y+3+j);
                docRef1[j] = db.collection("messages").document(arr[co-i-1]);
                future1[j] = docRef1[j].get();
                document1[j] = null;
                try {
                    document1[j] = future1[j].get();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                }
                if (document1[j].exists())
                {
                    e[j]=new Label(document1[j].get("sender").toString());
                    grid.add(e[j], 1, Extras.y+3+j);
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
                        abc=decrypt(document1[j].get("sub").toString(), originalKey1);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    su[j]=new TextArea();
                    su[j].setText(abc);
                    su[j].setEditable(false);
                    su[j].setPrefRowCount(1);
                    grid.add(su[j], 2, Extras.y+3+j);
                    d[j]=new Label(document1[j].get("Date").toString());
                    grid.add(d[j], 3, Extras.y+3+j);
                    p[j]=new Button("View");
                    HBox hbBtn = new HBox(10);
                    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                    hbBtn.getChildren().add(p[j]);
                    grid.add(hbBtn, 4, Extras.y+3+j);
                    int finalI2 = i;
                    p[j].setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent e) {

                                    Extras.curm=co- finalI2 -1;

                                    try {
                                        new SendMessage().start(new Stage());
                                    } catch (Exception scriptException) {
                                        scriptException.printStackTrace();
                                    }
                                    primaryStage.close();



                        }
                    });
                }
                j++;
            }
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(prev);
            grid.add(hbBtn, 1, Extras.y+3+j);
            if(Extras.cur==1)
            {
                prev.setDisable(true);
            }
            grid.add(curp, 2, Extras.y+3+j);
            HBox hbBtn1 = new HBox(10);
            hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn1.getChildren().add(next);
            grid.add(hbBtn1, 3, Extras.y+3+j);
            if(Extras.cur==max)
            {
                next.setDisable(true);
            }
            prev.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    try {
                        Extras.cur--;
                        new Sender().start(new Stage());
                    } catch (Exception scriptException) {
                        scriptException.printStackTrace();
                    }
                    primaryStage.close();
                }
            });
            next.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    try {
                        Extras.cur++;
                        new Sender().start(new Stage());
                    } catch (Exception scriptException) {
                        scriptException.printStackTrace();
                    }
                    primaryStage.close();
                }
            });
        }
        else
        {
            Label Name = new Label("No Messages sent till now");
            grid.add(Name, 2, Extras.y+3);
        }


        primaryStage.show();

    }
}