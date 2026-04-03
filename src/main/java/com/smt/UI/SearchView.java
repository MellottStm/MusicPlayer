package com.smt.UI;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
    public ImageView cover;

    @FXML
    public Label msg;


    public Timer timer;

    public boolean isSwitchMusic = false;

    public Stage playerStage;


    @FXML
    public void initialize() {
        logger.info("初始化完成!");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (playerStage != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            showPlayCard(MusicPlayer.getInstance().getCurrentMusicItem());
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            playCard.setVisible(false);
                        }
                    });
                }
            }
        },0,1000);
        playCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {   // 双击打开（推荐）
                    if (playerStage != null) {
                        ((Stage) searchList.getScene().getWindow()).hide();
                        playerStage.show();
                    }
                }
            }
        });
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playMusic();
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





    public void showPlayCard (MusicItem musicItem) {
        if (musicItem != null) {
            playCard.setVisible(true);
            if (isSwitchMusic) {
                MusicPlayer.getInstance().loadCoverImage(musicItem.coverUrl, cover, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Img/music_bg.jpg"))));
                isSwitchMusic = false;
            }
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
                    isSwitchMusic = true;
                    MusicItem selectedItem = searchList.getSelectionModel().getSelectedItem();
                    openPlayerWindow(selectedItem,items);
                }
            }
        });
    }


    private void openPlayerWindow(MusicItem musicItem,ObservableList<MusicItem> items) {
        try {
            playBtn.setStyle("-fx-background-image: url(\"Img/radio_stop.png\");");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/PlayerView.fxml"));
            Parent root = loader.load();
            // 获取 PlayerView 的控制器，并传递数据
            PlayerView playerController = loader.getController();
            playerController.startMusic(musicItem,items,((Stage) searchList.getScene().getWindow()));     // ← 关键：传递数据

            // 创建新窗口
            playerStage = new Stage();
            playerStage.setTitle("正在播放:" + musicItem.song);
            playerStage.initOwner(searchList.getScene().getWindow()); // 绑定父窗口
            try {
                Image icon = new Image(Main.class.getResourceAsStream("/Img/icon.jpg"));
                playerStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("loading fail");
            }
            Scene scene = new Scene(root, 850, 550);
            playerStage.setScene(scene);
            playerStage.setResizable(false);
            ((Stage) searchList.getScene().getWindow()).hide();
            playerStage.show();
        } catch (Exception e) {
            logger.error("打开 PlayerView 失败", e);
        }
    }


}
