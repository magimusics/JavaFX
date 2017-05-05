package model;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Controller;
import sample.FileProcessingController;
import sample.Main;

import java.io.*;

/**
 * Created by ivangeel on 03.04.17.
 */
public class FileCopier extends Thread {

    private boolean stop;
    private Operation operation;
    private String name;
    private ObservableList<OurFiles> ourFiles;

    @FXML
    Button stopButton;

    public FileCopier(ObservableList<OurFiles> files, String path, Operation operation){
        ourFiles = files;
        name = path;
        this.operation = operation;
        System.out.println("FileCopier: "+ourFiles.size());
    }

    public void stopProcessing() {
        this.stop = true;
    }
    public boolean getStop(){return stop;}

    @Override
    public void run() {
        if(operation==Operation.COPY)
            copy();
        if(operation==Operation.MOVE)
            move();
        if(operation==Operation.DELETE)
            delete();

    }

    private void copy(){
        System.out.println("WORKING!!!" + ourFiles.size());
        for (OurFiles input: ourFiles) {
            if(!stop) {
                //System.out.println(input.getFile().getName());
                if (input.getFile().isFile()) {
                    File output = new File(name + "/" + input.getFile().getName());
                    try (InputStream is = new FileInputStream(input.getFile()); OutputStream os = new FileOutputStream(output);) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        stop = true;
    }

    private void move(){
        for (OurFiles input: ourFiles) {
            if(!stop) {
                if (input.getFile().isFile()) {
                    File output = new File(name + "/" + input.getFile().getName());
                    try (InputStream is = new FileInputStream(input.getFile()); OutputStream os = new FileOutputStream(output);) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    input.getFile().delete();
                }
            }
        }
    }
    private void delete(){
        for (OurFiles input: ourFiles) {
            input.getFile().delete();
        }
    }
}
