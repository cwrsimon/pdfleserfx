/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx;

import de.wesim.pdfleserfx.backend.ConfigurationService;
import de.wesim.pdfleserfx.backend.pojos.BookConfiguration;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.wesim.pdfleserfx.frontend.mainview.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

/**
 * TODO
 * Integrate Spotless
 * i18n support
 * bookmarks
 * save settings for each book:
 * https://github.com/dieselpoint/norm
 * http://h2database.com
 * save list of last viewed files
 * logger
 * jlink image Windows
 * license file
 * README
 */
public class Main extends Application {

    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private MainView layout;

    /*
      TODO
      Display file name in main window title 
    */
    @Override
    public void start(Stage stage) throws Exception {
                
        stage.setFullScreenExitHint("Press ESC or double-click / double-tap in the center of the screen to exit fullscreen.");

        this.layout = new MainView();

        var scene = new Scene(layout, 640, 480);

        scene.setOnMouseClicked(e -> {

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
                    } else {
                    	; // do nothing for now
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
        });
        stage.setScene(scene);
        stage.show();

        
        // open file in sys.argv[0], if present
        var params = getParameters().getRaw();
        if (params.isEmpty()) return;
        var filename = params.get(0);
        var path = Paths.get(filename);
        if (Files.exists(path)) {
        	layout.openFile(path);
        }
       
    }

}
