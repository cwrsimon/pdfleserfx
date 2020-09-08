/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.frontend.mainview;

import de.wesim.pdfleserfx.helpers.ThrowingSupplier;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 *
 * @author cwrsi
 */
public class LoadImageTask extends Task<Image> {

    private final ThrowingSupplier<Image> getter;
    private final Consumer<Image> callback;

    public LoadImageTask(ThrowingSupplier<Image> getter, Consumer<Image> callback) {
        this.getter = getter;
        this.callback = callback;
    }
    // TODO Add error handler
    @Override
    protected Image call() throws Exception {
        return getter.get();
    }

    @Override
    protected void succeeded() {
        var value = getValue();
        Platform.runLater( () -> {
                callback.accept(value);    
        });
    }
    
    
   
}
