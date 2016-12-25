package com.shellever.iceplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class PlayingMusicActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_UPDATE_PROGRESS = 1;

    private ImageView mAlbumIv;
    private ImageView mPlayModeIv;
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
        mPrevActionIv = (ImageView) findViewById(R.id.iv_activity_action_prev);
        mPlayPauseActionIv = (ImageView) findViewById(R.id.iv_activity_action_play_pause);
        mNextActionIv = (ImageView) findViewById(R.id.iv_activity_action_next);
        mPlayModeIv.setOnClickListener(this);
        mPrevActionIv.setOnClickListener(this);
        mPlayPauseActionIv.setOnClickListener(this);
        mNextActionIv.setOnClickListener(this);

        mSongNameTv = (TextView) findViewById(R.id.tv_activity_playing_song_name);
        mStartedTimeTv = (TextView) findViewById(R.id.tv_activity_started_time);
        mSongDurationTv = (TextView) findViewById(R.id.tv_activity_song_duration);

        mSeekBar = (SeekBar) findViewById(R.id.sb_activity_progress);

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

    // 绑定成功后异步回调
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
    }

    // View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_activity_action_prev:
                mMusicService.prev();
                break;
            case R.id.iv_activity_action_play_pause:
                if (mMusicService.isPlaying()) {
                    mMusicService.pause();
                    mPlayPauseActionIv.setImageResource(R.drawable.play);   // 正在播放则点击后显示暂停按钮
                } else {
                    if (mMusicService.isPause()) {
                        mPlayPauseActionIv.setImageResource(R.drawable.pause);  // 正在暂停则点击后显示播放按钮
                        mMusicService.start();
                    } else {
                        mMusicService.play(0);        // 可以使用SharedPreferences来保存播放位置
                    }
                }
                break;
            case R.id.iv_activity_action_next:
                mMusicService.next();
                break;
        }
    }
}