package com.smt.Utils;

import com.alibaba.fastjson.JSONArray;
import com.smt.Data.MusicItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import static com.smt.Configure.IMAGE_CACHE;

public class MusicPlayer {

    private static String TAG = "PlayerView";

    private final static Logger logger = Logger.getLogger(TAG);

    private static MusicPlayer instance;

    private MediaPlayer mediaPlayer;

    private MusicItem currentMusicItem;

    private MusicPlayer() {}   // 单例

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void play (MusicItem currentMusicItem,JSONArray resJson,PlayerCallBack playerCallBack) {
        this.currentMusicItem = currentMusicItem;
        stop();
        mediaPlayer = new MediaPlayer(new Media(resJson.getJSONObject(0).getString("url")));
        // 当媒体准备好后，设置总时长
        mediaPlayer.setOnReady(playerCallBack::onReady);

        // 实时更新当前播放时间和进度条
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            playerCallBack.onProgress(oldTime,newTime);
        });

        // 播放结束
        mediaPlayer.setOnEndOfMedia(() -> {
            logger.info("播放结束");
            // 可以在这里自动下一首
        });
        mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }


    public MusicItem getCurrentMusicItem () {
        return currentMusicItem;
    }


    // 获取当前播放进度（秒）
    public double getCurrentTimeSeconds() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentTime().toSeconds();
        }
        return 0;
    }

    //获取总进度
    public String getTotalDuration () {
        if (mediaPlayer != null) {
            return formatTime(mediaPlayer.getTotalDuration());
        }
        return "00:00";
    }



    private String formatTime(Duration duration) {
        if (duration == null || duration.isUnknown() || duration.isIndefinite()) {
            return "00:00";
        }
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public interface PlayerCallBack {

        void onReady ();

        void onProgress (Duration oldTime,Duration newTime);

        void onComplete ();

    }

    public void loadCoverImage(String coverUrl, ImageView coverImage, Image defaultImage) {
        // 一开始就显示默认图片
        coverImage.setImage(defaultImage);

        if (coverUrl == null || coverUrl.trim().isEmpty()) {
            return;
        }

        if (IMAGE_CACHE.containsKey(coverUrl)) {
            Image cachedImage = IMAGE_CACHE.get(coverUrl);
            if (cachedImage != null) {
                logger.info("缓存了url:" + coverUrl);
                coverImage.setImage(cachedImage);
                return;
            }
        }

        // 创建异步加载的图片
        Image onlineImage = new Image(coverUrl, true);

        // 监听加载完成（成功或失败）
        onlineImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
            if (newProgress.doubleValue() >= 1.0) {  // 加载完成
                if (!onlineImage.isError()) {
                    IMAGE_CACHE.put(coverUrl, onlineImage);
                    // 加载成功，切换为在线图片
                    coverImage.setImage(onlineImage);
                } else {
                    IMAGE_CACHE.put(coverUrl, null); // 标记失败，避免重复请求
                }
                // 加载失败就什么都不做，继续显示默认图片
            }
        });
    }


}
