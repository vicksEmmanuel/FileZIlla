/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static filezilla.FileZilla.primaryStage;
import filezilla.Variables;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author vicksemmanuel
 */
public class TransferController{

    LoginController main;
    String host=Variables.LOCALHOST;
    String username,password,secretkey,encryptName,fileName,sessionKey,receivedFile,single_name;
    boolean received=false;
    File encryptedFile;
    DataInputStream fromServer;
    DataOutputStream toServer;
    BufferedOutputStream outputStream;
    BufferedInputStream inputStream;
    public FileChooser fileChooser;
    boolean send=false;
    public File inputFile;
    
    ListView list=new ListView();
    LinkedList fileList=new LinkedList<String>();
    
    ListView list_r=new ListView();
    LinkedList fileList_r=new LinkedList<String>();
    
    @FXML
    private Label user_connected_to;
    @FXML
    private Label user_name;
    @FXML
    private TextArea openTextBox;
    @FXML
    private TextArea openEncryptText;
    @FXML
    private Button chooseAFile;
    @FXML
    private Label status;
    @FXML
    private Tab transfer;
    @FXML
    private Tab history;
    @FXML
    private TabPane tab;
    @FXML
    private TextArea box;
    @FXML
    private AnchorPane listing;
    @FXML
    private AnchorPane listing_r;
    @FXML
    private Button deep;
    @FXML
    private Button iwantosend;
    public void setMain(LoginController main, String username,String password,String secretkey) throws IOException{
        this.main=main;
        this.username=username;
        this.password=password;
        this.secretkey=secretkey;
        user_name.setText(username);
        connectToServer();
        list.setPrefHeight(240);
        list.setPrefWidth(290);
        list_r.setPrefHeight(240);
        list_r.setPrefWidth(290);
        ObservableList<Label> fileNameHolder=FXCollections.observableArrayList();
        ObservableList<Label> fileNameHolder_r=FXCollections.observableArrayList();
        for(Object temp:getFiles().toArray()){
                Label file=new Label((String)temp);
                //file.setOnMouseClicked(new Action(file));
                fileNameHolder.add(file);
        }
        list.setPadding(new Insets(0,0,70,0));
        list.setItems( fileNameHolder );
        listing.getChildren().add(list);
        
        for(Object temp:getFiles_r().toArray()){
                Label file=new Label((String)temp);
                //file.setOnMouseClicked(new Action(file));
                fileNameHolder_r.add(file);
        }
        list_r.setPadding(new Insets(0,0,70,0));
        list_r.setItems( fileNameHolder_r );
        listing_r.getChildren().add(list_r);
    }
    
    public void writeToData(String fileName) throws FileNotFoundException, IOException{
        DataOutputStream output=new DataOutputStream(new FileOutputStream("sentbin.dat",true));
        output.writeUTF(fileName);
        output.close();
        Label file=new Label((String)fileName);
        //file.setOnMouseClicked(new Action(file));
        list.setPadding(new Insets(0,0,70,0));
        list.getItems().add(file);
        list.refresh();
    }
    
    public void writeToData_r(String fileName) throws FileNotFoundException, IOException{
        DataOutputStream output=new DataOutputStream(new FileOutputStream("recievedbin.dat",true));
        output.writeUTF(fileName);
        output.close();
        Label file=new Label((String)fileName);
        //file.setOnMouseClicked(new Action(file));
        list_r.setPadding(new Insets(0,0,70,0));
        list_r.getItems().add(file);
        list_r.refresh();
    }
    
    public LinkedList getFiles() throws FileNotFoundException, IOException{
        try{
            DataInputStream input=new DataInputStream(new FileInputStream("sentbin.dat"));
            while(true){
                String y=input.readUTF();
                fileList.add(y);
            }
        }catch(EOFException ex){
            System.out.println("End of Line");
        }
        return fileList;
    }
    public LinkedList getFiles_r() throws FileNotFoundException, IOException{
        try{
            DataInputStream input=new DataInputStream(new FileInputStream("recievedbin.dat"));
            while(true){
                String y=input.readUTF();
                fileList_r.add(y);
            }
        }catch(EOFException ex){
            System.out.println("End of Line");
        }
        return fileList_r;
    }
    
    private void connectToServer(){
        try{
            Socket socket=new Socket(host, 8000);
            fromServer=new DataInputStream(socket.getInputStream());
            toServer=new DataOutputStream(socket.getOutputStream());
            outputStream=new BufferedOutputStream(socket.getOutputStream());
            inputStream=new BufferedInputStream(socket.getInputStream());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        new Thread(()->{
           try{
               toServer.writeUTF(username);
               toServer.writeUTF(password);
               String username_of_sender=fromServer.readUTF();
               String stat=fromServer.readUTF();
               sessionKey=fromServer.readUTF();
               System.out.println(sessionKey+"--- session key");
               Platform.runLater(()->{
                  user_connected_to.setText("Connected to "+username_of_sender); 
                  status.setText(stat);
                  if(stat.equalsIgnoreCase("Receiver")){
                      transfer.setDisable(true);
                      tab.getSelectionModel().select(history);
                  }
               });
               
               new Thread(new Receiver()).start();
           }catch(Exception ex){
               ex.printStackTrace();
           }
        }).start();
    }

    @FXML
    private void openAndSelect(ActionEvent event) throws FileNotFoundException, IOException {
        fileChooser=new FileChooser();
        fileChooser.setTitle("Select A File to Send");
        inputFile=fileChooser.showOpenDialog(primaryStage);
        String fileForData="";
        if(inputFile!=null){
            fileForData=inputFile.getAbsolutePath();
            String ext=fileForData.substring(fileForData.lastIndexOf(".")+1, fileForData.length());
            openTextBox.setText("");
            File f=new File(fileForData);
            Scanner input=new Scanner(f);
            openTextBox.setText(inputFile.getAbsolutePath()+"\n\n\n");
            while(input.hasNext()){
                openTextBox.appendText(input.nextLine()+"\n");
            }
            input.close();
            chooseAFile.setDisable(true);
            File fila=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla\\Sent");
            fileName=inputFile.getName();
            encryptName=fila.getAbsolutePath()+"\\"+inputFile.getName();
            encryptedFile=new File(encryptName);
            encryptedFile.createNewFile();
            try{
                CryptoUtils.encrypt("01010101"+secretkey, inputFile, encryptedFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();                
                FileInputStream in = new FileInputStream(encryptedFile);
                DataOutputStream out=new DataOutputStream(baos);
                int val;
                while((val=in.read())!=-1){
                    out.write(val);
                }
                byte[] xxx=baos.toByteArray();
                String encoded = Base64.getEncoder().encodeToString(xxx);
                openEncryptText.setText(encoded);
                in.close();
                send=true;
                writeToData(fileForData);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void sendEncryptedFile(ActionEvent event) throws IOException {
        if(send){
            sendFileName(fileName);
            sendTheFile(encryptedFile);
        }
    }

    @FXML
    private void cancelTransfer(ActionEvent event) {
        if(send){
            send=false;
            File encryptedFile=new File(encryptName);
            encryptedFile.delete();
            chooseAFile.setDisable(false);
            openEncryptText.setText("");
            openTextBox.setText("");
        }
    }
    public void sendFileName(String fileName) throws IOException{
        toServer.writeUTF(fileName);
        toServer.flush();
    }
    public void sendTheFile(File file) throws FileNotFoundException, IOException{
        BufferedInputStream fileStream=new BufferedInputStream(new FileInputStream(file));
        long size=(int) file.length();
        byte bytearray[]=new byte [1024];
        
        int read, numberOfBytesCopied=0;
        
        toServer.writeUTF(Long.toString(size)); 
        toServer.flush(); 
        
        while((read=fileStream.read())!=-1){
            outputStream.write((byte)read);
            numberOfBytesCopied++;
        }
        System.out.println(numberOfBytesCopied);
        
        fileStream.close();
        outputStream.flush();
    }

    @FXML
    private void decryptReceivedFile(ActionEvent event) throws CryptoException, IOException, FileNotFoundException {
        if(received){
            System.out.println(receivedFile);
            File encryptedFile=new File(receivedFile);
            File fila=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla\\Decrypted");
            String decryptName=fila.getAbsolutePath()+"\\"+single_name;
            
            File decryptedFile=new File(decryptName);
            decryptedFile.createNewFile();
            CryptoUtils.decrypt("01010101"+sessionKey, encryptedFile, decryptedFile);

            Scanner input=new Scanner(decryptedFile);
            box.setText(decryptedFile.getAbsolutePath()+"\n\n\n");
            while(input.hasNext()){
                box.appendText(input.nextLine()+"\n");
            }
            input.close();
            writeToData_r(decryptedFile.getAbsolutePath());
        }
    }
    
    class Receiver implements Runnable{

        @Override
        public void run() {
            while(true){
                          try {
                            String fileName=fromServer.readUTF();
                            System.out.println(fileName);
                            single_name=fileName;
                              
                            long size=Long.parseLong(fromServer.readUTF());
                            System.out.println ("File Size: "+(size/(1024*1024))+" MB");
                              
                            File f=new File(System.getProperty("user.home")+"\\Desktop\\FileZilla\\Received");
                            f.mkdir();
                            fileName=f.getAbsolutePath()+"\\"+fileName;
                            receivedFile=fileName;
                            
                            byte bytearray[]=new byte [1024];
                            
                            File fila=new File(fileName);
                            fila.createNewFile();
                            BufferedOutputStream output=new BufferedOutputStream(new FileOutputStream(fila,true));
                    
                            int r,ee=0;
                            while(ee!=size){
                                r=inputStream.read();
                                output.write((byte)r);
                                ++ee;
                            }
                            System.out.println("Transfer complete");
                            output.close();
                            
                            
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();                
                            FileInputStream in = new FileInputStream(fileName);
                            DataOutputStream out=new DataOutputStream(baos);
                            int val;
                            while((val=in.read())!=-1){
                                out.write(val);
                            }
                            byte[] xxx=baos.toByteArray();
                            String encoded = Base64.getEncoder().encodeToString(xxx);
                            box.setText(encoded);
                            in.close();
                            received=true;
                          } catch (IOException ex) {
                              ex.printStackTrace();
                          }
                      }
        }
        
    }
}
