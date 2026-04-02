package com.smt.UI;

import com.smt.Data.MusicItem;
import com.smt.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;



public class MusicCell extends ListCell<MusicItem> {

    private HBox root;
    private MusicListItemController controller;

    public MusicCell() {
        try {
            FXMLLoader  loader = new FXMLLoader(getClass().getResource("/View/MusicItemView.fxml"));
            root = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(MusicItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            controller.setData(item);
            setGraphic(root);
        }
    }
}
