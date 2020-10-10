// Licensed under GNU General Public License v3.0, see 'LICENSE' for details.
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
