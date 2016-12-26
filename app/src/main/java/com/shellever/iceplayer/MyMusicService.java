package com.shellever.iceplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;
import java.util.Random;
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

public class MyMusicService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final int FLAG_PLAY_LIST_LOCAL = 1;
    public static final int FLAG_PLAY_LIST_FAVORITE = 2;

    public static final int PLAY_MODE_ORDER = 1;       // 顺序播放
    public static final int PLAY_MODE_RANDOM = 2;      // 随机播放
    public static final int PLAY_MODE_SINGLE = 3;      // 单曲循环

    private MediaPlayer mMediaPlayer;
    List<Mp3Info> mMp3InfoList;                         // 供Fragment数据同步
    //private List<Mp3Info> mMp3InfoList;
    private int curPos;                                 // 当前播放的歌曲位置
    private boolean isPause = false;
    private int mPlayMode = PLAY_MODE_ORDER;            // 默认为顺序播放
    private Random random = new Random();
    private int curPlayListFlag = FLAG_PLAY_LIST_LOCAL;          // 默认播放列表为本地音乐

    private ExecutorService mThreadExecutor;            // 单线程池

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

        // 恢复状态值
        MainApplication app = (MainApplication) getApplication();
        curPos = app.sp.getInt("curPos", 0);
        mPlayMode = app.sp.getInt("mPlayMode", PLAY_MODE_ORDER);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mMp3InfoList = MediaUtils.getMp3InfoList(this);     // 耗时操作，应该进行异步处理

        mThreadExecutor = Executors.newSingleThreadExecutor();   // 线程池初始化
        mThreadExecutor.execute(mMusicStatusUpdateTask);     // 执行任务
    }

    public int getCurPlayListFlag() {
        return curPlayListFlag;
    }

    public void setCurPlayListFlag(int curPlayListFlag) {
        this.curPlayListFlag = curPlayListFlag;
    }

    public int getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(int mPlayMode) {
        this.mPlayMode = mPlayMode;
    }

    // 播放歌曲 (从暂停状态开始播放)
    public void start() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    // 播放列表上指定位置的歌曲 (pos的检查由调用者负责)
    public void play(int pos) {
        Mp3Info mp3Info = mMp3InfoList.get(pos);
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));  // 设置数据源
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
            isPause = true;     // 暂停
        }
    }

    // curPos只由prev()和next()方法才能手动修改其值，
    // 故只需要在此两个方法中对修改后的curPos进行界限判断
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

    // 在fragment或者activity中可以获得状态
    public boolean isPause() {
        return isPause;
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
                try {
                    Thread.sleep(500);      // 限制更新频率500ms一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // 用于设置播放收藏列表的音乐
    public void setMp3InfoList(List<Mp3Info> list) {
        mMp3InfoList = list;
    }

    // 检查MediaPlayer是否正在播放
    private boolean checkMediaPlayer() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void setMusicUpdateListener(MusicUpdateListener listener) {
        mListener = listener;
    }

    // ======================================================
    // MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {    // 准备完成后再开始播放
        mMediaPlayer.start();               // 1st
        if (mListener != null) {
            mListener.onChange(curPos);     // 2nd 播放位置改变回调，因为在onChange()会进行isPlayer()判断，故必须start()后才回调
        }
    }

    // MediaPlayer.OnCompletionListener
    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (mPlayMode) {             // 播放模式
            case PLAY_MODE_ORDER:
                next();
                break;
            case PLAY_MODE_RANDOM:
                play(random.nextInt(mMp3InfoList.size()));
                break;
            case PLAY_MODE_SINGLE:
                play(curPos);
                break;
        }
    }

    // MediaPlayer.OnErrorListener
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer.reset();       // 错误时直接复位
        return false;
    }
    // ======================================================

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
