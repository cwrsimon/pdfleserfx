/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.frontend.mainview;

import java.io.IOException;
import java.nio.file.Path;

import org.kordamp.ikonli.javafx.FontIcon;

import de.wesim.pdfleserfx.backend.ConfigurationService;
import de.wesim.pdfleserfx.backend.pageproviders.PDFPageProvider;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author cwrsi
 */
public class MainLayout extends BorderPane {

    private static final int DEFAULT_BUTTON_ICON_SIZE = 16;

    private final ToolBar toolbar;
    private final ColorPicker picker;
    private final DisplayedImage image_container;
    private final ToggleButton fit_window_button;

    public void openFile(Path path) {
    	ConfigurationService.getInstance().findDbEntryForFile(path);
        var content_provider = new PDFPageProvider(path);
        this.image_container.setImageProvider(content_provider);
        loadFirst();
    }

    public MainLayout() throws IOException {

    	var config_service = ConfigurationService.getInstance();
    	
        this.image_container = new DisplayedImage();

        this.toolbar = new ToolBar();
        this.picker = new ColorPicker(Color.web("#f3efc1"));
        this.picker.setPrefWidth(50);
        this.picker.valueProperty().bindBidirectional(image_container.getColorProperty());

        var open_button = createOpenButton();

        var quit_button = createQuitButton();

        toolbar.getItems().add(open_button);
        toolbar.getItems().add(quit_button);
        toolbar.getItems().add(new Separator());

        var prev_button = createPreviousButton();

        var next_button = createNextButton();

        // TODO Show max page number
        var page_selector = new ComboBox<Number>();
        page_selector.setPrefWidth(70);

        page_selector.setItems(FXCollections.observableArrayList(10,11,12,13,14));   
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
        var dpi_chooser = new ComboBox<Number>();
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
        scrollpane.fitToHeightProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    return image_container.fitHeightProperty().isBound();
                }, image_container.fitHeightProperty()));
        
        scrollpane.fitToWidthProperty().bind(Bindings.createBooleanBinding(
                () -> {
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

        }, back_reference.heightProperty(), 
            toolbar.heightProperty(), 
            toolbar.visibleProperty()));
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

    private Button createOpenButton() {
        var file_icon = new FontIcon("gmi-folder-open");
        file_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
        var open_button = new Button("", file_icon);
        open_button.setOnAction(e -> {
            var file_chooser = new FileChooser();
            file_chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PDF files", "*.pdf"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
            );
            var chosen_file = file_chooser.showOpenDialog(getScene().getWindow());
            if (chosen_file != null) {
                openFile(chosen_file.toPath());
            }
        });
        return open_button;
    }

    private Button createQuitButton() {
        var quit_icon = new FontIcon("gmi-exit-to-app");
        quit_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
        var quit_button = new Button("", quit_icon);
        quit_button.setOnAction(e -> {
        	ConfigurationService.getInstance().getDb().close();
        	Platform.exit();
        	});
        return quit_button;
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
        var content = new CropCustomMenuItem();
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
            return new Rectangle2D(
                    left,
                    top,
                    width - left - right,
                    height - top - bottom);
        },
                content.top_cut.textProperty(),
                content.left_cut.textProperty(),
                content.right_cut.textProperty(),
                content.bottom_cut.textProperty()
                ));

        return crop_button;
    }
    
    private Button createHelpButton() {
        var help_icon = new FontIcon("gmi-touch-app");
        help_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
        var help_button = new Button("", help_icon);
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
}
