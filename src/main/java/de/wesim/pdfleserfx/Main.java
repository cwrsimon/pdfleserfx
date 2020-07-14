/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx;

import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author cwrsi
 */
public class Main extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        var p = Paths.get("C:\\Users\\cwrsi\\Downloads\\back_to_simple-000011.png");
        var is = Files.newInputStream(p);
        Image image = new Image(is);

        ImageView iv2 = new ImageView();
        iv2.setImage(image);
        //iv2.setFitWidth(10);
//         iv2.setFitHeight(0);
        iv2.setPreserveRatio(true);
        iv2.setSmooth(true);
        iv2.setCache(true);

        var scene = new Scene(new Group(iv2), 640, 480);
        iv2.fitHeightProperty().bind(scene.heightProperty());
        iv2.fitWidthProperty().bind(scene.widthProperty());

        var b = new Blend();
        b.setMode(BlendMode.MULTIPLY);
        var rect = new ColorInput();
//        Color.LIGHTGRAY
        rect.setX(0);
        rect.setY(0);
        rect.setPaint(Color.LIGHTYELLOW);
        rect.widthProperty().bind(scene.widthProperty());
        rect.heightProperty().bind(scene.heightProperty());
        b.setBottomInput(rect);

        iv2.setEffect(b);

        scene.setOnMouseClicked(e -> {

            System.out.println("CC: " + e.getClickCount());
            System.out.println("CC: " + e.getScreenX() + ";" + e.getScreenY());
            System.out.println("CC: " + e.getSceneX() + ";" + e.getSceneY());

            if (e.getClickCount() == 2) {
                var old_val = stage.isFullScreen();
                stage.setFullScreen(!old_val);
            }
        });

        scene.setOnTouchPressed(e -> {
            System.out.println(
                    e.getTouchCount()
            );
        });
        stage.setScene(scene);

        stage.show();
    }

}
