package com.smt.UI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smt.Configure;
import com.smt.Data.MusicItem;
import com.smt.Utils.MusicPlayer;
import com.smt.Utils.NetworkUtil;
import com.smt.Utils.ThreadManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PlayerView {

    private static String TAG = "PlayerView";

    private final static Logger logger = Logger.getLogger(TAG);


    @FXML
    private ImageView coverImage;

    @FXML
    private Label song;

    @FXML
    private Label singer;

    @FXML
    private Label duration;

    @FXML
    private Label currentDuration;

    @FXML
    private Button beforeBtn;

    @FXML
    private Button playBtn;

    @FXML
    private Button nextBtn;

    @FXML
    private Button playModBtn;

    @FXML
    private Button listBtn;

    @FXML
    private Slider progressSlider;

    private boolean isPlaying;

    private MusicItem musicItem = new MusicItem();

    private ObservableList<MusicItem> musicItemList;

    private Stage listStage;

    @FXML
    public void initialize() {
        logger.info("初始化完成！");
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playMusic();
            }
        });
        listBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ((Stage)listBtn.getScene().getWindow()).hide();
                listStage.show();
            }
        });
    }


    public void startMusic (MusicItem musicItem, ObservableList<MusicItem> musicItemList, Stage listStage) {
        this.musicItem = musicItem;
        this.musicItemList = musicItemList;
        this.listStage = listStage;
        if (musicItem.coverUrl != null && !musicItem.coverUrl.isEmpty()) {
            // 使用 background loading，避免卡顿
            Image image = new Image(musicItem.coverUrl, true); // true = background loading
            coverImage.setImage(image);
        } else {
            // 设置默认图片
            coverImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/icon.jpg"))));
        }
        singer.setText("艺术家:" + musicItem.singer);
        song.setText("歌:" + musicItem.song);
        ThreadManager.setThreadToPool(new Runnable() {
            @Override
            public void run() {
                String msg = "?type=song&id=" + musicItem.id;
                NetworkUtil.okHttpGet(msg, Configure.getMusicPlayUrl, new NetworkUtil.HttpCallBack() {
                    @Override
                    public void callBackFail(String error) {

                    }

                    @Override
                    public void callBackSuccess(String response) {
                        JSONArray resJson = JSONArray.parseArray(response);
                        musicPlay(resJson);
                    }
                });
            }
        });
    }
    private String formatTime(Duration duration) {
        if (duration == null || duration.isUnknown() || duration.isIndefinite()) {
            return "00:00";
        }
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void playMusic () {
        if (isPlaying) {
            playBtn.setStyle("-fx-background-image: url(\"Img/radio_play.png\");");
            isPlaying = false;
            MusicPlayer.getInstance().pause();
        }  else {
            playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
            isPlaying = true;
            MusicPlayer.getInstance().resume();
        }
    }

    public void musicPlay (JSONArray resJson) {
        MusicPlayer.getInstance().play(resJson);
        isPlaying = true;
        playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
        duration.setText(MusicPlayer.getInstance().getTotalDuration());
    }

}
