/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.backend.pageproviders;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFPageProvider implements IPageProvider {

    private final Path pdf_file;

    
    public PDFPageProvider(Path pdf_file) {
        this.pdf_file = pdf_file;
    }
    
    @Override
    public Optional<Integer> getNumberOfPages() {
        try (PDDocument document = PDDocument.load(pdf_file.toFile())) {
            return Optional.of(document.getNumberOfPages());
        } catch (IOException ex) {
            
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    
    @Override
    public Image getPageAsImage(int page_number, int dpi) throws IOException {
    try (PDDocument document = PDDocument.load(pdf_file.toFile())) {

            PDFRenderer renderer = new PDFRenderer(document);
            renderer.setSubsamplingAllowed(false);
                    // TODO View-Rendering
                var image = renderer.renderImageWithDPI(page_number, dpi, ImageType.RGB);
                return SwingFXUtils.toFXImage(image, null);
        }
    }
}
