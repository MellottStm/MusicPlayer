package com.smt.Data;

public class MusicItem {

    public String id;

    public String song;

    public String singer;

    public String album;

    public String coverUrl;

    public boolean isCollected;//是否收藏

    public MusicItem () {

    }


    public MusicItem (String id,String song,String singer,String album,String coverUrl,boolean isCollected) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.coverUrl = coverUrl;
        this.isCollected = isCollected;
    }


}
