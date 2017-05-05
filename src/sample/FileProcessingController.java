package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.FileCopier;
import model.Operation;
import model.OurFiles;

import java.io.File;

/**
 * Created by ivangeel on 03.04.17.
 */
public class FileProcessingController {

    private Stage dialogStage;
    private ObservableList<OurFiles> ourFiles;
    private String path;
    @FXML
    Button okButton = new Button();
    @FXML
    Button cancelButton = new Button();
    @FXML
    TextField textField;

    public FileProcessingController() {
    }

    public void setDialogStage(Stage dialogStage, ObservableList<OurFiles> ourFiles, String path) {
        this.dialogStage = dialogStage;
        this.ourFiles = ourFiles;
        this.path = path;
    }

    @FXML
    private void initialize(){


        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = new File(path+"/"+textField.getText());
                file.mkdirs();
                ourFiles.add(new OurFiles(file));
                dialogStage.close();
            }
        });
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialogStage.close();
            }
        });
    }

    public void close(){
        dialogStage.close();
    }
}
