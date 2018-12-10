/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import filezilla.FileZilla;
import filezilla.Variables;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author vicksemmanuel
 */
public class TcController{

    FileZilla main;
    static Statement statement;
    @FXML
    private TextArea eagle_box;
    public TextArea getBox(){
        return eagle_box;
    }
    public void setMain(FileZilla main) throws ClassNotFoundException, SQLException{
        this.main=main;
        initializeDB();
        setUp();
    }
    public void initializeDB() throws ClassNotFoundException, SQLException{
         Class.forName("com.mysql.jdbc.Driver");
         Connection connection = DriverManager.getConnection(Variables.LINK, Variables.USER, Variables.PASSWORD);
         statement= connection.createStatement();
    }
    public static ResultSet query(String query) throws SQLException{
        ResultSet resultSet=statement.executeQuery(query);
        return resultSet;
    }
    public void setUp(){
        new Thread(()->{
            try {
                ServerSocket serverSocket=new ServerSocket(8000);
                Platform.runLater(()->{
                    eagle_box.appendText("Server started at "+ new Date()+"\n"); 
                });
                while(true){
                    Socket user1=serverSocket.accept();
                    Platform.runLater(()->{
                        eagle_box.appendText(user1.getInetAddress().getHostAddress()+" requesting connection at "+ new Date()+"\n"); 
                    });
                    Socket user2=serverSocket.accept();
                    Platform.runLater(()->{
                        eagle_box.appendText(user2.getInetAddress().getHostAddress()+" requesting connection at "+ new Date()+"\n"); 
                    });
                    new Thread(new HandleASession(user1,user2)).start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    class HandleASession implements Runnable{
        private Socket user1;
        private Socket user2;
        private DataInputStream fromUser1;
        private DataOutputStream toUser1;
        private DataInputStream fromUser2;
        private DataOutputStream toUser2;
        private String username1,username2,password1,password2,secretkey1,secretkey2,sessionkey;
        private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        public HandleASession(Socket user1, Socket user2){
            this.user1=user1;
            this.user2=user2;
        }
        public ResultSet tweek(String username, String password) throws SQLException{
            return query("select * from users where username='"+username+"' and password='"+password+"'");
        }
        public String generateSessionKey(int count){
            StringBuilder builder = new StringBuilder();
            while (count-- != 0) {
                int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
                    builder.append(ALPHA_NUMERIC_STRING.charAt(character));
            }
            return builder.toString();
        }
        public void sendFileName(DataOutputStream toUser2,String fileName) throws IOException{
            toUser2.writeUTF(fileName);
            toUser2.flush();
        }
        public void sendTheFile(DataOutputStream toUser2,BufferedOutputStream outputStreamToUser2,File file) throws FileNotFoundException, IOException{
            BufferedInputStream fileStream=new BufferedInputStream(new FileInputStream(file));
            long size=(int) file.length();
            byte bytearray[]=new byte [1024];

            int read, numberOfBytesCopied=0;

            toUser2.writeUTF(Long.toString(size)); 
            toUser2.flush(); 

            while((read=fileStream.read())!=-1){
                outputStreamToUser2.write((byte)read);
                numberOfBytesCopied++;
            }
            
            fileStream.close();
            outputStreamToUser2.flush();

        }
        @Override
        public void run() {
            try{
                DataInputStream fromUser1=new DataInputStream(user1.getInputStream());
                DataOutputStream toUser1=new DataOutputStream(user1.getOutputStream());
                DataInputStream fromUser2=new DataInputStream(user2.getInputStream());
                DataOutputStream toUser2=new DataOutputStream(user2.getOutputStream());
                BufferedInputStream inputStreamFromUser1=new BufferedInputStream(user1.getInputStream());
                BufferedOutputStream outputStreamToUser1=new BufferedOutputStream(user1.getOutputStream());
                BufferedInputStream inputStreamFromUser2=new BufferedInputStream(user2.getInputStream());
                BufferedOutputStream outputStreamToUser2=new BufferedOutputStream(user2.getOutputStream());
                username1=fromUser1.readUTF();
                password1= fromUser1.readUTF();
                username2=fromUser2.readUTF();
                password2= fromUser2.readUTF();
                toUser1.writeUTF(username2);
                toUser2.writeUTF(username1);
                toUser1.writeUTF("Sender");
                toUser2.writeUTF("Receiver");
                sessionkey=generateSessionKey(8);
                toUser1.writeUTF(sessionkey);
                toUser2.writeUTF(sessionkey);
                Platform.runLater(()->{
                     eagle_box.appendText(username1+" is connected to "+username2+"at"+ new Date()+"\n"); 
                });
                ResultSet var=tweek(username1,password1);
                if(var.next()){
                    secretkey1=var.getString("secretkey");
                }
                var=tweek(username2,password2);
                if(var.next()){
                    secretkey2=var.getString("secretkey");
                }
                while(true){
                    String fileName=fromUser1.readUTF();
                    Platform.runLater(()->{
                            eagle_box.appendText(fileName+"\n"); 
                    });
                    
                    long size=Long.parseLong(fromUser1.readUTF());
                    Platform.runLater(()->{
                            eagle_box.appendText("File Size: "+(size/(1024*1024))+" MB"+"\n"); 
                    });
                    
                    byte bytearray[]=new byte [1024];
                    BufferedOutputStream output=new BufferedOutputStream(new FileOutputStream(new File(fileName),true));
                    
                    int r,ee=0;
                    while(ee!=size){
                        r=inputStreamFromUser1.read();
                        output.write((byte)r);
                        ++ee;
                    }
                    Platform.runLater(()->{
                            eagle_box.appendText("Transfer complete"+"\n"); 
                    });
                    output.close();
                    
                    File decryptedFile=new File("decrypted__"+fileName);
                    decryptedFile.createNewFile();
                    File encryptedFile=new File(fileName);
                    try{
                        CryptoUtils.decrypt("01010101"+secretkey1, encryptedFile, decryptedFile);
                        encryptedFile.delete();
                        encryptedFile=new File(fileName);
                        CryptoUtils.encrypt("01010101"+sessionkey, decryptedFile, encryptedFile);
                        decryptedFile.delete();
                        sendFileName(toUser2,fileName);
                        sendTheFile(toUser2,outputStreamToUser2,encryptedFile);
                        encryptedFile.delete();
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

}