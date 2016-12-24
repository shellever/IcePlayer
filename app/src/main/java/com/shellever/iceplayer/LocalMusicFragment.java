package com.shellever.iceplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * Author: Shellever
 * Date:   12/23/2016
 * Email:  shellever@163.com
 */

public class LocalMusicFragment extends Fragment implements AdapterView.OnItemClickListener{

    private MainActivity mMainActivity;
    private MyMusicService mMusicService;

    private ListView mLocalMusicLv;
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
        loadMp3InfoList();      // 加载Mp3列表
        mMainActivity.bindMusicService();     // 绑定服务
        return view;
    }

    private void loadMp3InfoList() {
        mMp3InfoList = MediaUtils.getMp3InfoList(mMainActivity);
        mLocalMusicAdapter = new LocalMusicAdapter(mMainActivity, mMp3InfoList);
        mLocalMusicLv.setAdapter(mLocalMusicAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMainActivity.unbindMusicService();   // 解除绑定
    }

    // AdapterView.OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMainActivity.mMusicService.play(position); // 绑定成功之后才能调用Service的play方法，否则会报空指针异常
    }
}
