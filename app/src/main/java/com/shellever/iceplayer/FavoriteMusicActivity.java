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


public class FavoriteMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mFavoriteMusicLv;

    private List<Mp3Info> mFavMp3InfoList;  // favorite list


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_music);

        mFavoriteMusicLv = (ListView) findViewById(R.id.lv_favorite_music);
        mFavoriteMusicLv.setOnItemClickListener(this);

        mFavMp3InfoList = setupFavMp3InfoList();
        Toast.makeText(this, "size: " + mFavMp3InfoList.size(), Toast.LENGTH_SHORT).show();
        mFavoriteMusicLv.setAdapter(new LocalMusicAdapter(this, mFavMp3InfoList));
    }

    // 若查找比较耗时，则应该使用线程来异步加载
    private List<Mp3Info> setupFavMp3InfoList() {
        MainApplication app = (MainApplication) getApplication();
        List<Mp3Info> result = null;
        try {
            result = app.mDbUtils.findAll(Selector.from(Mp3Info.class).where("isFavorite", "=", "1"));   // 查找所有收藏过的音乐
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
        if (mMusicService.getCurPlayListFlag() != mMusicService.FLAG_PLAY_LIST_FAVORITE) {
            mMusicService.setCurPlayListFlag(MyMusicService.FLAG_PLAY_LIST_FAVORITE);   // 设置当前播放列表标识
            mMusicService.setMp3InfoList(mFavMp3InfoList);  // 设置当前播放列表为收藏的列表
        }
        mMusicService.play(position);

        storeToRecentRecord();      // 保存当前歌曲到最近播放中(只有手动点击时才会保存到最近播放记录中)
    }

    private void storeToRecentRecord() {
        MainApplication app = (MainApplication) getApplication();
        Mp3Info current = mMusicService.getCurMp3Info();
        try {
            Mp3Info result = app.mDbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", current.getMp3InfoId()));
            if (result == null) {       // 表中未有此记录
                current.setRecentPlayTime(System.currentTimeMillis());  // 设置系统当前时间
                app.mDbUtils.save(current); // 保存记录
            } else {
                result.setRecentPlayTime(System.currentTimeMillis());   // 更新时间
                app.mDbUtils.update(result, "recentPlayTime");  // 更新记录
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
