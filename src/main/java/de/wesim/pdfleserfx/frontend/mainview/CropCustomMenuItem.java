/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.frontend.mainview;

import javafx.geometry.Pos;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author papa
 */
public class CropCustomMenuItem extends CustomMenuItem {

    final TextField top_cut;
    final TextField bottom_cut;
    final TextField left_cut;
    final TextField right_cut;
    
    public CropCustomMenuItem() {
        super();
        this.top_cut = new TextField();
        top_cut.setPrefWidth(50);
        top_cut.setPromptText("Top");
        this.bottom_cut = new TextField();
        bottom_cut.setPrefWidth(50);
        bottom_cut.setPromptText("Bottom");
        this.left_cut = new TextField();
        left_cut.setPrefWidth(50);
        left_cut.setPromptText("Left");
        this.right_cut = new TextField();
        right_cut.setPrefWidth(50);
        right_cut.setPromptText("Right");

        var vertical_container = new VBox(top_cut, new HBox(left_cut, right_cut), bottom_cut);
        vertical_container.setAlignment(Pos.TOP_CENTER);
        vertical_container.setFillWidth(false);
        setContent(vertical_container);
        getStyleClass().clear();
        setHideOnClick(false);
    }
    
}
