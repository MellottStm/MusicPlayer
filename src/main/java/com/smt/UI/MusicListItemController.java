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

        // 异步加载网络图片（推荐方式）
        if (item.coverUrl != null && !item.coverUrl.isEmpty()) {
            // 使用 background loading，避免卡顿
            Image image = new Image(item.coverUrl, true); // true = background loading
            coverImage.setImage(image);
        } else {
            // 设置默认图片
            coverImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/icon.jpg"))));
        }
    }

    public VBox getRoot() {
        return root;
    }

}
