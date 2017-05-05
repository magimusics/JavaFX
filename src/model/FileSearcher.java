package model;

import javafx.collections.ObservableList;

import java.io.File;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by ivangeel on 03.04.17.
 */
public class FileSearcher extends Thread{
    private Queue<File> queue = new PriorityQueue<>();
    private boolean stop;
    private File file;
    private String name;
    private ObservableList<OurFiles> ourFiles;

    public FileSearcher(File file, String name, ObservableList<OurFiles> ourFiles, boolean start) {
        this.file = file;
        this.name = name;
        this.ourFiles = ourFiles;
        this.stop = !start;
    }

    public void stopSearching() {
        this.stop = true;
    }

    @Override
    public void run() {
        Collections.addAll(queue, file.listFiles());
        while (!queue.isEmpty()) {
            if(!stop) {
                if (queue.peek().isFile()) {
                    if (queue.peek().getName().indexOf(name)!=-1) {
                        ourFiles.add(new OurFiles(queue.poll()));
                    }
                    queue.remove();
                } else {
                    File[] files = queue.poll().listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (f != null)
                                queue.add(f);
                        }
                    }
                }
            }
            else queue.removeAll(queue);
        }
    }

}
