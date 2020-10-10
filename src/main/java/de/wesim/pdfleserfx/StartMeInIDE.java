// Licensed under GNU General Public License v3.0, see 'LICENSE' for details.
package de.wesim.pdfleserfx;

import java.io.IOException;

public class StartMeInIDE {

	public static void main(String[] args) throws IOException {
		// call actual JavaFX main app
		// thanks to
		// https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
		Main.main(args);
	}
}
