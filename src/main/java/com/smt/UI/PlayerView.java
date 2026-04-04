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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    private boolean isDrag;

    private int playIndex;

    private SearchView searchView;


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

        progressSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isDrag = true;
            }
        });

        progressSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (MusicPlayer.getInstance().getTotalTime() > 0) {
                    MusicPlayer.getInstance().seekTo(progressSlider.getValue());
                }
                isDrag = false;
            }
        });
        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (MusicPlayer.getInstance().getTotalTime() > 0) {
                double seconds = newVal.doubleValue();
                currentDuration.setText(formatSeconds(seconds));   // 下面会给出
            }
        });
        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playNext();
            }
        });

        beforeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playBefore();
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
    // 把秒数转成 00:00 格式
    private String formatSeconds(double seconds) {
        if (seconds < 0) seconds = 0;
        int min = (int) (seconds / 60);
        int sec = (int) (seconds % 60);
        return String.format("%02d:%02d", min, sec);
    }

    public void setSearchView (SearchView searchView) {
        this.searchView = searchView;
    }


    public void startMusic (int playIndex,MusicItem musicItem, ObservableList<MusicItem> musicItemList, Stage listStage) {
        this.musicItem = musicItem;
        this.musicItemList = musicItemList;
        this.listStage = listStage;
        this.playIndex = playIndex;
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
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText((Stage) playBtn.getScene().getWindow(),
                                        "播放请求失败!", 3000);
                            }
                        });
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
                progressSlider.setValue(0);
                progressSlider.setMax(MusicPlayer.getInstance().getTotalTime());
            }

            @Override
            public void onProgress(String duration) {
                logger.info("当前播放进度:" + duration);
                currentDuration.setText(duration);
                if (!isDrag) {
                    progressSlider.setValue(MusicPlayer.getInstance().getCurrentTime());
                }
            }


            @Override
            public void onComplete() {
                progressSlider.setValue(0);
                logger.info("已播放完毕!");
                switch (Configure.currentPlayMod) {
                    case list:
                        logger.info("列表循环:播放下一首");
                        playNext();
                        break;
                    case single:
                        logger.info("单曲循环:继续播放当前音乐");
                        break;
                    case random:
                        logger.info("随机播放:随机播放下一首");
                        break;
                }
            }
        });
        playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
    }


    public void playNext () {
        if (playIndex < musicItemList.size() - 1) {
            playIndex ++;
        } else {
            playIndex = 0;
        }
        searchView.showPlayCard(musicItemList.get(playIndex));
        startMusic (playIndex,musicItemList.get(playIndex), musicItemList, listStage);
    }

    public void playBefore () {
        if (playIndex > 0) {
            playIndex --;
        } else {
            playIndex = musicItemList.size() - 1;
        }
        searchView.showPlayCard(musicItemList.get(playIndex));
        startMusic (playIndex,musicItemList.get(playIndex), musicItemList, listStage);
    }



}
