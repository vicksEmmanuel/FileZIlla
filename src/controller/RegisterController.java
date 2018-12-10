/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static filezilla.Clientele.primaryStage;
import filezilla.FileZilla;
import filezilla.Variables;
import java.io.File;
import java.io.FileNotFoundException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.PrintWriter;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author vicksemmanuel
 *//**
 * FXML Controller class
 *
 * @author vicksemmanuel
 */
public class RegisterController{
    
    
    MainController main;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField secretkey;
    @FXML
    private Label msg;
    @FXML
    private Button generateButton;
    public void setMain(MainController main) throws ClassNotFoundException, SQLException{
        this.main=main;
        initializeDB();
    }  
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    @FXML
    private void generate(ActionEvent event) throws FileNotFoundException, IOException{
        String key=generateSecretKey(8);
        secretkey.setText(key);
        showAlertWithHeaderText(key);
        generateButton.setDisable(true);
    }
    private void showAlertWithHeaderText(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Secret Key");
        alert.setHeaderText("Results:");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public static String generateSecretKey(int count){
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    @FXML
    private void register(ActionEvent event) throws SQLException,IOException, ClassNotFoundException {
        if(check(username.getText(), password.getText(), secretkey.getText())){
            insertQuery("Insert into users values(NULL,'"+username.getText()+"','"+password.getText()+"','"+secretkey.getText()+"')");
            File file=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla");
            file.mkdir();
            PrintWriter saveSecretKey=new PrintWriter(new File(System.getProperty("user.home")+"\\Desktop\\FileZilla\\"+username.getText()+"--secretkey.txt"));
            saveSecretKey.print(secretkey.getText());
            saveSecretKey.close();
            mainloader();
        }
    } 
    Statement statement;
    public void initializeDB() throws ClassNotFoundException, SQLException{
         Class.forName("com.mysql.jdbc.Driver");
         Connection connection = DriverManager.getConnection(Variables.LINK, Variables.USER, Variables.PASSWORD);
         statement= connection.createStatement();
    }
    public ResultSet query(String query) throws SQLException{
        ResultSet resultSet=statement.executeQuery(query);
        return resultSet;
    }
    public void insertQuery(String query) throws SQLException{
        statement.executeUpdate(query);
    }
    public boolean check(String username, String password, String secretkey) throws SQLException{
        if((username.length()>0) && (password.length()>0) && (secretkey.length() > 0)){
            ResultSet resultSet=statement.executeQuery("select * from users where username='"+username+"'");
            if(resultSet.next()){
                msg.setText("User Already Exits... Try Again");
                return false;
            }
            else{
                msg.setText("");
                return true;
            }
        }else{
            msg.setText("Invalid inputs... Try Again");
            return false;
        }
    } 
    public void mainloader() throws IOException, ClassNotFoundException, SQLException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/login.fxml"));
        AnchorPane mainPane=load.load();
        LoginController controller=load.getController();
        controller.setMain(this);
        Scene scene=new Scene(mainPane);
        primaryStage.setScene(scene);
    }
    public void back() throws IOException{
        FXMLLoader load=new FXMLLoader(FileZilla.class.getResource("/view/main.fxml"));
        AnchorPane mainPane=load.load();
        MainController controller=load.getController();
        controller.setMain(this);
        Scene scene=new Scene(mainPane);
        primaryStage.setScene(scene);
    }

    @FXML
    private void go_back(ActionEvent event) throws IOException {
        back();
    }
}
