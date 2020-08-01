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
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author cwrsi
 */
public class MainLayout extends BorderPane {
    
    public MainLayout() throws IOException {
      
         var p = Paths.get("C:\\Users\\cwrsi\\Downloads\\back_to_simple-000011.png");
        var is = Files.newInputStream(p);
//        var g = new Group();
        var g = new StackPane();
//var g = new StackPane();
        //
        var iv2 = new DisplayedImage(is);
        g.getChildren().add(new Group(iv2));
        
//        iv2.fitHeightProperty().bind(this.heightProperty());
       

        ToolBar tb = new ToolBar();
        Button b = new Button("Bla");
        tb.getItems().add(b);
        setTop(b);
//        setCenter(new Group(iv2));
        setCenter(g);
//        iv2.setFitHeight(USE_COMPUTED_SIZE);
//                iv2.setFitWidth(USE_COMPUTED_SIZE);
 iv2.fitWidthProperty().bind(this.widthProperty());
    var back_reference = this;
        iv2.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> {
        
            var border_height = back_reference.heightProperty().get();
            var tb_height = tb.heightProperty().get();
            return border_height - tb_height;
            
        }, back_reference.heightProperty(), tb.heightProperty()));
    }
    
}
