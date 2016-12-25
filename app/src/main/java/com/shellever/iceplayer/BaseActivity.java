package com.shellever.iceplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

    private static final boolean DEBUG = true;

    protected MyMusicService mMusicService;     // 声明为protected权限，子类可以直接使用
    private boolean isBound = false;            // 是否已绑定


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 绑定服务成功后回调接口
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyMusicService.MusicServiceBinder binder = (MyMusicService.MusicServiceBinder) service;
            mMusicService = binder.getMusicService();
            isBound = true;     // 绑定成功
            showToast("bind success");

            // 绑定成功后设置回调事件监听
            mMusicService.setMusicUpdateListener(listener);

            // 绑定成功后调用监听onChange()方法
            listener.onChange(mMusicService.getCurrentPosition());
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
            showToast("unbind success");
        }
    }

    // 模板设计模式
    // 使用抽象方式，强制让子类实现其具体操作
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

    // 用于调试信息输出
    private void showToast(String info) {
        if (DEBUG) {
            Toast.makeText(BaseActivity.this, info, Toast.LENGTH_SHORT).show();
        }
    }

}
