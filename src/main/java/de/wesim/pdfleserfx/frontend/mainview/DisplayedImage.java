/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.frontend.mainview;

import de.wesim.pdfleserfx.helpers.ThrowingSupplier;
import de.wesim.pdfleserfx.backend.pageproviders.IPageProvider;
import de.wesim.pdfleserfx.backend.pageproviders.SampleImageProvider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
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
    private IPageProvider imageProvider;
    
    private SimpleIntegerProperty left_cut_property = new SimpleIntegerProperty();
    
    public DisplayedImage() {
        
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
     
//        Rectangle2D viewportRect = new Rectangle2D(40, 35, 110, 110);
//        //viewportRect.
//        setViewport(viewportRect);
    }

    DisplayedImage(SampleImageProvider imageProvider) {
        this();
        this.imageProvider = imageProvider;
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
        final ThrowingSupplier<Image> getter = () -> imageProvider.getFirstImage();
        loadImage(getter);
    }
    
    public void loadNextImage() {
        final ThrowingSupplier<Image> getter = () -> imageProvider.getNextImage();
        loadImage(getter);
    }
    
    public void loadPreviousImage() {
        final ThrowingSupplier<Image> getter = () -> imageProvider.getPrevImage();
        loadImage( getter );
    }
    
    // TODO In einen Service verlagern!
    private void loadImage(ThrowingSupplier<Image> getter ) {
        var task = new LoadImageTask(getter, image -> setImage(image));
        task.run();
    }
}


