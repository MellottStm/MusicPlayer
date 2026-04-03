package com.smt.UI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smt.Configure;
import com.smt.Data.MusicItem;
import com.smt.Utils.NetworkUtil;
import com.smt.Utils.ThreadManager;
import javafx.application.Platform;
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
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.util.Objects;


public class PlayerView {

    private static String TAG = "PlayerView";

    public final static Logger logger = Logger.getLogger(TAG);


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

    private MediaPlayer mediaPlayer;
    @FXML
    public void initialize() {
        logger.info("初始化完成！");
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playMusic();
            }
        });
        startMusic(new MusicItem(
                "1818064296",
                "Bury the Light",
                "Victor Borba",
                "DEVIL MAY CRY 5 SPECIAL EDITION VERGIL SOUND SELECTION",
                "http://p4.music.126.net/1yMEfakS6S6wOU0ypvXg9g==/109951165698662473.jpg"));
    }


    public void startMusic (MusicItem musicItem) {
        this.musicItem = musicItem;
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
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }  else {
            playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
            isPlaying = true;
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        }
    }

    public void musicPlay (JSONArray resJson) {
        mediaPlayer = new MediaPlayer(new Media(resJson.getJSONObject(0).getString("url")));
        // 当媒体准备好后，设置总时长
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getTotalDuration();
            progressSlider.setMax(totalDuration.toSeconds());
            duration.setText(formatTime(totalDuration));
            logger.info("媒体准备完成，总时长: " + totalDuration);
        });

        // 实时更新当前播放时间和进度条
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {

        });

        // 播放结束
        mediaPlayer.setOnEndOfMedia(() -> {
            logger.info("播放结束");
            // 可以在这里自动下一首
        });
        mediaPlayer.play();
        isPlaying = true;
        playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
    }

}
