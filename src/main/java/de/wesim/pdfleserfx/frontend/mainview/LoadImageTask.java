//TODO License Header
package de.wesim.pdfleserfx.frontend.mainview;

import de.wesim.pdfleserfx.helpers.ThrowingSupplier;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 *
 * @author cwrsi
 */
public class LoadImageTask extends Task<Image> {

	private final ThrowingSupplier<Image> getter;
	private final Consumer<Image> callback;

	public LoadImageTask(ThrowingSupplier<Image> getter, Consumer<Image> callback) {
		this.getter = getter;
		this.callback = callback;
	}

	// TODO Add error handler
	@Override
	protected Image call() throws Exception {
		return getter.get();
	}

	@Override
	protected void succeeded() {
		var value = getValue();
		Platform.runLater(() -> {
			callback.accept(value);
		});
	}

	@Override
	protected void failed() {
		super.failed(); // To change body of generated methods, choose Tools | Templates.
		getException().printStackTrace();
	}

}
