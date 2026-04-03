package com.smt.Utils;

import com.alibaba.fastjson.JSONArray;
import com.smt.Data.MusicItem;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.log4j.Logger;

public class MusicPlayer {

    private static String TAG = "PlayerView";

    private final static Logger logger = Logger.getLogger(TAG);

    private static MusicPlayer instance;

    private MediaPlayer mediaPlayer;

    private MusicPlayer() {}   // 单例

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void play (JSONArray resJson) {
        mediaPlayer = new MediaPlayer(new Media(resJson.getJSONObject(0).getString("url")));
        // 当媒体准备好后，设置总时长
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getTotalDuration();
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

}
