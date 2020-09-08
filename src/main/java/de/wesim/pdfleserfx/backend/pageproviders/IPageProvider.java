/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.backend.pageproviders;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author cwrsi
 */
public interface IPageProvider {

    public Optional<Integer> getNumberOfPages();

    
    public Image getPageAsImage(int page_number) throws IOException;
    
}
