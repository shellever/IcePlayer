package com.shellever.iceplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

public class PlayingMusicActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int MSG_UPDATE_PROGRESS = 1;

    private ImageView mAlbumIv;
    private ImageView mPlayModeIv;
    private ImageView mFavoriteIv;
    private ImageView mPrevActionIv;
    private ImageView mPlayPauseActionIv;
    private ImageView mNextActionIv;
    private TextView mSongNameTv;
    private TextView mStartedTimeTv;
    private TextView mSongDurationTv;
    private SeekBar mSeekBar;

    private List<Mp3Info> mMp3InfoList;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_PROGRESS) {
                int progress = msg.arg1;
                mStartedTimeTv.setText(MediaUtils.formatTime(progress));
                mSeekBar.setProgress(progress);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_music);

        mAlbumIv = (ImageView) findViewById(R.id.iv_activity_album);

        mPlayModeIv = (ImageView) findViewById(R.id.iv_activity_playing_mode);
        mFavoriteIv = (ImageView) findViewById(R.id.iv_activity_favorite);
        mPrevActionIv = (ImageView) findViewById(R.id.iv_activity_action_prev);
        mPlayPauseActionIv = (ImageView) findViewById(R.id.iv_activity_action_play_pause);
        mNextActionIv = (ImageView) findViewById(R.id.iv_activity_action_next);
        mPlayModeIv.setOnClickListener(this);
        mFavoriteIv.setOnClickListener(this);
        mPrevActionIv.setOnClickListener(this);
        mPlayPauseActionIv.setOnClickListener(this);
        mNextActionIv.setOnClickListener(this);

        mSongNameTv = (TextView) findViewById(R.id.tv_activity_playing_song_name);
        mStartedTimeTv = (TextView) findViewById(R.id.tv_activity_started_time);
        mSongDurationTv = (TextView) findViewById(R.id.tv_activity_song_duration);

        mSeekBar = (SeekBar) findViewById(R.id.sb_activity_progress);
        mSeekBar.setOnSeekBarChangeListener(this);

        // 从LocalMusicFragment转到PlayingMusicActivity时，需要将进度值传过来，
        // 因为更新率为500毫秒，可能出现00:00的情况，故需要在创建时进行赋值操作
        int progress = getIntent().getIntExtra("progress", 0);
        mStartedTimeTv.setText(MediaUtils.formatTime(progress));

        mMp3InfoList = MediaUtils.getMp3InfoList(this);
    }

    // ========================================
    // 每一次重新进入播放界面时都要进行绑定操作
    @Override
    protected void onResume() {
        super.onResume();
        bindMusicService();     // 绑定服务
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindMusicService();   // 解除绑定
    }
    // ========================================

    // Abstract method defined in BaseActivity
    @Override
    public void publish(int progress) {
        // 此处是后台服务中子线程的回调，无法更新UI线程中的UI控件
        //mStartedTimeTv.setText(MediaUtils.formatTime(progress));
        //mSeekBar.setProgress(progress);
        Message msg = handler.obtainMessage(MSG_UPDATE_PROGRESS);
        msg.arg1 = progress;
        handler.sendMessage(msg);
    }

    // 绑定成功后异步回调 (可以做些初始化操作)
    @Override
    public void change(int position) {
        Mp3Info mp3Info = mMp3InfoList.get(position);
        mSongNameTv.setText(mp3Info.getTitle());
        mAlbumIv.setImageBitmap(MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false));
        mSongDurationTv.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        mSeekBar.setProgress(0);
        mSeekBar.setMax((int) mp3Info.getDuration());
        if (mMusicService.isPlaying()) {
            mPlayPauseActionIv.setImageResource(R.drawable.pause);  // 正在播放则显示暂停按钮
        } else {
            mPlayPauseActionIv.setImageResource(R.drawable.play);   // 正在暂停则显示播放按钮
        }

        // 初始化播放模式状态
        switch (mMusicService.getPlayMode()) {
            case MyMusicService.PLAY_MODE_ORDER:
                mPlayModeIv.setImageResource(R.drawable.order);
                break;
            case MyMusicService.PLAY_MODE_RANDOM:
                mPlayModeIv.setImageResource(R.drawable.random);
                break;
            case MyMusicService.PLAY_MODE_SINGLE:
                mPlayModeIv.setImageResource(R.drawable.single);
                break;
        }

        // 初始化收藏状态
        MainApplication app = (MainApplication) getApplication();
        try {
            Mp3Info result = app.mDbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", mp3Info.getId()));
            if (result == null) {
                mFavoriteIv.setImageResource(R.drawable.xin_bai);
            } else {
                mFavoriteIv.setImageResource(R.drawable.xin_hong);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    // View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_activity_action_prev:      // 上一首
                mMusicService.prev();
                break;
            case R.id.iv_activity_action_next:      // 下一首
                mMusicService.next();
                break;
            case R.id.iv_activity_action_play_pause:// 播放或暂停
                if (mMusicService.isPlaying()) {
                    mMusicService.pause();
                    mPlayPauseActionIv.setImageResource(R.drawable.play);   // 正在播放则点击后显示暂停按钮
                } else {
                    if (mMusicService.isPause()) {
                        mPlayPauseActionIv.setImageResource(R.drawable.pause);  // 正在暂停则点击后显示播放按钮
                        mMusicService.start();
                    } else {
                        int curPos = mMusicService.getCurrentPosition();
                        mMusicService.play(curPos);        // default 0
                    }
                }
                break;
            case R.id.iv_activity_playing_mode:     // 播放模式
                switch (mMusicService.getPlayMode()) {
                    case MyMusicService.PLAY_MODE_ORDER:
                        mMusicService.setPlayMode(MyMusicService.PLAY_MODE_RANDOM);
                        mPlayModeIv.setImageResource(R.drawable.random);
                        showTip(R.string.tip_play_mode_random);
                        break;
                    case MyMusicService.PLAY_MODE_RANDOM:
                        mMusicService.setPlayMode(MyMusicService.PLAY_MODE_SINGLE);
                        mPlayModeIv.setImageResource(R.drawable.single);
                        showTip(R.string.tip_play_mode_single);
                        break;
                    case MyMusicService.PLAY_MODE_SINGLE:
                        mMusicService.setPlayMode(MyMusicService.PLAY_MODE_ORDER);
                        mPlayModeIv.setImageResource(R.drawable.order);
                        showTip(R.string.tip_play_mode_order);
                        break;
                }
                break;
            case R.id.iv_activity_favorite:
                MainApplication app = (MainApplication) getApplication();
                Mp3Info current = mMp3InfoList.get(mMusicService.getCurrentPosition());
                try {
                    Mp3Info result = app.mDbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", current.getId()));
                    if (result == null) {
                        current.setMp3InfoId(current.getId());  // 因为DbUtils中默认主键是id
                        app.mDbUtils.save(current);             // 保存到数据库
                        showTip(R.string.tip_favorite_like);
                        mFavoriteIv.setImageResource(R.drawable.xin_hong);
                    } else {
                        app.mDbUtils.deleteById(Mp3Info.class, result.getId());
                        showTip(R.string.tip_favorite_unlike);
                        mFavoriteIv.setImageResource(R.drawable.xin_bai);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // isPause有问题，即起到的是刚开始启动时没有播放状态时会首先调用play(0)一次，以后就不再调用
    // 在暂停状态下，拖动SeekBar时，没有更新Play_Pause按钮状态
    // =============================================================================
    // SeekBar.OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //mMusicService.pause();          // 先暂停
            mMusicService.seekTo(progress); // 再根据拖动来设置进度
            //mMusicService.start();          // 最后播放

            mStartedTimeTv.setText(MediaUtils.formatTime(progress));    // 拖动的同时更新已经开始的时间位置
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    // =============================================================================

    // 播放模式提示信息
    private void showTip(int resId) {
        Toast.makeText(PlayingMusicActivity.this, getString(resId), Toast.LENGTH_SHORT).show();
    }
}
