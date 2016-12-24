package com.shellever.iceplayer;

import android.app.Application;

/**
 * Author: Shellever
 * Date:   12/14/2016
 * Email:  shellever@163.com
 */

// AndroidManifest.xml/Application
// android:name=".CrashApplication"

public class CrashApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化CrashHandler
        CrashHandler.getInstance().init(this);
    }

}
