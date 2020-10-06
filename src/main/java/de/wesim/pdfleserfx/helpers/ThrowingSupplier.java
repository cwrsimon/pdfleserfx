//TODO License Header
package de.wesim.pdfleserfx.helpers;

import java.io.IOException;

/**
 *
 * @author cwrsi
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
	T get() throws IOException;
}
