package com.shellever.iceplayer;

/**
 * Author: Shellever
 * Date:   12/26/2016
 * Email:  shellever@163.com
 */

public class SearchResult {

    private String songName;
    private String singerName;
    private String url;
    private String album;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "songName='" + songName + '\'' +
                ", singerName='" + singerName + '\'' +
                ", url='" + url + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
