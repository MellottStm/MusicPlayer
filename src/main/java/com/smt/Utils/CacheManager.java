package com.smt.Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smt.Configure;
import com.smt.Data.MusicItem;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CacheManager {

    private static String TAG = "CacheManager";

    public final static Logger logger = Logger.getLogger(TAG);

    private static final String CACHE_FILE = "./cache/app_data.json";

    private static JSONObject cacheJson = new JSONObject();

    /** 保存缓存到相对路径的 JSON 文件 */
    public static void saveCurrentMusicCache(MusicItem musicItem) {
        try {
            // 更新数据
            // 确保目录存在
            File file = new File(CACHE_FILE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            JSONObject saveJson = loadCache();
            // 写入 JSON
            if (saveJson == null) {
                saveJson = new JSONObject();
            }
            JSONObject currentMusicJson = new JSONObject();
            currentMusicJson.put("id",musicItem.id);
            currentMusicJson.put("song",musicItem.song);
            currentMusicJson.put("singer",musicItem.singer);
            currentMusicJson.put("coverUrl",musicItem.coverUrl);
            currentMusicJson.put("album",musicItem.album);
            currentMusicJson.put("isCollected",musicItem.isCollected);
            saveJson.put("currentMusic",currentMusicJson);
            Path path = Paths.get(CACHE_FILE);
            Files.createDirectories(path.getParent());
            Files.writeString(path, saveJson.toJSONString(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            logger.warn(ex);
        }
    }

    public static void savePlayModCache (String playMod) {
        try {
            // 确保目录存在
            File file = new File(CACHE_FILE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            JSONObject saveJson = loadCache();
            // 写入 JSON
            if (saveJson == null) {
                saveJson = new JSONObject();
            }
            saveJson.put("currentPlayMod",playMod);
            Path path = Paths.get(CACHE_FILE);
            Files.createDirectories(path.getParent());
            Files.writeString(path, saveJson.toJSONString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn(e);
        }
    }


    public static void saveCollectedMusicCache(ObservableList<MusicItem> items) {
        try {
            // 确保目录存在
            File file = new File(CACHE_FILE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            JSONObject saveJson = loadCache();
            // 写入 JSON
            if (saveJson == null) {
                saveJson = new JSONObject();
            }
            JSONArray collectedMusicJsonArray = new JSONArray();
            for (MusicItem musicItem:items) {
                JSONObject musicJson = new JSONObject();
                musicJson.put("id",musicItem.id);
                musicJson.put("song",musicItem.song);
                musicJson.put("singer",musicItem.singer);
                musicJson.put("coverUrl",musicItem.coverUrl);
                musicJson.put("album",musicItem.album);
                musicJson.put("isCollected",musicItem.isCollected);
                collectedMusicJsonArray.add(musicJson);
            }
            saveJson.put("collectedMusic",collectedMusicJsonArray);
            Path path = Paths.get(CACHE_FILE);
            Files.createDirectories(path.getParent());
            Files.writeString(path, saveJson.toJSONString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn(e);
        }
    }



    /** 从 JSON 文件加载缓存 */
    public static JSONObject loadCache() {
        JSONObject loadJson = null;
        try {
            File file = new File(CACHE_FILE);
            if (!file.exists()) {
                logger.info("缓存文件不存在，将使用默认值");
                return null;
            }
        String content = Files.readString(Paths.get(CACHE_FILE));
        loadJson = JSONObject.parseObject(content);
        logger.info("加载获取的缓存内容:" + loadJson.toJSONString());
        } catch (Exception ex) {
            logger.warn(ex);
        }
        return loadJson;
    }


}
