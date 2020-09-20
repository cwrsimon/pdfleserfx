/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.frontend.mainview;

import de.wesim.pdfleserfx.backend.pageproviders.PDFPageProvider;
import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 *
 * @author cwrsi
 */
public class MainLayout extends BorderPane {

    private static final int DEFAULT_BUTTON_ICON_SIZE = 16;

    private final ToolBar toolbar;
    private final ColorPicker picker;
    private final DisplayedImage iv2;
    private final ToggleButton fit_window_button;

    public void openFile(Path path) {
        System.out.println(path.toAbsolutePath().toString());
        var content_provider = new PDFPageProvider(path);
        this.iv2.setImageProvider(content_provider);
        loadFirst();
    }

    public MainLayout() throws IOException {

        var g = new StackPane();
        //
        this.iv2 = new DisplayedImage();
        var contain = new HBox(iv2);
        contain.setAlignment(Pos.CENTER);
        g.getChildren().add(contain);

        this.toolbar = new ToolBar();
        this.picker = new ColorPicker(Color.web("#f3efc1"));
        this.picker.setPrefWidth(50);
        this.picker.valueProperty().bindBidirectional(iv2.getColorProperty());

        var open_button = createOpenButton();

        var quit_button = createQuitButton();

        toolbar.getItems().add(open_button);
        toolbar.getItems().add(quit_button);
        toolbar.getItems().add(new Separator());

        var prev_button = createPreviousButton();

        var next_button = createNextButton();

        // TODO Show max page number
        var page_selector = new TextField("Page");
        //page_selector.
        page_selector.setPrefWidth(50);
//        page_selector.setEditable(true);
//        page_selector.getValueFactory().valueProperty().bindBidirectional(this.iv2.pageProperty());
        Bindings.bindBidirectional(page_selector.textProperty(),
                this.iv2.pageProperty(), new NumberStringConverter());
        toolbar.getItems().addAll(prev_button, page_selector, next_button);
        toolbar.getItems().add(new Separator());

        var crop_button = createCropButton();
        toolbar.getItems().add(crop_button);
        
        toolbar.getItems().addAll(new Separator(), this.picker);
        this.fit_window_button = createFitWindowButton();
                toolbar.getItems().addAll(fit_window_button);

        // TODO Add TextField for Modifying the resolution !
        var dpi_chooser = new ComboBox();
        dpi_chooser.setItems(FXCollections.observableArrayList(96, 100, 200, 300));
        dpi_chooser.valueProperty().bindBidirectional(this.iv2.dpiProperty());
        toolbar.getItems().add(dpi_chooser);

        // TODO Add 
        var help_button = createHelpButton();
        toolbar.getItems().add(help_button);
        setTop(this.toolbar);
        setCenter(g);

        //addFitScreenBinding();
    }

    // TODO This binding must respect fullscreen mode
    private void addFitScreenBinding() {
        var back_reference = this;

        iv2.fitWidthProperty().bind(back_reference.widthProperty());
        iv2.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> {

            var border_height = back_reference.heightProperty().get();
            var tb_height = toolbar.heightProperty().get();

            return border_height - tb_height;

        }, back_reference.heightProperty(), toolbar.heightProperty()));
    }

    public void switchToFullscreen(boolean fullscreen) {
        if (fullscreen) {
            setTop(null);
        } else {
            setTop(this.toolbar);
        }
    }

    public void loadFirst() {
        this.iv2.loadFirstImage();
    }

    public void flipRight() {
        this.iv2.loadNextImage();
    }

    public void flipLeft() {
        this.iv2.loadPreviousImage();
    }

    private Button createOpenButton() {
        var file_icon = new FontIcon("gmi-folder-open");
        file_icon.setIconSize(DEFAULT_BUTTON_ICON_SIZE);
        var open_button = new Button("", file_icon);
//        open_button.setPrefWidth(50);
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
        quit_button.setOnAction(e -> Platform.exit());
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
        iv2.viewportProperty().bind(Bindings.createObjectBinding(() -> {
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
            if (iv2.getImage() == null) {
                return null;
            }
            var width = iv2.getImage().getWidth();
            var height = iv2.getImage().getHeight();
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
                this.iv2.fitHeightProperty().unbind();
                this.iv2.fitWidthProperty().unbind();
                this.iv2.setFitWidth(0);
                this.iv2.setFitHeight(0);                
            }
            iv2.refresh();

        });
        return button;
    }
}
