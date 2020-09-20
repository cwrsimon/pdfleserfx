/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx;

import de.wesim.pdfleserfx.frontend.mainview.MainLayout;
import java.awt.Toolkit;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
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
        launch(args);
    }
    
    private MainLayout layout;

    /*
      TODO
      Display file name in main window title 
    */
    @Override
    public void start(Stage stage) throws Exception {
        
        var dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        System.out.print("Resolution:");
        System.out.println(dpi);
        
        this.layout = new MainLayout();

        var scene = new Scene(layout, 640, 480);

        scene.setOnMouseClicked(e -> {

            /*
            System.out.println("CC: " + e.getClickCount());
            System.out.println("Screen X: " + e.getScreenX() + ";" + e.getScreenY());
            System.out.println("Scene X: " + e.getSceneX() + ";" + e.getSceneY());
            System.out.println("X: " + e.getX() + ";" + e.getY());
            */
            if (e.getButton() == MouseButton.PRIMARY) {
                if (e.getClickCount() == 2) {
                    var old_val = stage.isFullScreen();
                    stage.setFullScreen(!old_val);
                    layout.switchToFullscreen(!old_val);
                    return;
                } else if (e.getClickCount() == 1) {

                    var screenx = e.getSceneX();
                    var current_width = scene.getWidth();
                    var left_side = current_width / 3.0;
                    var right_side = current_width / 3.0 * 2.0;
                    if (screenx <= left_side) {
                        layout.flipLeft();

                    } else if (screenx >= right_side) {
                        layout.flipRight();
                        System.out.println("Rechts");
                    } else {
                        System.out.println("Mitte");
                    }

                }
            }

            if (e.getButton() == MouseButton.SECONDARY) {
                ContextMenu cm = new ContextMenu(new MenuItem("Quit"));
                cm.show(scene.getWindow(), e.getScreenX(), e.getScreenY());
            }
        });

        scene.setOnTouchPressed(e -> {
            System.out.println(
                    e.getTouchCount()
            );
//            if (e.getTouchCount() == 2) {
//                ContextMenu cm = new ContextMenu(new MenuItem("Quit"));
//                cm.show(scene.getWindow());
//                
//            }
        });
        stage.setScene(scene);
        stage.show();

        // TODO Issue a warning if file does not exist        
         // we should have at least on parameter
        var filename = getParameters().getRaw().get(0);
        var path = Paths.get(filename);
       
        layout.openFile(path);
    }

}
