package com.shellever.iceplayer;

/**
 * Author: Shellever
 * Date:   12/23/2016
 * Email:  shellever@163.com
 */

public class SongInfoBean {

    private String singerName;
    private String songName;
    private String songDuration;


    public SongInfoBean() {
    }

    public SongInfoBean(String singerName, String songName, String songDuration) {
        this.singerName = singerName;
        this.songName = songName;
        this.songDuration = songDuration;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    @Override
    public String toString() {
        return "SongInfoBean{" +
                "singerName='" + singerName + '\'' +
                ", songName='" + songName + '\'' +
                ", songDuration='" + songDuration + '\'' +
                '}';
    }
}
