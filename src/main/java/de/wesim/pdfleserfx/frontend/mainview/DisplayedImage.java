// Licensed under GNU General Public License v3.0, see 'LICENSE' for details.
package de.wesim.pdfleserfx.frontend.mainview;

import de.wesim.pdfleserfx.backend.pageproviders.IPageProvider;
import de.wesim.pdfleserfx.helpers.ThrowingSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 *
 * @author cwrsi
 */
public class DisplayedImage extends ImageView {

	private ColorInput rect;

	private final SimpleObjectProperty<ObservableList<Number>> pagesProperty = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.web("#f3efc1"));
	private IPageProvider imageProvider;
	private Integer numer_of_pages = -1;

	private final SimpleIntegerProperty dpiProperty = new SimpleIntegerProperty(96);
	private final SimpleIntegerProperty pageProperty = new SimpleIntegerProperty(0);

	public DisplayedImage() {

		setPreserveRatio(true);
		setSmooth(true);
		setCache(true);

		var blend = createBlend();
		setEffect(blend);

		colorProperty.addListener((o, old, newV) -> {
			this.rect.setPaint(newV);
		});

		dpiProperty.addListener((obs, oldV, newV) -> {
			refresh();
		});

		pageProperty.addListener((obs, oldV, newV) -> {
			if (newV.intValue() < 1)
				return;
			System.out.println(oldV + ";" + newV);
			loadImage();
		});

	}

	public void refresh() {
		loadImage();
	}

	private Blend createBlend() {
		var b = new Blend();
		b.setMode(BlendMode.MULTIPLY);
		this.rect = new ColorInput();
		rect.setX(0);
		rect.setY(0);
		rect.setPaint(colorProperty.getValue());
		b.setBottomInput(rect);
		return b;
	}

	public SimpleObjectProperty<Color> getColorProperty() {
		return colorProperty;
	}

	public void loadFirstImage() {
		pageProperty.set(0);
		var number_of_pages_opt = imageProvider.getNumberOfPages();
		if (number_of_pages_opt.isPresent()) {
			this.numer_of_pages = number_of_pages_opt.get();
			var range = IntStream.rangeClosed(1, this.numer_of_pages).boxed().collect(Collectors.toList());
			this.pagesProperty.setValue(FXCollections.observableArrayList(range));
		}
		System.out.println("page count:" + this.numer_of_pages);
		if (this.numer_of_pages != -1) {
			System.out.println("Hey");
			pageProperty.set(1);
		}
	}

	public void loadNextImage() {
		var current_page = pageProperty.get();
		if (current_page > -1) {
			var candidate = current_page + 1;
			if (candidate <= this.numer_of_pages) {
				pageProperty.set(candidate);
			}
		}
	}

	public void loadPreviousImage() {
		var current_page = pageProperty.get();
		if (current_page > -1) {
			var candidate = current_page - 1;
			if (candidate > 0) {
				pageProperty.set(candidate);
			}
		}
	}

	private void loadImage() {
		var page = pageProperty.get();
		System.out.println("Loading page:" + page);

		final ThrowingSupplier<Image> getter = () -> imageProvider.getPageAsImage((page - 1), dpiProperty.get());
		var task = new LoadImageTask(getter, image -> {
			setImage(image);
			rect.heightProperty().unbind();
			rect.widthProperty().unbind();
			rect.heightProperty().bind(Bindings.createDoubleBinding(() -> {
				var new_dimensions = calculateDimensions(image, viewportProperty().getValue());
				return new_dimensions[1];
			}, viewportProperty(), fitWidthProperty(), fitHeightProperty()));

			rect.widthProperty().bind(Bindings.createDoubleBinding(() -> {
				var new_dimensions = calculateDimensions(image, viewportProperty().getValue());
				return new_dimensions[0];
			}, viewportProperty(), fitWidthProperty(), fitHeightProperty()));
		});
		task.run();
	}

	private double[] calculateDimensions(Image image, Rectangle2D viewport_rect) {
		var width_to_use = image.getWidth();
		var height_to_use = image.getHeight();
		if (viewport_rect != null) {
			width_to_use = viewport_rect.getWidth();
			height_to_use = viewport_rect.getHeight();
		}
		return calcHeightWidth(width_to_use, height_to_use, fitWidthProperty().get(), fitHeightProperty().get());
	}

	/*
	 * Borrowed from
	 * https://github.com/javafxports/openjdk-jfx/blob/develop/modules/javafx.
	 * graphics/src/main/java/javafx/scene/image/ImageView.java
	 */
	private double[] calcHeightWidth(double w, double h, double localFitWidth, double localFitHeight) {
		if (isPreserveRatio() && w > 0 && h > 0 && (localFitWidth > 0 || localFitHeight > 0)) {
			if (localFitWidth <= 0 || (localFitHeight > 0 && localFitWidth * h > localFitHeight * w)) {
				w = w * localFitHeight / h;
				h = localFitHeight;
			} else {
				h = h * localFitWidth / w;
				w = localFitWidth;
			}
		}
		return new double[]{w, h};
	}

	public void setImageProvider(IPageProvider imageProvider) {
		this.imageProvider = imageProvider;
	}

	Property<Number> pageProperty() {
		return this.pageProperty;
	}

	Property<Number> dpiProperty() {
		return this.dpiProperty;
	}

	public Integer getNumer_of_pages() {
		return numer_of_pages;
	}

	public SimpleObjectProperty<ObservableList<Number>> pagesProperty() {
		return pagesProperty;
	}

}
