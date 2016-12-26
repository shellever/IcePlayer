package com.shellever.iceplayer;

/**
 * Author: Shellever
 * Date:   12/26/2016
 * Email:  shellever@163.com
 */

public class BaseConstant {

    public static final String SP_NAME = "IcePlayer";
    public static final String DB_NAME = "IcePlayer.db";
    public static final int MAX_RECENT_PLAY_RECORD = 12;
    public static final String DIR_MUSIC = "/ice_music";
    public static final String DIR_MUSIC_LRC = "/ice_music/lrc/";
    public static final int SUCCESS = 1;        // 成功标记
    public static final int FAILURE = 2;        // 失败标记

    // =====================================================
    // 定义一个与具体平台无关的URL请求参数
    public static final String BASE_MUSIC_URL = "";     // baidu / migu
    public static final String MUSIC_URL = "";
    // =====================================================

    // =====================================================
    // 百度音乐
    public static final String BAIDU_MUSIC_URL = "http://music.baidu.com/";
    // 热歌榜-百度音乐排行榜
    public static final String BAIDU_DAYHOT_URL = "http://music.baidu.com/top/dayhot/?pst=shouyeTop";
    // =====================================================


    // =====================================================
    // =====================================================
    // =====================================================
    // =====================================================

    // 百度音乐网址
    public static final String BAIDU_URL = "http://music.baidu.com/";

    // 热歌榜
    public static final String BAIDU_DAYHOT = "top/dayhot/?pst=shouyeTop";
    public static final String BAIDU_DAYHOT2 = "http://music.baidu.com/top/new";

    // 搜索
    public static final String BAIDU_SEARCH = "search?key"; //   /search/song  /search?key

    //歌词  "http://music.baidu.com/search/lrc?key=" + 歌名 + " " + 歌手
    public static final String BAIDU_LRC_SEARCH_HEAD = "http://music.baidu.com/search/lrc?key=";

    // 用户代理
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:48.0) Gecko/20100101 Firefox/48.0";
    //public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:45.0) Gecko/20100101 Firefox/45.0";
    //public static final String USER_AGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; Tablet PC 2.0; .NET4.0E)";

}
