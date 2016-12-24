package com.shellever.iceplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

/**
 * Author: Shellever
 * Date:   12/24/2016
 * Email:  shellever@163.com
 */

//
// BaseActivity中进行服务的绑定操作
// 使用Binder机制来实现Activity与Service之间的交互
//
public abstract class BaseActivity extends AppCompatActivity {

    protected MyMusicService mMusicService;     // 声明为protected权限，子类可以直接使用
    private boolean isBound = false;            // 是否已绑定


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyMusicService.MusicServiceBinder binder = (MyMusicService.MusicServiceBinder) service;
            mMusicService = binder.getMusicService();
            isBound = true;     // 绑定成功

            mMusicService.setMusicUpdateListener(listener);
            // 绑定成功后调用监听onChange方法

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;    // 绑定失败
        }
    };

    // 异步绑定，绑定成功后会回调onServiceConnected()方法
    // Context.BIND_AUTO_CREATE - 绑定时自动创建Service
    // 执行绑定：bindService() -> onCreate() -> onBind()
    public void bindMusicService() {
        if (!isBound) {
            Intent intent = new Intent(this, MyMusicService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    // 解除绑定服务
    public void unbindMusicService() {
        if (isBound) {
            unbindService(conn);
            isBound = false;    // 解除绑定
        }
    }

    public abstract void publish(int progress);
    public abstract void change(int position);

    private MyMusicService.MusicUpdateListener listener = new MyMusicService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

}
