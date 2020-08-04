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
import java.nio.file.Paths;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 *
 * @author cwrsi
 */
public class DisplayedImage extends ImageView {

    private ColorInput rect;
    
    private final SimpleObjectProperty<Color> colorProperty = 
            new SimpleObjectProperty<>(Color.web("#f3efc1"));
    
    public DisplayedImage() {

//        Image image = new Image(stream);
        
//        setImage(image);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);
        
        var blend = createBlend();
//        getImage().widthProperty().addListener((obs, oldV, newV) -> {
//            System.out.println("NewV:" + newV);
//        });
        fitWidthProperty().addListener((obs, oldV, newV) -> {
            System.out.println("NewV:" + newV);
        });
        setEffect(blend);

        colorProperty.addListener((o,old, newV) -> {
            this.rect.setPaint(newV);
        });
        
    }
    
    private Blend createBlend() {
        var b = new Blend();
        b.setMode(BlendMode.MULTIPLY);
        this.rect = new ColorInput();
        rect.setX(0);
        rect.setY(0);
        rect.setPaint(colorProperty.getValue());
        rect.widthProperty().bind(fitWidthProperty());
        rect.heightProperty().bind(fitHeightProperty());
        b.setBottomInput(rect);
        return b;
    }

    public SimpleObjectProperty<Color> getColorProperty() {
        return colorProperty;
    }
    
    public void loadFirstImage() {
        var path = ImageProvider.getInstance().getFirstImage();
        loadImage(path);
    }
    
    public void loadNextImage() {
        var path = ImageProvider.getInstance().getNextImage();
        loadImage(path);
    }
    
    public void loadPreviousImage() {
        var path = ImageProvider.getInstance().getPrevImage();
        loadImage(path);
    }
    
    // TODO In einen Service verlagern!
    private void loadImage(Path path) {
        var task = new LoadImageTask(path, image -> setImage(image));
        task.run();
    }
}
