package sample;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class KeyRing extends Application {

    public static void main() {
        launch();
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

        Button btn_import = new Button("Import");
        HBox hb_import = new HBox(10);
        hb_import.setAlignment(Pos.CENTER);
        hb_import.getChildren().add(btn_import);
        grid.add(hb_import,4,Extras.y+1);

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width=  visualBounds.getWidth()/2;
        Scene scene = new Scene(grid, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        grid.setAlignment(Pos.TOP_CENTER);
        Text scenetitle = new Text("My contacts");
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
        grid.add(hb_view_sent, 10, 0);
        btn_home.setGraphic(viewSent_imageView);

        ArrayList<String> users= new ArrayList<String>();
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
            Label[] s=new Label[10];
            Label[] e=new Label[10];
            Button prev= new Button("Previous Page");
            Button next= new Button("Next Page");
            Label curp=new Label("Current Page - "+Extras.cur );

            Label[] ini = new Label[2];
            ini[0]=new Label("Sr.No");
            ini[1]=new Label("Email");

            grid.add(ini[0], 0, Extras.y+2);
            grid.add(ini[1], 1, Extras.y+2);
            int i,j,k,co;

            co=arr.length;
            int max=co/10;
            if(co%10>0)
            {
                max++;
            }
            j=0;
            for(i=(Extras.cur-1)*10;i<=Math.min(Extras.cur*10-1,co-1);i++)
            {
                s[j]=new Label(Integer.toString(j+1));
                grid.add(s[j], 0, Extras.y+3+j);
                e[j]=new Label(arr[i]);
                grid.add(e[j], 1, Extras.y+3+j);
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
                        new KeyRing().start(new Stage());
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
                        new KeyRing().start(new Stage());
                    } catch (Exception scriptException) {
                        scriptException.printStackTrace();
                    }
                    primaryStage.close();
                }
            });
        }
        else
        {
            Label Name = new Label("Nothing to show");
            grid.add(Name, 2, Extras.y+3);
        }




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

        btn_import.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                try {
                    new Import().start(new Stage());
                } catch (Exception scriptException) {
                    scriptException.printStackTrace();
                }
                primaryStage.close();
            }
        });
        primaryStage.show();
    }


}
