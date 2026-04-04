package com.smt;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class Configure {

    public final static String getSearchMusicListUrl = "https://api.vkeys.cn/v2/music/netease";

    public final static String getMusicPlayUrl = "https://api.qijieya.cn/meting/";

    public final static Map<String, Image> IMAGE_CACHE = new HashMap<>();

    public enum playMod {
        list, //列表循环
        random, //随机播放
        single, //单曲循环
    }

    public static playMod currentPlayMod = playMod.list;


}
