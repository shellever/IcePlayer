package com.shellever.iceplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

public class RecentMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mRecentMusicLv;

    private List<Mp3Info> mRecentMp3InfoList;  // recent list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_music);

        mRecentMusicLv = (ListView) findViewById(R.id.lv_recent_music);
        mRecentMusicLv.setOnItemClickListener(this);

        mRecentMp3InfoList = setupRecentMp3InfoList();
        Toast.makeText(this, "size: " + mRecentMp3InfoList.size(), Toast.LENGTH_SHORT).show();
        mRecentMusicLv.setAdapter(new LocalMusicAdapter(this, mRecentMp3InfoList));
    }

    private List<Mp3Info> setupRecentMp3InfoList() {
        MainApplication app = (MainApplication) getApplication();
        List<Mp3Info> result = null;
        try {
            Selector selector = Selector.from(Mp3Info.class)
                    .where("recentPlayTime", "!=", "0")         // 查询条件
                    .orderBy("recentPlayTime", true)            // 按recentPlayTime降序排序
                    .limit(BaseConstant.MAX_RECENT_PLAY_RECORD);// 查询结果记录数限制，默认为12
            result = app.mDbUtils.findAll(selector);   // 查找最近播放记录
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null) {
            return result;
        }
        // 防止在第一次启动时，由于没有收藏过音乐时报空指针异常
        // 故需要给Adapter默认实例化一个List
        return new ArrayList<>();
    }

    // ===================================================
    // 绑定后会回调onChange()方法，从而回调changeUIStatus()方法
    @Override
    protected void onResume() {
        super.onResume();
        bindMusicService();         // 绑定服务
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindMusicService();       // 解除绑定
    }
    // ===================================================

    // ========================================
    // Abstract method
    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }
    // ========================================

    // AdapterView.OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mMusicService.getCurPlayListFlag() != MyMusicService.FLAG_PLAY_LIST_RECENT) {
            mMusicService.setCurPlayListFlag(MyMusicService.FLAG_PLAY_LIST_RECENT); // 设置当前播放列表标识
            mMusicService.setMp3InfoList(mRecentMp3InfoList);   // 设置当前播放列表为最近播放的列表
        }
        mMusicService.play(position);
    }
}
