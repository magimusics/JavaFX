package sample;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.*;

import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private TableView<OurFiles> fileField1;
    @FXML
    private TableView<OurFiles> fileField2;

    @FXML
    private TableColumn<OurFiles, String> first;
    @FXML
    private TableColumn<OurFiles, String> second;
    @FXML
    TextField textField1;
    @FXML
    Button buttonSearch;
    @FXML
    Button stopButton;

    ObservableList<OurFiles> ourFilesObservableList = FXCollections.observableArrayList();
    ObservableList<OurFiles> ourFilesCopy = FXCollections.observableArrayList();
    FileSearcher fileSearcher;
    Operation operation;

    @FXML
    private TableColumn<OurFiles, String> first2;
    @FXML
    private TableColumn<OurFiles, String> second2;

    @FXML
    MenuItem close;
    @FXML
    MenuItem deleteMenuItem = new MenuItem();
    @FXML
    MenuItem openDir;
    @FXML
    MenuItem renameFile;
    @FXML
    MenuItem copyFile;
    @FXML
    MenuItem moveFile;
    @FXML
    MenuItem copyFile2;
    @FXML
    MenuItem pasteFile;
    @FXML
    MenuItem pasteFile2;
    @FXML
    MenuItem moveFile2;
    @FXML
    MenuItem newDir;
    @FXML
    MenuItem newDir2;
    @FXML
    MenuItem deleteMenuItem2 = new MenuItem();

    @FXML
    ContextMenu contextMenu;
    @FXML
    ContextMenu contextMenu2;
    private File currentDirectory1;
    private File currentDirectory2;
    DataFormat dataFormat = new DataFormat("for file");

    private Main main;

    public Controller(){
    }

    public void setCellValues(TableColumn<OurFiles, String> right, TableColumn<OurFiles, String> left){
        right.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OurFiles, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<OurFiles, String> param) {
                ObservableValue<String> res = new SimpleObjectProperty(param.getValue().getFilename());
                return res;
            }
        });

        left.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OurFiles, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<OurFiles, String> param) {
                ObservableValue<String> res = new SimpleObjectProperty(param.getValue().getFile().length());
                return res;
            }
        });
    }

    @FXML
    public void deleteFile(TableView<OurFiles> ourFilesTableView, ObservableList<OurFiles> files){
        System.out.println("Delete row");
        ObservableList<OurFiles> p = ourFilesTableView.getSelectionModel().getSelectedItems();
        if(ourFilesTableView.getSelectionModel().getSelectedIndex()!=0)
            if(p!=null) {
                FileCopier fileCopier = new FileCopier(p,"", Operation.DELETE);
                fileCopier.start();
                try {
                    fileCopier.join();
                }catch (InterruptedException e){e.printStackTrace();}
                files.removeAll(p);
                ourFilesTableView.setItems(files);
            }
    }

    @FXML
    private void initialize(){

        setCellValues(first, second);
        setCellValues(first2, second2);
        openDir.setVisible(false);
        if(ourFilesCopy.size()==0){
            pasteFile.setDisable(true);
            pasteFile2.setDisable(true);
        }

        System.out.println(ourFilesCopy.size());
        fileField1.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteFile(fileField1, main.getObservableList());
            }
        });

        deleteMenuItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteFile(fileField2, main.getObservableList2());
            }
        });


        fileField1.setOnDragDetected(new EventHandler<MouseEvent>() { //drag
            @Override
            public void handle(MouseEvent event) {

                OurFiles selected = fileField1.getSelectionModel().getSelectedItem();
                if(selected !=null){
                    Dragboard db = fileField1.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.put(dataFormat, selected);
                    db.setContent(content);
                    event.consume();
                }
            }
        });

        fileField2.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().hasContent(dataFormat)){
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        fileField2.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (event.getDragboard().hasContent(dataFormat)) {
                    OurFiles of = (OurFiles) db.getContent(dataFormat);
                    String path = currentDirectory2+"/"+of.getFilename();
                    System.out.println(path);
                    if(FileHandler.copyFile(of.getFile(), new File(path))>0) {
                        main.getObservableList2().add(of);
                        fileField2.setItems(main.getObservableList2());
                        success = true;
                    }
                    else success = false;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(fileField1.getSelectionModel().getSelectedIndex()==0)
                    deleteMenuItem.setDisable(true);
                else deleteMenuItem.setDisable(false);
            }
        });

        contextMenu2.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(fileField2.getSelectionModel().getSelectedIndex()==0)
                    deleteMenuItem2.setDisable(true);
                else deleteMenuItem2.setDisable(false);
            }
        });

        buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ourFilesObservableList.removeAll(ourFilesObservableList);
                System.out.println("Button1 is pressed");
                String text = textField1.getText();
                fileSearcher = new FileSearcher(main.getCurrent(), text, ourFilesObservableList, true);
                fileSearcher.start();
                ourFilesObservableList.add(new OurFiles(main.getCurrent(),".."));
                fileField1.setItems(ourFilesObservableList);
                openDir.setVisible(true);

            }
        });
        openDir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                OurFiles of = fileField1.getSelectionModel().getSelectedItem();

                System.out.println("selected path: "+of.getFile().getPath());
                main.setNewPath(new File(of.getFile().getParent()), 2);
                fileField2.setItems(main.getObservableList2());
            }
        });

        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fileSearcher.stopSearching();
            }
        });

        copyFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(currentDirectory1.getAbsolutePath());
                pasteFile.setDisable(false);
                pasteFile2.setDisable(false);
                operation = Operation.COPY;
                ourFilesCopy.removeAll(ourFilesCopy);
                ourFilesCopy.addAll(fileField1.getSelectionModel().getSelectedItems());
                System.out.println(ourFilesCopy.size());
            }
        });
        pasteFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                System.out.println(currentDirectory1.getAbsolutePath());
                copyOrMove(operation, currentDirectory1);
            }
        });

        moveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pasteFile.setDisable(false);
                pasteFile2.setDisable(false);
                operation = Operation.MOVE;
                ourFilesCopy.removeAll(ourFilesCopy);
                ourFilesCopy.addAll(fileField1.getSelectionModel().getSelectedItems());
                System.out.println(ourFilesCopy.size());
            }
        });

        copyFile2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pasteFile.setDisable(false);
                pasteFile2.setDisable(false);
                operation = Operation.COPY;
                ourFilesCopy.removeAll(ourFilesCopy);
                ourFilesCopy.addAll(fileField2.getSelectionModel().getSelectedItems());
                System.out.println(ourFilesCopy.size());
            }
        });
        pasteFile2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                System.out.println(currentDirectory2.getAbsolutePath());
                FileCopier fileCopier = new FileCopier(ourFilesCopy, currentDirectory2.getAbsolutePath(), operation);
                System.out.println(currentDirectory2.getAbsolutePath());
                fileCopier.start();
                try {
                    fileCopier.join();
                }catch (InterruptedException e){e.printStackTrace();}
                main.setNewPath(currentDirectory2, 2);
                fileField2.setItems(main.getObservableList2());
            }
        });

        moveFile2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pasteFile.setDisable(false);
                pasteFile2.setDisable(false);
                operation = Operation.MOVE;
                ourFilesCopy.removeAll(ourFilesCopy);
                ourFilesCopy.addAll(fileField2.getSelectionModel().getSelectedItems());
                System.out.println(ourFilesCopy.size());
            }
        });

        newDir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.newDir(currentDirectory1.getAbsolutePath(), Operation.FISRT);
                fileField1.setItems(main.getObservableList());
            }
        });

        newDir2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.newDir(currentDirectory2.getAbsolutePath(), Operation.SECOND);
                fileField1.setItems(main.getObservableList());
            }
        });

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
    }

    private void copyOrMove(Operation operation, File current){
        FileCopier fileCopier = new FileCopier(ourFilesCopy, current.getAbsolutePath(), operation);
        System.out.println(current.getAbsolutePath());
        fileCopier.start();
        try {
            fileCopier.join();
        }catch (InterruptedException e){e.printStackTrace();}
        main.setNewPath(current, 1);
        fileField1.setItems(main.getObservableList());
        //ourFilesCopy.removeAll(ourFilesCopy);
    }

    @FXML
    private void mouseDoubleClick2(){
        fileField2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    @SuppressWarnings("rawtypes")
                    OurFiles pos = fileField2.getSelectionModel().getSelectedItem();
                    if(pos.getFile().isFile()) {
                        System.out.println(pos.getFile().getName() + " - файл");
                        try {
                            ProcessBuilder processBuilder = new ProcessBuilder("open", pos.getFile().getAbsolutePath());
                            processBuilder.start();

                            System.out.println("open "+pos.getFile().getAbsolutePath());
                            System.out.println(System.getProperty("os.name"));
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println(pos.getFile().getAbsolutePath() + " - папка");
                        currentDirectory2=pos.getFile();
                        main.setNewPath(pos.getFile(), 2);
                        fileField2.setItems(main.getObservableList2());
                    }
                }
            }
        });
    }

    @FXML
    private void mouseDoubleClick(){
        fileField1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    @SuppressWarnings("rawtypes")
                    OurFiles pos = fileField1.getSelectionModel().getSelectedItem();
                    if(pos.getFile().isFile()) {
                        System.out.println(pos.getFile().getName() + " - файл");
                        try {
                            ProcessBuilder processBuilder = new ProcessBuilder("open", pos.getFile().getAbsolutePath());
                            processBuilder.start();

                            System.out.println("open "+pos.getFile().getAbsolutePath());
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println(pos.getFile().getAbsolutePath() + " - папка");
                        currentDirectory1=pos.getFile();
                        main.setNewPath(pos.getFile(), 1);
                        fileField1.setItems(main.getObservableList());
                    }
                }
            }
        });
    }

    public void setMain(Main mainApp) {
        this.main = mainApp;

        // Добавление в таблицу данных из наблюдаемого списка
        fileField1.setItems(mainApp.getObservableList());
        fileField2.setItems(mainApp.getObservableList2());
    }
}
