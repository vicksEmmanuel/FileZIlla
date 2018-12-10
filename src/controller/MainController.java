/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import filezilla.Clientele;
import static filezilla.Clientele.primaryStage;
import filezilla.FileZilla;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author vicksemmanuel
 */
public class MainController{

      Clientele main;
      RegisterController main1;
      LoginController main2;
      public void setMain(Clientele main){
          this.main=main;
      }
      public void setMain(RegisterController main){
          this.main1=main;
      }
      public void setMain(LoginController main){
          this.main2=main;
      }
    @FXML
    private void registerBlock(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        registerloader();
    }

    @FXML
    private void loginBlock(ActionEvent event) throws IOException, ClassNotFoundException, SQLException{
        loginloader();
    }

    @FXML
    private void exitNow(ActionEvent event) {
        main.getStage().close();
        System.exit(0);
    }
    
    public void registerloader() throws IOException, ClassNotFoundException, SQLException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/register.fxml"));
        AnchorPane mainPane=load.load();
        RegisterController controller=load.getController();
        controller.setMain(this);
        Scene scene=new Scene(mainPane);
        primaryStage.setScene(scene);
    }
    
    public void loginloader() throws IOException, ClassNotFoundException, SQLException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/login.fxml"));
        AnchorPane mainPane=load.load();
        LoginController controller=load.getController();
        controller.setMain(this);
        Scene scene=new Scene(mainPane);
        primaryStage.setScene(scene);
    }
    
}
