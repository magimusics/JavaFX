package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.Serializable;

/**
 * Created by ivangeel on 31.03.17.
 */
public class OurFiles implements Serializable{

    private final File file;
    private final String filename;

    public OurFiles(){
        this(null,null);
    }

    public OurFiles(File file, String filename) {
        this.file = file;
        this.filename = filename;
    }

    public OurFiles(File file) {
        this.file = file;
        this.filename = file.getName();
    }

    public String getFilename(){
        return filename;
    }

    public File getFile() {
        return file;
    }
}
