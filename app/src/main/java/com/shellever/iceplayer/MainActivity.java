package com.shellever.iceplayer;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

// All in right
// android.support.v4.app.Fragment

public class MainActivity extends AppCompatActivity {

    private PagerSlidingTabStrip mTabStrip;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        String[] mTabTitles = {"本地音乐", "网络音乐"};
        List<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(LocalMusicFragment.newInstance());
        mFragmentList.add(LocalMusicFragment.newInstance());
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), mTabTitles, mFragmentList);
        mViewPager.setAdapter(adapter);

        mTabStrip.setViewPager(mViewPager);
    }
}
