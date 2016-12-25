package com.shellever.iceplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 实现功能：
 * 1、点击列表上的某首歌播放
 * 2、点击播放按钮，从暂停转为播放状态
 * 3、点击暂停按钮，从播放状态转为暂停状态
 * 4、上一首
 * 5、下一首
 * 6、播放进度显示
 * 7、播放模式
 */

public class MyMusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private List<Mp3Info> mMp3InfoList;
    private int curPos;       // 当前播放的歌曲位置

    private ExecutorService mThreadExecutor;    // 单线程池

    private MusicUpdateListener mListener;


    public MyMusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {  // AIDL for IPC
        // TODO: Return the communication channel to the service.
        return new MusicServiceBinder();    // 返回一个Binder对象
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMp3InfoList = MediaUtils.getMp3InfoList(this);     // 耗时操作，应该进行异步处理

        mThreadExecutor = Executors.newSingleThreadExecutor();   // 线程池初始化
        mThreadExecutor.execute(mMusicStatusUpdateTask);     // 执行任务
    }

    // 播放歌曲 (从暂停状态开始播放)
    public void start() {
        if (checkMediaPlayer()) {
            mMediaPlayer.start();
        }
    }

    // 播放列表上指定位置的歌曲 (pos的检查由调用者负责)
    public void play(int pos) {
        Mp3Info mp3Info = mMp3InfoList.get(pos);
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));  // 设置数据源
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mListener != null) {
                        mListener.onChange(curPos);
                    }
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
            mMediaPlayer.prepareAsync();    // 异步准备
            curPos = pos;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 暂停播放
    public void pause() {
        if (checkMediaPlayer()) {
            mMediaPlayer.pause();
        }
    }

    // 播放上一首
    public void prev() {
        curPos--;
        if (curPos < 0) {       // 上限限制0
            curPos = mMp3InfoList.size() - 1;
        }
        play(curPos);
    }

    // 播放下一首
    public void next() {
        curPos++;
        if (curPos >= mMp3InfoList.size()) {    // 下限限制size-1
            curPos = 0;
        }
        play(curPos);
    }

    // 在音乐播放中，获得播放的位置信息
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    // 跳转到某个地方
    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    // 返回当前的位置
    public int getCurrentPosition() {
        return curPos;
    }

    // 获得当前位置
    public int getCurrentProgress() {
        if (checkMediaPlayer()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    // 反馈状态
    public boolean isPlaying() {
        return checkMediaPlayer();
    }

    // 实时更新播放进度
    private Runnable mMusicStatusUpdateTask = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (checkMediaPlayer()) {
                    if (mListener != null) {
                        mListener.onPublish(getCurrentProgress());  // 更新进度
                    }
                }
            }
        }
    };

    // 检查MediaPlayer是否正在播放
    private boolean checkMediaPlayer() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void setMusicUpdateListener(MusicUpdateListener listener) {
        mListener = listener;
    }

    // 状态更新回调接口
    public interface MusicUpdateListener {
        void onPublish(int progress);       // 更新进度条

        void onChange(int position);        // 切换播放位置
    }

    // AIDL - IPC
    // 使用Binder机制将此服务对象传递到Activity中，
    // 使Activity可以直接调用服务中所提供的方法
    public class MusicServiceBinder extends Binder {
        public MyMusicService getMusicService() {
            return MyMusicService.this;     // 获取当前Service的实例
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mThreadExecutor != null && mThreadExecutor.isTerminated()) {
            mThreadExecutor.shutdown();
            mThreadExecutor = null;
        }
    }
}
