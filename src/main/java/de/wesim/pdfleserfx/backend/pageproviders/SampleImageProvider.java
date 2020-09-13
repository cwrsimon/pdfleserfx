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
import java.util.Optional;

/**
 *
 * @author cwrsi
 */
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
   
    
    
    private Image readImageFromPath(Path path) throws IOException {
            try (var is = Files.newInputStream(path)) {
            return new Image(is);
            }
        
    }

    @Override
    public Optional<Integer> getNumberOfPages() {
        return Optional.of(image_list.length);
    }

    @Override
    public Image getPageAsImage(int page_number, int dpi) throws IOException {
        return readImageFromPath(Paths.get(image_list[page_number]));
    }
}
