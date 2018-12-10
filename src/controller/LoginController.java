/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static filezilla.Clientele.primaryStage;
import filezilla.FileZilla;
import filezilla.Variables;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author vicksemmanuel
 */
public class LoginController{

    MainController main;
    RegisterController main2;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label msg;
    @FXML
    private TextField secretkey;
    public void setMain(MainController main) throws ClassNotFoundException, SQLException{
        this.main=main;
        setUp();
    }     
    public void setMain(RegisterController main2) throws ClassNotFoundException, SQLException{
        this.main2=main2;
        setUp();
    } 
    public void setUp() throws ClassNotFoundException, SQLException{
        initializeDB();
    }

    @FXML
    private void login(ActionEvent event) throws SQLException, IOException {
        if(check(username.getText(), password.getText(), secretkey.getText())){
            transferView();
        }
    }
    Statement statement;
    public void initializeDB() throws ClassNotFoundException, SQLException{
         Class.forName("com.mysql.jdbc.Driver");
         Connection connection = DriverManager.getConnection(Variables.LINK, Variables.USER, Variables.PASSWORD);
         statement= connection.createStatement();
    }
    public boolean check(String username, String password, String secretkey) throws SQLException{
        if((username.length()>0) && (password.length()>0) && (secretkey.length() > 0)){
            ResultSet resultSet=statement.executeQuery("select * from users where username='"+username+"' and password='"+password+"' and secretkey='"+secretkey+"'");
            if(resultSet.next()){
                return true;
            }
            else{
                msg.setText("User does not exist");
                return false;
            }
        }else{
            msg.setText("Invalid inputs... Try Again");
            return false;
        }
    } 
    public void transferView() throws IOException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/transfer.fxml"));
        AnchorPane mainPane=load.load();
        TransferController controller=load.getController();
        controller.setMain(this,username.getText(),password.getText(),secretkey.getText());
        Scene scene=new Scene(mainPane);
        primaryStage.setScene(scene);
    }

    @FXML
    private void go_back(ActionEvent event) throws IOException {
        back();
    }
    public void back() throws IOException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/main.fxml"));
        AnchorPane mainPane=load.load();
        MainController controller=load.getController();
        controller.setMain(this);
        Scene scene=new Scene(mainPane);
        primaryStage.setScene(scene);
    }
}
