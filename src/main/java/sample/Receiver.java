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
public class Receiver extends Application {

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



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("Received Messages");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 2, Extras.y+1);

        Button btn6= new Button("Home");
        HBox hbBtn6 = new HBox(10);
        hbBtn6.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn6.getChildren().add(btn6);
        grid.add(hbBtn6, 4, Extras.y+1);

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
           Label[] s=new Label[10];
           Label[] e=new Label[10];
           Label[] d=new Label[10];
           Button [] p=new Button[10];
           TextArea[] su=new TextArea[10];
           Button prev= new Button("Previous Page");
           Button next= new Button("Next Page");
           Label curp=new Label("Current Page - "+Extras.cur );
           int i,j,k,co;
           String re=document.get("receive").toString();
           String[] arr=re.split(",");
           co=arr.length;
           int max=co/10;
           if(co%10>0)
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

            DocumentReference[] docRef1=new DocumentReference[10];
            ApiFuture<DocumentSnapshot>[] future1=new ApiFuture[10];
            DocumentSnapshot[] document1=new DocumentSnapshot[10];
           for(i=(Extras.cur-1)*10;i<=Math.min(Extras.cur*10-1,co-1);i++)
           {
                s[j]=new Label(Integer.toString(j+1));
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
                   int finalJ = j;

                   int finalI = i;
                   DocumentSnapshot finalDocument = document;
                   int finalI1 = i;
                   p[j].setOnAction(new EventHandler<ActionEvent>() {

                       @Override
                       public void handle(ActionEvent e) {
                           if(document1[finalJ].get("algorithm").toString().equals("01")||document1[finalJ].get("algorithm").toString().equals("11"))
                           {
                               TextInputDialog td = new TextInputDialog("Enter Key");
                               td.setHeaderText("Enter secret key");
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
                               }
                               else if(finalDocument.get(arr[co- finalI -1]+"-key").toString().equals(MD5.getMd5(pwd.getText().trim())))
                               {
                                    Extras.curm=co- finalI1 -1;
                                    Extras.user_key=pwd.getText().trim();
                                   try {
                                       new ReceiveMessage().start(new Stage());
                                   } catch (Exception scriptException) {
                                       scriptException.printStackTrace();
                                   }
                                   primaryStage.close();
                               }
                               else
                               {
                                   showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                           "Error!", "Invalid secret key");
                                   return;
                               }
                           }
                           else if(document1[finalJ].get("algorithm").toString().equals("00")||document1[finalJ].get("algorithm").toString().equals("10"))
                           {


                                   String asy=finalDocument.get(arr[co- finalI -1]+"-id").toString();

                                   DocumentReference docRef11 = db.collection("asymmetric").document(Extras.email);
                                   ApiFuture<DocumentSnapshot> future11 = docRef11.get();
                                   DocumentSnapshot document11 = null;
                                   try {
                                       document11 = future11.get();
                                   } catch (InterruptedException interruptedException) {
                                       interruptedException.printStackTrace();
                                   } catch (ExecutionException executionException) {
                                       executionException.printStackTrace();
                                   }
                                   if (document11.exists())
                                   {
                                       if(document11.get("id").toString().equals(asy))
                                       {
                                           TextInputDialog td = new TextInputDialog("Enter Passphrase");
                                           td.setHeaderText("Enter passphrase for decryption");
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
                                                       "Error!", "Enter passphrase");
                                               return;
                                           }
                                           if(MD5.getMd5(pwd.getText().trim()).equals(document11.get("passphrase").toString()))
                                           {
                                               Extras.curm=co- finalI1 -1;
                                               Extras.user_key=pwd.getText().trim();
                                               try {
                                                   new ReceiveMessage().start(new Stage());
                                               } catch (Exception scriptException) {
                                                   scriptException.printStackTrace();
                                               }
                                               primaryStage.close();
                                           }
                                           else
                                           {
                                               showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                       "Error!", "Passphrase not valid");
                                               return;
                                           }
                                       }
                                       else
                                       {


                                               Extras.curm=co- finalI1 -1;
                                               showAlert(Alert.AlertType.ERROR, grid.getScene().getWindow(),
                                                       "Alert!", "Passphrase for decryption changed");
                                               try {
                                                   new ReceiveMessage().start(new Stage());
                                               } catch (Exception scriptException) {
                                                   scriptException.printStackTrace();
                                               }
                                               primaryStage.close();


                                       }

                                   }
                                   else
                                   {

                                   }

                           }

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
                        new Receiver().start(new Stage());
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
                        new Receiver().start(new Stage());
                    } catch (Exception scriptException) {
                        scriptException.printStackTrace();
                    }
                    primaryStage.close();
                }
            });
        }
        else
        {
            Label Name = new Label("No Messages to show");
            grid.add(Name, 2, Extras.y+3);
        }

        /*Label Name = new Label("Name");
        grid.add(Name, 0, 4);

        final TextField nameField = new TextField();
        grid.add(nameField, 1, 4,5,1);

        Label userName = new Label("Email ID");
        grid.add(userName, 0, 6);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 6,5,1);

        Label pw = new Label("Password");
        grid.add(pw, 0, 8);

        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 8,5,1);

        Button btn = new Button("Sign Up");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 10);

        Button btn1 = new Button("Go to Login page");
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

                    try {
                        Extras.email=userTextField.getText().trim();
                        new Email().start(new Stage());
                    } catch (Exception scriptException) {
                        scriptException.printStackTrace();
                    }
                    primaryStage.close();
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