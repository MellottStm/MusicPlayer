package com.smt;

import com.smt.Data.MusicItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class Configure {

    public final static String getSearchMusicListUrl = "https://api.vkeys.cn/v2/music/netease";

    public final static String getMusicPlayUrl = "https://api.qijieya.cn/meting/";

    public final static Map<String, Image> imageCache = new HashMap<>();

    public enum playMod {
        list("list"), //列表循环
        random("random"), //随机播放
        single("single"); //单曲循环

        public String value;

        playMod (String value) {
            this.value = value;
        }

        public static playMod getByValue(String value) {
            for (playMod mod : values()) {
                if (mod.value.equals(value)) {
                    return mod;
                }
            }
            return null;
        }
    }

    public static MusicItem currentMusic = new MusicItem();

    public static playMod currentPlayMod = playMod.list;

    public static ObservableList<MusicItem> collectedList = FXCollections.observableArrayList();


}
