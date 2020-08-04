/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author cwrsi
 */
// TODO Generisch für PDF und andere Formate machen
// TODO Statt Path lieber Images zurückgeben
// TODO An dieser Stelle cachen und ein paar Seiten vorausladen..
// TODO Hashmap von Index -> Image anlegen !
public class ImageProvider {
    
    private int image_index = 0;
    
    
    private static final String[] image_list = new String[]{
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000011.png",
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000012.png",
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000013.png",
        "C:\\Users\\cwrsi\\Downloads\\back_to_simple-000014.png"

    };
    
    
    private static ImageProvider _instance;
    
    private ImageProvider() {
        
    }
    
    public static ImageProvider getInstance() {
        if (_instance == null) {
            _instance = new ImageProvider();
        }
        return _instance;
    }
    
    
    public Path getFirstImage() {
        image_index = 0;
        return Paths.get(image_list[image_index]);
    }
    
    public Path getNextImage() {
        image_index++;
        return Paths.get(image_list[image_index]);
    }
    
    public Path getPrevImage() {
        image_index--;
        return Paths.get(image_list[image_index]);
    }
}
