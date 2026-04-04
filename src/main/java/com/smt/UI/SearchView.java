package com.smt.UI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smt.Configure;
import com.smt.Data.MusicItem;
import com.smt.Main;
import com.smt.Utils.MusicPlayer;
import com.smt.Utils.NetworkUtil;
import com.smt.Utils.ThreadManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SearchView {

    private static String TAG = "SearchView";

    public final static Logger logger = Logger.getLogger(TAG);

    @FXML
    private TextField searchField;   // 对应 fxml 里的 fx:id（建议加上）

    @FXML
    private Button searchButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    public ListView<MusicItem> searchList;

    @FXML
    public AnchorPane playCard;

    @FXML
    public Button playBtn;

    @FXML
    public Button beforeBtn;

    @FXML
    public Button nextBtn;

    @FXML
    public Button collectedListBtn;

    @FXML
    public Button isCollectedBtn;

    @FXML
    public ImageView cover;

    @FXML
    public Label msg;

    @FXML
    private ImageView playViewCoverImage;

    @FXML
    private Label playViewSong;

    @FXML
    private Label playViewSinger;

    @FXML
    private Label playViewDuration;

    @FXML
    private Label playViewCurrentDuration;

    @FXML
    private Button playViewBeforeBtn;

    @FXML
    private Button playViewPlayBtn;

    @FXML
    private Button playViewNextBtn;

    @FXML
    private Button playViewPlayModBtn;

    @FXML
    private Button playViewListBtn;

    @FXML
    private Slider playViewProgressSlider;

    @FXML
    public Button playViewIsCollectedBtn;

    @FXML
    public AnchorPane playView;

    public Timer timer;

    public ObservableList<MusicItem> currentItems;

    private boolean isDrag;

    private MusicItem musicItem;

    private ObservableList<MusicItem> musicItemList;

    private int playIndex;

    @FXML
    public void initialize() {
        logger.info("初始化完成!");
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
        playCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {   // 双击打开（推荐）
                    openPlayView();
                }
            }
        });
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playMusic();
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



        playViewPlayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playMusic();
            }
        });
        playViewListBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                closePlayView();
            }
        });

        playViewProgressSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isDrag = true;
            }
        });

        playViewProgressSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (MusicPlayer.getInstance().getTotalTime() > 0) {
                    MusicPlayer.getInstance().seekTo(playViewProgressSlider.getValue());
                }
                isDrag = false;
            }
        });
        playViewProgressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (MusicPlayer.getInstance().getTotalTime() > 0) {
                double seconds = newVal.doubleValue();
                playViewCurrentDuration.setText(formatSeconds(seconds));   // 下面会给出
            }
        });
        playViewNextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playNext();
            }
        });

        playViewBeforeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playBefore();
            }
        });


    }


    private String formatSeconds(double seconds) {
        if (seconds < 0) seconds = 0;
        int min = (int) (seconds / 60);
        int sec = (int) (seconds % 60);
        return String.format("%02d:%02d", min, sec);
    }


    private void openPlayView () {
        playView.setVisible(true);
    }

    private void closePlayView () {
        playView.setVisible(false);
    }



    public void playNext () {
        if (playIndex < musicItemList.size() - 1) {
            playIndex ++;
        } else {
            playIndex = 0;
        }
        showPlayCard(musicItemList.get(playIndex));
        startMusic (playIndex,musicItemList.get(playIndex), musicItemList);
    }

    public void playBefore () {
        if (playIndex > 0) {
            playIndex --;
        } else {
            playIndex = musicItemList.size() - 1;
        }
        showPlayCard(musicItemList.get(playIndex));
        startMusic (playIndex,musicItemList.get(playIndex), musicItemList);
    }




    public void playMusic () {
        if (MusicPlayer.getInstance().getCurrentMusicItem() != null) {
            if (MusicPlayer.getInstance().isPlaying()) {
                playBtn.setStyle("-fx-background-image: url(\"Img/radio_play.png\");");
                playViewPlayBtn.setStyle("-fx-background-image: url(\"Img/radio_play.png\");");
                MusicPlayer.getInstance().pause();
            } else {
                playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
                playViewPlayBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
                MusicPlayer.getInstance().resume();
            }
        }
    }





    public void showPlayCard (MusicItem musicItem) {
        if (musicItem != null) {
            MusicPlayer.getInstance().loadCoverImage(musicItem.coverUrl, cover, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/music_bg.jpg"))));
            msg.setText(musicItem.song + "-" + musicItem.singer);
        }
    }




    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        Configure.IMAGE_CACHE.clear();
        if (!keyword.isEmpty()) {
            logger.info("搜索的内容:" + keyword);
            loadingIndicator.setVisible(true);
            searchList.setVisible(false);
            searchButton.setDisable(true);
            // 后面可以在这里写搜索逻辑
            String msg = "?word=" + keyword + "&page=1&num=20";
            ThreadManager.setThreadToPool(new Runnable() {
                @Override
                public void run() {
                    NetworkUtil.okHttpGet(msg, Configure.getSearchMusicListUrl, new NetworkUtil.HttpCallBack() {
                        @Override
                        public void callBackFail(String error) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    searchButton.setDisable(false);
                                    loadingIndicator.setVisible(false);
                                    searchList.setVisible(false);
                                    Toast.makeText((Stage) playBtn.getScene().getWindow(),
                                            "搜索请求失败!", 3000);
                                }
                            });
                        }

                        @Override
                        public void callBackSuccess(String response) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject resJson = JSONObject.parseObject(response);
                                    showSearchResult(resJson);
                                }
                            });
                        }
                    });
                }
            });
        } else {
           logger.info("搜索内容为空!");
        }
    }


    public void showSearchResult (JSONObject resJson) {
        searchButton.setDisable(false);
        loadingIndicator.setVisible(false);
        searchList.setVisible(true);
        searchList.setCellFactory(listview -> new MusicCell());
        resJson.getJSONArray("data");
        ObservableList<MusicItem> items = FXCollections.observableArrayList();
        for (int i = 0;i < resJson.getJSONArray("data").size();i++) {
            JSONObject musicJson = resJson.getJSONArray("data").getJSONObject(i);
            items.add(new MusicItem(
                    musicJson.getString("id"),
                    musicJson.getString("song"),
                    musicJson.getString("singer"),
                    musicJson.getString("album"),
                    musicJson.getString("cover")));
        }
        searchList.setItems(items);
        searchList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {   // 双击打开（推荐）
                    currentItems = items;
                    MusicItem selectedItem = searchList.getSelectionModel().getSelectedItem();
                    openPlayerWindow(searchList.getSelectionModel().getSelectedIndex(), selectedItem,items);
                }
            }
        });
    }


    private void openPlayerWindow(int playIndex,MusicItem musicItem,ObservableList<MusicItem> items) {
        showPlayCard(musicItem);
        playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
        openPlayView();
        startMusic(playIndex,musicItem,items);     // ← 关键：传递数据
    }


    public void startMusic (int playIndex,MusicItem musicItem, ObservableList<MusicItem> musicItemList) {
        this.musicItem = musicItem;
        this.musicItemList = musicItemList;
        this.playIndex = playIndex;
        MusicPlayer.getInstance().loadCoverImage(this.musicItem.coverUrl,playViewCoverImage,new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/music_bg.jpg"))));
        playViewSinger.setText("艺术家:" + musicItem.singer);
        playViewSong.setText("歌:" + musicItem.song);
        playViewDuration.setText("00:00");
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


    public void musicPlay (JSONArray resJson) {
        MusicPlayer.getInstance().play(this.musicItem, resJson, new MusicPlayer.PlayerCallBack() {
            @Override
            public void onReady() {
                playViewDuration.setText(MusicPlayer.getInstance().getTotalDuration());
                playViewProgressSlider.setValue(0);
                playViewProgressSlider.setMax(MusicPlayer.getInstance().getTotalTime());
            }

            @Override
            public void onProgress(String duration) {
                logger.info("当前播放进度:" + duration);
                playViewCurrentDuration.setText(duration);
                if (!isDrag) {
                    playViewProgressSlider.setValue(MusicPlayer.getInstance().getCurrentTime());
                }
            }


            @Override
            public void onComplete() {
                playViewProgressSlider.setValue(0);
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
        playViewPlayBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
    }




}
