package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Operation;
import model.OurFiles;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private ObservableList<OurFiles> observableList = FXCollections.observableArrayList();
    private ObservableList<OurFiles> observableList2 = FXCollections.observableArrayList();
    private File current;
    private File current2;

    public Main(){
        setNewPath(new File("/Users/ivangeel/Documents"), 1);
        setNewPath(new File("/"), 2);
    }

    public void setNewPath(File newFile, int n){
        ObservableList<OurFiles> list = null;
        if(n==1) {
            current = newFile;
            list = observableList;
        }
        if(n==2) {
            current2 = newFile;
            list = observableList2;
        }
        File[] files = newFile.listFiles();
        String mko = newFile.getAbsolutePath();
        list.removeAll(list);
        list.add(new OurFiles(new File(mko.substring(0, mko.lastIndexOf("/")>1?mko.lastIndexOf("/"):mko.lastIndexOf("/")+1)), ".."));
        for(File f: files){
            //System.out.println(f.getName());
            list.add(new OurFiles(f));
        }
    }

    public File getCurrent2() {
        return current2;
    }

    public File getCurrent() {
        return current;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<OurFiles> getObservableList() {
        return observableList;
    }

    public ObservableList<OurFiles> getObservableList2() {
        return observableList2;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("File Manager");
        initRootLayout();
    }

    public void initRootLayout(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("sample.fxml"));
            AnchorPane anchorPane = loader.load();
            Controller controller = loader.getController();
            controller.setMain(this);

            Scene scene = new Scene(anchorPane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean newDir(String path, Operation operation) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("newDir.fxml"));
            GridPane page = (GridPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("File Processing");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            FileProcessingController controller = loader.getController();
            if(operation==Operation.FISRT)
                controller.setDialogStage(dialogStage, getObservableList(), path);
            if(operation==Operation.SECOND)
                controller.setDialogStage(dialogStage, getObservableList2(), path);
            dialogStage.show();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
