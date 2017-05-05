package model;

import javafx.collections.ObservableList;

import java.io.*;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by ivangeel on 02.04.17.
 */

public class FileHandler {

    private Queue<File> queue = new PriorityQueue<>();

    public FileHandler() {
    }

    public static int copyFile(File input, File output){
        if(input.isFile()) {
            try (InputStream is = new FileInputStream(input); OutputStream os = new FileOutputStream(output);) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }
        return 0;
    }

    //дописать
    public static boolean deleteFiles(ObservableList<OurFiles> list){
        boolean b = false;
        for (OurFiles ourFile: list) {
            if (ourFile.getFile().isFile()) {
                if (ourFile.getFile().delete()) {
                    b = true;
                    System.out.println(ourFile.getFile().getAbsolutePath() + " - удален");
                } else b = false;
            }
        }
        return b;
    }
}
