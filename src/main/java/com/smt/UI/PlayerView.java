package com.smt.UI;

import com.alibaba.fastjson.JSONArray;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


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


    private MusicItem musicItem;

    private ObservableList<MusicItem> musicItemList;

    private Stage listStage;

    public Timer timer;


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
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MusicPlayer.getInstance().isPlaying()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            playBtn.setStyle("-fx-background-image: url(\"Img/radio_play.png\");");
                        }
                    });
                }
            }
        },0,1000);
    }



    public void startMusic (MusicItem musicItem, ObservableList<MusicItem> musicItemList, Stage listStage) {
        this.musicItem = musicItem;
        this.musicItemList = musicItemList;
        this.listStage = listStage;
        MusicPlayer.getInstance().loadCoverImage(this.musicItem.coverUrl,coverImage,new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/music_bg.jpg"))));
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
        if (MusicPlayer.getInstance().isPlaying()) {
            playBtn.setStyle("-fx-background-image: url(\"Img/radio_play.png\");");
            MusicPlayer.getInstance().pause();
        }  else {
            playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
            MusicPlayer.getInstance().resume();
        }
    }

    public void musicPlay (JSONArray resJson) {
        MusicPlayer.getInstance().play(this.musicItem, resJson, new MusicPlayer.PlayerCallBack() {
            @Override
            public void onReady() {
                duration.setText(MusicPlayer.getInstance().getTotalDuration());
            }

            @Override
            public void onProgress(Duration oldTime, Duration newTime) {

            }

            @Override
            public void onComplete() {

            }
        });
        playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
    }

}
