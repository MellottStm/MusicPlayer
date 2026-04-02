package com.smt.Data;

public class MusicItem {

    public String id;

    public String song;

    public String singer;

    public String album;

    public String coverUrl;


    public MusicItem () {

    }


    public MusicItem (String id,String song,String singer,String album,String coverUrl) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.coverUrl = coverUrl;
    }


}
