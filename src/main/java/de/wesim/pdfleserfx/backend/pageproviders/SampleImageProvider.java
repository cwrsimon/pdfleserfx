/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.backend.pageproviders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import de.wesim.pdfleserfx.backend.pageproviders.IPageProvider;

/**
 *
 * @author cwrsi
 */
// TODO Generisch für PDF und andere Formate machen
// TODO An dieser Stelle cachen und ein paar Seiten vorausladen..
// TODO Hashmap von Index -> Image anlegen !
public class SampleImageProvider implements IPageProvider {
    
    private int image_index = 0;
    
    
    private static final String[] image_list = new String[]{
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000011.png",
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000012.png",
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000013.png",
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000014.png"

    };
    
    
    
    public SampleImageProvider() {
        
    }
   
    
    @Override
    public Image getFirstImage() throws IOException {
        image_index = 0;
        return readImageFromPath(Paths.get(image_list[image_index]));
    }
    
    @Override
    public Image getNextImage() throws IOException {
        image_index++;
        return readImageFromPath(Paths.get(image_list[image_index]));
    }
    
    @Override
    public Image getPrevImage() throws IOException {
        image_index--;
        return readImageFromPath(Paths.get(image_list[image_index]));
    }
    
    private Image readImageFromPath(Path path) throws IOException {
            try (var is = Files.newInputStream(path)) {
            return new Image(is);
            }
        
    }
}
