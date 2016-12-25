package com.shellever.iceplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author: Shellever
 * Date:   12/26/2016
 * Email:  shellever@163.com
 */

public class MainApplication extends Application {

    // 在MainActivity的onDestroy()中 保存状态值
    // 在MyMusicService的onCreate()中 恢复状态值
    public SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(BaseConstant.SP_NAME, Context.MODE_PRIVATE);
    }
}
