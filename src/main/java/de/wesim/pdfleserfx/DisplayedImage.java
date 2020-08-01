/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.Scene;
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
    
    public DisplayedImage(InputStream stream) {

        Image image = new Image(stream);

        setImage(image);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);

        var blend = createBlend();

        setEffect(blend);

    }
    
    private Blend createBlend() {
        var b = new Blend();
        b.setMode(BlendMode.MULTIPLY);
        var rect = new ColorInput();
        //        Color.LIGHTGRAY
        rect.setX(0);
        rect.setY(0);
        rect.setPaint(Color.LIGHTYELLOW);
        rect.widthProperty().bind(fitWidthProperty());
        rect.heightProperty().bind(fitHeightProperty());
        b.setBottomInput(rect);
        return b;
    }

}
