package com.shellever.iceplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Author: Shellever
 * Date:   12/24/2016
 * Email:  shellever@163.com
 */

public class MediaUtils {

    // 用于从数据库中查询歌曲的信息，保存在List当中
    public static ArrayList<Mp3Info> getMp3InfoList(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DURATION + ">=180000",   // 最小音乐长度180sec
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );

        ArrayList<Mp3Info> mMp3InfoList = new ArrayList<Mp3Info>();
        Mp3Info mp3Info;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));    // 是否为音乐
                if(isMusic == 0) {
                    continue;
                }
                mp3Info = new Mp3Info();

                mp3Info.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));           // id
                mp3Info.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));    // 歌名
                mp3Info.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));  // 艺术家
                mp3Info.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));    // 专辑
                mp3Info.setAlbumId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))); // 专辑id
                mp3Info.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));// 时长
                mp3Info.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));        // 大小
                mp3Info.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));       // 路径

                mMp3InfoList.add(mp3Info);
            }
            cursor.close();
        }
        return mMp3InfoList;
    }

    // 格式化时间 (03:09)
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }

        return min + ":" + sec.trim().substring(0, 2);
    }

}
