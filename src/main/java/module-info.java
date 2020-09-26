module de.wesim.pdfleserfx {
	exports de.wesim.pdfleserfx.helpers;
	exports de.wesim.pdfleserfx.backend.pageproviders;
	exports de.wesim.pdfleserfx;
	exports de.wesim.pdfleserfx.backend;
	exports de.wesim.pdfleserfx.frontend.mainview;

	requires java.desktop;
	requires javafx.base;
	requires javafx.controls;
	requires transitive javafx.graphics;
	requires javafx.swing;
	requires org.apache.pdfbox;
	requires org.kordamp.iconli.core;
	requires org.kordamp.ikonli.material;
	requires org.kordamp.ikonli.javafx;
}