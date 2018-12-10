package filezilla;

import controller.MainController;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author vicksemmanuel
 */
public class Clientele extends Application {
    
    public static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage=primaryStage;
        mainloader();
    }
    public Stage getStage(){
        return primaryStage;
    }
    public void mainloader() throws IOException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/main.fxml"));
        AnchorPane mainPane=load.load();
        MainController controller=load.getController();
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
