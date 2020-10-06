//TODO License Header
package de.wesim.pdfleserfx.backend.pageproviders;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.image.Image;

/**
 *
 * @author cwrsi
 */
public interface IPageProvider {

	public Optional<Integer> getNumberOfPages();

	public Image getPageAsImage(int page_number, int dpi) throws IOException;

}
