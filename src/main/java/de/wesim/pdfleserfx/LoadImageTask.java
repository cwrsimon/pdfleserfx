/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 *
 * @author cwrsi
 */
public class LoadImageTask extends Task<Image> {

    private final Path path;
    private final Consumer<Image> callback;

    public LoadImageTask(Path path, Consumer<Image> callback) {
        this.path = path;
        this.callback = callback;
    }
    
    @Override
    protected Image call() throws Exception {
        InputStream is = null;
        try {
            is = Files.newInputStream(path);
            return new Image(is);
        } catch (IOException ex) {
            // TODO Do something smart here
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void succeeded() {
        var value = getValue();
        Platform.runLater( () -> {
                callback.accept(value);    
        });
    }
    
    
   
}
