package com.smt.UI;

import com.smt.Data.MusicItem;
import com.smt.Utils.MusicPlayer;
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
        singerLabel.setText("歌手:" + item.singer);
        albumLabel.setText("专辑:" + item.album);
        MusicPlayer.getInstance().loadCoverImage(item.coverUrl,coverImage,new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/music_bg.jpg"))));
    }

    public VBox getRoot() {
        return root;
    }


}
