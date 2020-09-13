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
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author cwrsi
 */
public class MainLayout extends BorderPane {

    private final ToolBar toolbar;
    private final ColorPicker picker;
    private final DisplayedImage iv2;

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
        var open_button = new Button("Open");
        open_button.setPrefWidth(50);
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

        var quit_button = new Button("Quit");
        quit_button.setPrefWidth(50);
        quit_button.setOnAction(e -> Platform.exit());

        toolbar.getItems().add(open_button);
        toolbar.getItems().add(quit_button);
        toolbar.getItems().add(new Separator());

        var prev_button = new Button("Previous");
        prev_button.setPrefWidth(50);
        prev_button.setOnAction(e -> flipLeft());

        var next_button = new Button("Next");
        next_button.setPrefWidth(50);
        next_button.setOnAction(e -> flipRight());

        // TODO Add arrows for prev/next page
        var page_selector = new TextField("Page");
        //page_selector.
        page_selector.setPrefWidth(50);
//        page_selector.setEditable(true);
//        page_selector.getValueFactory().valueProperty().bindBidirectional(this.iv2.pageProperty());
        Bindings.bindBidirectional(page_selector.textProperty(),
                this.iv2.pageProperty(), new NumberStringConverter());
        toolbar.getItems().addAll(prev_button, page_selector, next_button);
        toolbar.getItems().add(new Separator());

        // TODO Turn TextField into ComboBox
        var top_cut = new TextField();
        top_cut.setPrefWidth(50);
        top_cut.setPromptText("Top");
        var bottom_cut = new TextField();
        bottom_cut.setPrefWidth(50);
        bottom_cut.setPromptText("Bottom");
        var left_cut = new TextField();
        left_cut.setPrefWidth(50);
        left_cut.setPromptText("Left");
        var right_cut = new TextField();
        right_cut.setPrefWidth(50);
        right_cut.setPromptText("Right");

        iv2.viewportProperty().bind(Bindings.createObjectBinding(() -> {
            var top_value = top_cut.textProperty().get();
            var bottom_value = bottom_cut.textProperty().get();
            var left_value = left_cut.textProperty().get();
            var right_value = right_cut.textProperty().get();

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
                top_cut.textProperty(),
                left_cut.textProperty(),
                right_cut.textProperty(),
                bottom_cut.textProperty()));

        toolbar.getItems().addAll(left_cut, top_cut, right_cut, bottom_cut);
        toolbar.getItems().addAll(new Separator(), this.picker);
        var fit_window_button = new Button("Original Size");
        toolbar.getItems().addAll(fit_window_button);
        fit_window_button.setOnAction(e -> {
            if (this.iv2.fitHeightProperty().isBound()) {
                this.iv2.fitHeightProperty().unbind();
                this.iv2.fitWidthProperty().unbind();
                fit_window_button.setText("Fit to window");
                iv2.refresh();

            } else {
                fit_window_button.setText("Original size");
                addFitScreenBinding();
                iv2.refresh();
            }
        });
        // TODO Add TextField for Modifying the resolution !
        var dpi_chooser = new ComboBox();
        dpi_chooser.setItems(FXCollections.observableArrayList(96, 100, 200, 300));
        dpi_chooser.valueProperty().bindBidirectional(this.iv2.dpiProperty());
        toolbar.getItems().add(dpi_chooser);

        // TODO Add 
        var help_button = new Button("Help");
        toolbar.getItems().add(help_button);
        setTop(this.toolbar);
        setCenter(g);

        addFitScreenBinding();
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

}
