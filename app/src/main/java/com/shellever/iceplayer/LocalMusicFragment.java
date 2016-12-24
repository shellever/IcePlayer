package com.shellever.iceplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

/**
 * Author: Shellever
 * Date:   12/23/2016
 * Email:  shellever@163.com
 */

public class LocalMusicFragment extends Fragment {

    private Context context;

    private ListView mLocalMusicLv;
    private List<Mp3Info> mMp3InfoList;
    private LocalMusicAdapter mLocalMusicAdapter;


    public static LocalMusicFragment newInstance() {
        return new LocalMusicFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        mLocalMusicLv = (ListView) view.findViewById(R.id.lv_local_music);
        loadMp3InfoList();
        return view;
    }

    private void loadMp3InfoList() {
        mMp3InfoList = MediaUtils.getMp3InfoList(context);
        mLocalMusicAdapter = new LocalMusicAdapter(context, mMp3InfoList);
        mLocalMusicLv.setAdapter(mLocalMusicAdapter);
    }
}
