package com.shellever.iceplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Author: Shellever
 * Date:   12/24/2016
 * Email:  shellever@163.com
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabTitleArray;
    private List<Fragment> mFragmentList;


    public MyPagerAdapter(FragmentManager fm, String[] tabs, List<Fragment> fragmentList) {
        super(fm);
        mTabTitleArray = tabs;
        mFragmentList = fragmentList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitleArray[position];
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mTabTitleArray.length;
    }
}
