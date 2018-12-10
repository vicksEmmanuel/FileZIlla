/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filezilla;

import controller.TcController;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author vicksemmanuel
 */
public class FileZilla extends Application {
    
    public static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage=primaryStage;
        mainloader();
        setUp();
    }
    public void setUp() throws IOException{
        File f=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla");
        f.mkdir();
        f=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla\\Sent");
        f.mkdir();
        f=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla\\Received");
        f.mkdir();
        f=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla\\Decrypted");
        f.mkdir();
        File f2=new File("recievedbin.dat");
        f2.createNewFile();
        File f3=new File("sentbin.dat");
        f3.createNewFile();
    }
    public Stage getStage(){
        return primaryStage;
    }
    public void mainloader() throws IOException, ClassNotFoundException, SQLException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/tc.fxml"));
        AnchorPane mainPane=load.load();
        TcController controller=load.getController();
        controller.setMain(this);
        Scene scene=new Scene(mainPane);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setResizable(false);
        primaryStage.setTitle("FileZilla 1.0");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/WEBPIX2.jpg")));
        primaryStage.show();
    }
    public static void main(String[] args) { 
        launch(args);
    }
}
