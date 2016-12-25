package com.shellever.iceplayer;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Author: Shellever
 * Date:   12/23/2016
 * Email:  shellever@163.com
 */

public class LocalMusicFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final boolean DEBUG = true;

    private ListView mLocalMusicLv;
    private ImageView mAlbumIv;
    private ImageView mPlayPauseActionIv;
    private ImageView mNextActionIv;
    private TextView mPlayingSongNameTv;
    private TextView mPlayingSingerNameTv;

    private MainActivity mMainActivity;
    private MyMusicService mMusicService;

    private List<Mp3Info> mMp3InfoList;
    private LocalMusicAdapter mLocalMusicAdapter;


    public static LocalMusicFragment newInstance() {
        return new LocalMusicFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mMainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);

        mLocalMusicLv = (ListView) view.findViewById(R.id.lv_local_music);
        mLocalMusicLv.setOnItemClickListener(this);

        mAlbumIv = (ImageView) view.findViewById(R.id.iv_album);
        mPlayPauseActionIv = (ImageView) view.findViewById(R.id.iv_action_play_pause);
        mNextActionIv = (ImageView) view.findViewById(R.id.iv_action_next);
        mAlbumIv.setOnClickListener(this);
        mPlayPauseActionIv.setOnClickListener(this);
        mNextActionIv.setOnClickListener(this);

        mPlayingSongNameTv = (TextView) view.findViewById(R.id.tv_playing_song_name);
        mPlayingSingerNameTv = (TextView) view.findViewById(R.id.tv_playing_singer_name);

        loadMp3InfoList();      // 加载Mp3列表
        return view;
    }

    // ===================================================
    // 绑定后会回调onChange()方法，从而回调changeUIStatus()方法
    @Override
    public void onResume() {
        super.onResume();
        mMainActivity.bindMusicService();     // 绑定服务
    }

    @Override
    public void onPause() {
        super.onPause();
        mMainActivity.unbindMusicService();   // 解除绑定
    }
    // ===================================================

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void loadMp3InfoList() {
        mMp3InfoList = MediaUtils.getMp3InfoList(mMainActivity);
        mLocalMusicAdapter = new LocalMusicAdapter(mMainActivity, mMp3InfoList);
        mLocalMusicLv.setAdapter(mLocalMusicAdapter);
    }

    // AdapterView.OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMainActivity.mMusicService.play(position); // 绑定成功之后才能调用Service的play方法，否则会报空指针异常
        mPlayPauseActionIv.setImageResource(R.drawable.pause);
        showToast("LocalMusicFragment::onItemClick()");
    }

    // 每次点击列表歌曲项或者切换至下一首时会被回调
    public void changeUIStatus(int position) {
        Mp3Info mp3Info = mMp3InfoList.get(position);
        mPlayingSongNameTv.setText(mp3Info.getTitle());
        mPlayingSingerNameTv.setText(mp3Info.getArtist());
        if (mMainActivity.mMusicService.isPlaying()) {
            mPlayPauseActionIv.setImageResource(R.drawable.pause);
        } else {
            mPlayPauseActionIv.setImageResource(R.drawable.play);
        }
        mAlbumIv.setImageBitmap(MediaUtils.getArtwork(mMainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true));
    }

    // View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_action_play_pause:
                if (mMainActivity.mMusicService.isPlaying()) {      // 播放 -> 暂停
                    mMainActivity.mMusicService.pause();
                    mPlayPauseActionIv.setImageResource(R.drawable.play);
                } else {
                    if (mMainActivity.mMusicService.isPause()) {        // 暂停 -> 播放
                        mMainActivity.mMusicService.start();
                        mPlayPauseActionIv.setImageResource(R.drawable.pause);
                    } else {
                        int curPos = mMainActivity.mMusicService.getCurrentPosition();
                        mMainActivity.mMusicService.play(curPos);        // 0
                    }
                }
                break;
            case R.id.iv_action_next:
                mMainActivity.mMusicService.next();
                break;
            case R.id.iv_album:
                Intent intent = new Intent(mMainActivity, PlayingMusicActivity.class);
                intent.putExtra("progress", mMainActivity.mMusicService.getCurrentProgress());
                startActivity(intent);
                break;
        }
    }

    // 用于调试信息输出
    private void showToast(String info) {
        if (DEBUG) {
            Toast.makeText(mMainActivity, info, Toast.LENGTH_SHORT).show();
        }
    }
}
