/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.backend.pageproviders;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 *
 * @author cwrsi
 */
public interface IPageProvider {

    Image getFirstImage() throws IOException;

    Image getNextImage() throws IOException;

    Image getPrevImage() throws IOException;
    
}
