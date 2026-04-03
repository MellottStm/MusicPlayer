package com.smt.UI;

import com.smt.Data.MusicItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


import java.util.Objects;

public class MusicListItemController {

    @FXML
    private VBox root;
    @FXML private ImageView coverImage;
    @FXML private Label songLabel;
    @FXML private Label singerLabel;
    @FXML private Label albumLabel;

    public void setData(MusicItem item) {
        songLabel.setText(item.song);
        singerLabel.setText(item.singer);
        albumLabel.setText(item.album != null ? item.album : "");
        loadCoverImage(item.coverUrl,coverImage,new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/music_bg.jpg"))));
    }

    public VBox getRoot() {
        return root;
    }

    private void loadCoverImage(String coverUrl, ImageView coverImage,Image defaultImage) {
        if (coverUrl == null || coverUrl.trim().isEmpty()) {
            coverImage.setImage(defaultImage);
            return;
        }

        Image image = new Image(coverUrl, true);

        // 错误时立即切换默认图
        image.errorProperty().addListener((obs, oldVal, newVal) -> {
            if (Boolean.TRUE.equals(newVal)) {
                coverImage.setImage(defaultImage);
            }
        });

        coverImage.setImage(image);
    }


}
