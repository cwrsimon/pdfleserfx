//TODO License Header
package de.wesim.pdfleserfx.frontend.mainview;

import java.io.IOException;
import java.nio.file.Path;

import org.kordamp.ikonli.javafx.FontIcon;

import de.wesim.pdfleserfx.backend.ConfigurationService;
import de.wesim.pdfleserfx.backend.DBService;
import de.wesim.pdfleserfx.backend.pageproviders.PDFPageProvider;
import de.wesim.pdfleserfx.backend.pojos.BookSettings;
import java.time.LocalDateTime;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.PopOver;

public class MainView extends BorderPane {

	private static final int DEFAULT_BUTTON_ICON_SIZE = 16;

	private final ToolBar toolbar;
	private final ColorPicker picker;
	private final DisplayedImage image_container;
	private final ToggleButton fit_window_button;
	private BookSettings current_settings;
	private final ComboBox<Number> dpi_chooser;
	private final ComboBox<Number> page_selector;
	private CropCustomMenuItem content;
	private List<Path> last_5_read_files;
	private StringProperty documentNameProperty = new SimpleStringProperty("No file open");

	public void openFile(Path path) {
		// save settings of book currently open
		if (this.current_settings != null) {
			saveCurrentSettings();
		}
		this.current_settings = DBService.getInstance().findDbEntryForFile(path);
		var content_provider = new PDFPageProvider(path);
		this.image_container.setImageProvider(content_provider);
		loadFirst();
		applySettings();
		documentNameProperty.setValue(path.toString());

	}

	private void applySettings() {
		if (this.current_settings == null) {
			return;
		}
		if (this.current_settings.background_color != null) {
			this.picker.valueProperty().set(Color.valueOf(this.current_settings.background_color));
		}
		if (this.current_settings.dpi != 0) {
			this.dpi_chooser.valueProperty().set(this.current_settings.dpi);
		}
		if (this.current_settings.current_page > 0) {
			this.page_selector.valueProperty().set(this.current_settings.current_page);
		}
		this.content.top_cut.textProperty().set(String.valueOf(this.current_settings.crop_top));
		this.content.left_cut.textProperty().set(String.valueOf(this.current_settings.crop_left));
		this.content.right_cut.textProperty().set(String.valueOf(this.current_settings.crop_right));
		this.content.bottom_cut.textProperty().set(String.valueOf(this.current_settings.crop_bottom));
	}

	public MainView() throws IOException {

		var config_service = ConfigurationService.getInstance();

		this.image_container = new DisplayedImage();

		this.toolbar = new ToolBar();
		this.picker = new ColorPicker(Color.valueOf("0xf3efc1ff"));
		this.picker.setPrefWidth(50);
		this.picker.valueProperty().bindBidirectional(image_container.getColorProperty());

		var open_button = createOpenButton();

		var open_last_5_button = createOpenLast5Button();

		var quit_button = createQuitButton();

		toolbar.getItems().add(open_button);
		toolbar.getItems().add(open_last_5_button);
		toolbar.getItems().add(quit_button);
		toolbar.getItems().add(new Separator());

		var prev_button = createPreviousButton();

		var next_button = createNextButton();

		// TODO Show max page number
		this.page_selector = new ComboBox<>();
		page_selector.setPrefWidth(70);

		page_selector.setItems(FXCollections.observableArrayList(10, 11, 12, 13, 14));
		page_selector.valueProperty().bindBidirectional(this.image_container.pageProperty());
		page_selector.itemsProperty().bind(this.image_container.pagesProperty());

		toolbar.getItems().addAll(prev_button, page_selector, next_button);
		toolbar.getItems().add(new Separator());

		var crop_button = createCropButton();
		toolbar.getItems().add(crop_button);

		toolbar.getItems().addAll(new Separator(), this.picker);
		this.fit_window_button = createFitWindowButton();
		toolbar.getItems().addAll(fit_window_button);

		// TODO Add TextField for Modifying the resolution !
		this.dpi_chooser = new ComboBox<>();
		dpi_chooser.setPrefWidth(70);
		dpi_chooser.setEditable(true);
		dpi_chooser.setConverter(new NumberStringConverter());
		dpi_chooser.setItems(config_service.getDpiResolutions());
		dpi_chooser.valueProperty().bindBidirectional(this.image_container.dpiProperty());
		toolbar.getItems().add(dpi_chooser);

		// TODO Add
		var help_button = createHelpButton();
		toolbar.getItems().add(help_button);
		setTop(this.toolbar);

		var scrollpane = new ScrollPane(new StackPane(this.image_container));
		scrollpane.fitToHeightProperty().bind(Bindings.createBooleanBinding(() -> {
			return image_container.fitHeightProperty().isBound();
		}, image_container.fitHeightProperty()));

		scrollpane.fitToWidthProperty().bind(Bindings.createBooleanBinding(() -> {
			return image_container.fitWidthProperty().isBound();
		}, image_container.fitWidthProperty()));

		setCenter(scrollpane);
		setTop(this.toolbar);
	}

	private void addFitScreenBinding() {
		var back_reference = this;

		image_container.fitWidthProperty().bind(back_reference.widthProperty());
		image_container.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> {

			var border_height = back_reference.heightProperty().get();
			double tb_height = 0.0;
			if (toolbar.visibleProperty().get()) {
				tb_height = toolbar.heightProperty().get();
			}

			return border_height - tb_height;

		}, back_reference.heightProperty(), toolbar.heightProperty(), toolbar.visibleProperty()));
	}

	public void switchToFullscreen(boolean fullscreen) {
		this.toolbar.setVisible(!fullscreen);
		if (fullscreen) {
			setTop(null);
		} else {
			setTop(this.toolbar);
		}
	}

	public void loadFirst() {
		this.image_container.loadFirstImage();
	}

	public void flipRight() {
		this.image_container.loadNextImage();
	}

	public void flipLeft() {
		this.image_container.loadPreviousImage();
	}

	private Button createOpenLast5Button() {
		var file_icon = new FontIcon("gmi-looks-5");

		file_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
		var button = new Button("", file_icon);
		button.setTooltip(new Tooltip("last 5 read files"));
		button.setOnAction(e -> {
			var context_menu = new ContextMenu();
			this.last_5_read_files.forEach(item -> {
				var menu_item = new MenuItem(item.toString());
				menu_item.setOnAction(click -> openFile(item));
				context_menu.getItems().add(menu_item);

			}

			);

			if (!context_menu.isShowing()) {

				context_menu.show(button, Side.BOTTOM, 0, 0);

			} else {

				context_menu.hide();

			}

		});
		return button;

	}

	private Button createOpenButton() {
		var file_icon = new FontIcon("gmi-folder-open");
		file_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
		var open_button = new Button("", file_icon);
		open_button.setOnAction(e -> {
			var file_chooser = new FileChooser();
			file_chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF files", "*.pdf"),
					new FileChooser.ExtensionFilter("All files", "*.*"));
			var chosen_file = file_chooser.showOpenDialog(getScene().getWindow());
			if (chosen_file != null) {
				openFile(chosen_file.toPath());
			}
		});
		return open_button;
	}

	// React on window close / os shutdown event
	private Button createQuitButton() {
		var quit_icon = new FontIcon("gmi-exit-to-app");
		quit_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
		var quit_button = new Button("", quit_icon);
		quit_button.setOnAction(e -> {
			saveCurrentSettings();
			// TODO Find an alternative
			// ConfigurationService.getInstance().getDb().close();
			Platform.exit();
		});
		return quit_button;
	}

	// TODO Save more frequently!
	private void saveCurrentSettings() {
		if (this.current_settings == null) {
			return;
		}

		this.current_settings.background_color = this.picker.getValue().toString();
		this.current_settings.dpi = this.dpi_chooser.getValue().intValue();
		this.current_settings.current_page = this.page_selector.getValue().intValue();
		this.current_settings.crop_top = Integer.valueOf(this.content.top_cut.getText());
		this.current_settings.crop_left = Integer.valueOf(this.content.left_cut.getText());
		this.current_settings.crop_right = Integer.valueOf(this.content.right_cut.getText());
		this.current_settings.crop_bottom = Integer.valueOf(this.content.bottom_cut.getText());
		this.current_settings.last_read = LocalDateTime.now();

		DBService.getInstance().update(this.current_settings);
	}

	// TODO Create keyboard shortcut
	private Button createNextButton() {
		var next_icon = new FontIcon("gmi-arrow-forward");
		next_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
		var next_button = new Button("", next_icon);
		next_button.setOnAction(e -> flipRight());
		return next_button;
	}

	private Button createPreviousButton() {
		var prev_icon = new FontIcon("gmi-arrow-back");
		prev_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
		var prev_button = new Button("", prev_icon);
		prev_button.setOnAction(e -> flipLeft());
		return prev_button;
	}

	private Button createCropButton() {
		// TODO REname
		this.content = new CropCustomMenuItem();
		var context_menu = new ContextMenu(content);

		context_menu.setAutoHide(true);

		var crop_icon = new FontIcon("gmi-crop");
		crop_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
		var crop_button = new Button("", crop_icon);

		crop_button.setOnAction(e -> {

			if (!context_menu.isShowing()) {

				context_menu.show(crop_button, Side.BOTTOM, 0, 0);

			} else {

				context_menu.hide();

			}

		});
		image_container.viewportProperty().bind(Bindings.createObjectBinding(() -> {
			var top_value = content.top_cut.textProperty().get();
			var bottom_value = content.bottom_cut.textProperty().get();
			var left_value = content.left_cut.textProperty().get();
			var right_value = content.right_cut.textProperty().get();

			var top = 0;
			var bottom = 0;
			var left = 0;
			var right = 0;
			if (top_value != null && !top_value.isBlank()) {
				top = Integer.valueOf(top_value);
			}
			if (bottom_value != null && !bottom_value.isBlank()) {
				bottom = Integer.valueOf(bottom_value);
			}
			if (left_value != null && !left_value.isBlank()) {
				left = Integer.valueOf(left_value);
			}
			if (right_value != null && !right_value.isBlank()) {
				right = Integer.valueOf(right_value);
			}
			if (image_container.getImage() == null) {
				return null;
			}
			var width = image_container.getImage().getWidth();
			var height = image_container.getImage().getHeight();
			return new Rectangle2D(left, top, width - left - right, height - top - bottom);
		}, content.top_cut.textProperty(), content.left_cut.textProperty(), content.right_cut.textProperty(),
				content.bottom_cut.textProperty()));

		return crop_button;
	}

	private Button createHelpButton() {
		var help_icon = new FontIcon("gmi-touch-app");
		help_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
		var help_button = new Button("", help_icon);
		help_button.setOnAction(e -> {

			var popover = new PopOver();
			popover.setContentNode(
					new VBox(new Label("Double-tap in the middle of the screen to enter / exit fullscreen."),
							new Label("Tap in the leftmost third of the screen to flip to previous page."),
							new Label("Tap in the rightmost third of the screen to flip to next page.")

					));
			popover.show(help_button);
		});

		// TODO Create popup with instructions for touch use
		return help_button;
	}

	private ToggleButton createFitWindowButton() {
		var fit_window_icon = new FontIcon("gmi-aspect-ratio");
		fit_window_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);

		var button = new ToggleButton("", fit_window_icon);

		button.selectedProperty().addListener((obs, oldV, newV) -> {
			if (newV) {
				addFitScreenBinding();
			} else {
				this.image_container.fitHeightProperty().unbind();
				this.image_container.fitWidthProperty().unbind();
				this.image_container.setFitWidth(0);
				this.image_container.setFitHeight(0);
			}
			image_container.refresh();

		});
		return button;
	}

	public void setLast5Read(List<Path> last_5_read_files) {
		this.last_5_read_files = last_5_read_files;
	}

	public StringProperty documentNameProperty() {
		return this.documentNameProperty;
	}
}
