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
    private Integer numer_of_pages = -1;
    private Integer current_page = -1;
        
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

    DisplayedImage(IPageProvider imageProvider) {
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
//        rect.widthProperty().bind(fitWidthProperty());
//        rect.heightProperty().bind(fitHeightProperty());
        b.setBottomInput(rect);
        return b;
    }

    public SimpleObjectProperty<Color> getColorProperty() {
        return colorProperty;
    }
    
    public void loadFirstImage() {
        var number_of_pages_opt = imageProvider.getNumberOfPages();
        if (number_of_pages_opt.isPresent()) {
            this.numer_of_pages = number_of_pages_opt.get();
        }
        if (this.numer_of_pages != -1) {
            this.current_page = 0;
            loadImage();
        }
    }
    
    public void loadNextImage() {
        if ( this.current_page > -1 ) {
            var candidate = this.current_page + 1;
            if (candidate < this.numer_of_pages) {
                this.current_page = candidate;
            }
        }
        loadImage();
    }
    
    public void loadPreviousImage() {
        if ( this.current_page > -1 ) {
            var candidate = this.current_page - 1;
            if (candidate > -1) {
                this.current_page = candidate;
            }
        }
        loadImage();
    }
    
    // TODO In einen Service verlagern!
    private void loadImage() {
        final ThrowingSupplier<Image> getter = () -> imageProvider.getPageAsImage(this.current_page);
        var task = new LoadImageTask(getter, image -> {
            setImage(image);
            rect.heightProperty().unbind();
            rect.widthProperty().unbind();
            /* Irgendwie nachimplementieren 
            
        double w = 0;
        double h = 0;
        if (localViewport != null && localViewport.getWidth() > 0 && localViewport.getHeight() > 0) {
            w = localViewport.getWidth();
            h = localViewport.getHeight();
        } else if (localImage != null) {
            w = localImage.getWidth();
            h = localImage.getHeight();
        }

        double localFitWidth = getFitWidth();
        double localFitHeight = getFitHeight();

        if (isPreserveRatio() && w > 0 && h > 0 && (localFitWidth > 0 || localFitHeight > 0)) {
            if (localFitWidth <= 0 || (localFitHeight > 0 && localFitWidth * h > localFitHeight * w)) {
                w = w * localFitHeight / h;
                h = localFitHeight;
            } else {
                h = h * localFitWidth / w;
                w = localFitWidth;
            }
        }
            https://github.com/javafxports/openjdk-jfx/blob/develop/modules/javafx.graphics/src/main/java/javafx/scene/image/ImageView.java
            */
            rect.heightProperty().bind(image.heightProperty());
            rect.widthProperty().bind(image.widthProperty());
        });
        task.run();
    }
}


