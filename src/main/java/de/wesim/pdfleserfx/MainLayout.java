/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.beans.binding.Bindings;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author cwrsi
 */
public class MainLayout extends BorderPane {

    private final ToolBar toolbar;
    private final ColorPicker picker;

    public MainLayout() throws IOException {

        var p = Paths.get("C:\\Users\\cwrsi\\Downloads\\back_to_simple-000011.png");
        var is = Files.newInputStream(p);

        var g = new StackPane();
        //
        var iv2 = new DisplayedImage(is);
        var contain = new HBox(iv2);
        contain.setAlignment(Pos.CENTER);
        g.getChildren().add(contain);

        this.toolbar = new ToolBar();
        this.picker = new ColorPicker(Color.web("#f3efc1"));
        this.picker.valueProperty().bindBidirectional(iv2.getColorProperty());
        var b = new Button("Bla");
        toolbar.getItems().add(b);
        toolbar.getItems().add(this.picker);
        
        setTop(this.toolbar);
        setCenter(g);
        
        var back_reference = this;

        iv2.fitWidthProperty().bind(back_reference.widthProperty());
        iv2.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> {

            var border_height = back_reference.heightProperty().get();
            var tb_height = toolbar.heightProperty().get();

            return border_height - tb_height;

        }, back_reference.heightProperty(), toolbar.heightProperty()));
        
//                System.out.println("Image With:" + iv2.getViewport().getWidth());

    }

    public void switchToFullscreen(boolean fullscreen) {
        if (fullscreen) {
            setTop(null);
        } else {
            setTop(this.toolbar);
        }
    }
}
