package com.shellever.iceplayer;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

// All in right.
// android.support.v4.app.Fragment
//
// 优化：
// 1. 可以把扫描手机图片的操作放在启动闪屏页的过程中(3-5秒的时间足够加载完所有图片)
// 2. 启动后台服务

// 继承自BaseActivity，用于绑定服务
public class MainActivity extends BaseActivity {

    private PagerSlidingTabStrip mTabStrip;
    private ViewPager mViewPager;

    private LocalMusicFragment mLocalFragment;
    private NetworkMusicFragment mNetworkFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        String[] mTabTitles = {"本地音乐", "网络音乐"};
        List<Fragment> mFragmentList = new ArrayList<>();
        mLocalFragment = LocalMusicFragment.newInstance();
        mNetworkFragment = NetworkMusicFragment.newInstance();
        mFragmentList.add(mLocalFragment);
        mFragmentList.add(mNetworkFragment);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), mTabTitles, mFragmentList);
        mViewPager.setAdapter(adapter);

        mTabStrip.setViewPager(mViewPager);
    }

    // ========================================
    // 实现BaseActivity中的两个抽象方法
    @Override
    public void publish(int progress) {
        // 更新进度条
    }

    // 回调：Service -> MainActivity -> Fragment
    // 类似场景：自动播放时，需要Service来通知UI组件进行状态更新
    @Override
    public void change(int position) {
        // 切换播放位置
        int curItem = mViewPager.getCurrentItem();
        if (curItem == 0) {
            mLocalFragment.changeUIStatus(position);
        } else if (curItem == 1) {

        }
    }
    // ========================================

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 保存当前播放的一些状态值
        MainApplication app = (MainApplication) getApplication();
        SharedPreferences.Editor editor = app.sp.edit();
        // 保存当前正在播放的歌曲的位置
        editor.putInt("curPos", mMusicService.getCurrentPosition());
        // 保存播放模式
        editor.putInt("mPlayMode", mMusicService.getPlayMode());
        // 保存提交
        editor.apply();
    }
}
