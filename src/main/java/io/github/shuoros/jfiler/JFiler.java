package io.github.shuoros.jfiler;

import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;

public class JFiler {

    private Boolean lock;
    private Folder currentLocation;
    private final Folder homeLocation;
    private final Stack<Folder> frontLocation = new Stack<>();
    private final Stack<Folder> rearLocation = new Stack<>();

    public JFiler() {
        this("/", false);
    }

    public JFiler(String location) {
        this(location, false);
    }

    public JFiler(String location, Boolean lock) {
        this.homeLocation = new Folder(Paths.get(location));
        this.currentLocation = this.homeLocation;
        this.lock = lock;
    }

    public static JFiler open(String location){
        return new JFiler(location);
    }

    public static JFiler openInLockedHome(String location){
        return new JFiler(location, true);
    }

    public Folder getCurrentLocation(){
        return currentLocation;
    }

    public List<File> getList() {
        return currentLocation.getContains();
    }

    public File getFile(String location) throws IOException {
        return new File(Paths.get(location));
    }

    public Folder getFolder(String location) throws IOException {
        return new Folder(Paths.get(location));
    }

    public void openFolder(String location) {
        this.frontLocation.clear();
        this.rearLocation.push(this.currentLocation);
        this.currentLocation = new Folder(Paths.get(location));
    }

    public void goBackward(){
        this.frontLocation.push(this.currentLocation);
        this.currentLocation = this.rearLocation.pop();
    }

    public void goForward(){
        this.rearLocation.push(this.currentLocation);
        this.currentLocation = this.frontLocation.pop();
    }

    public void goUp(){
        this.frontLocation.push(this.currentLocation);
        this.currentLocation = this.currentLocation.getParentFolder();
    }

}
