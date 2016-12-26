package com.shellever.iceplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Author: Shellever
 * Date:   12/27/2016
 * Email:  shellever@163.com
 */

public class NetworkMusicAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SearchResult> mSearchResultList;

    public NetworkMusicAdapter(Context context, List<SearchResult> list) {
        mSearchResultList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSearchResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_list_search_info, null);
            holder.songName = (TextView) convertView.findViewById(R.id.tv_item_search_song_name);
            holder.singerName = (TextView) convertView.findViewById(R.id.tv_item_search_singer_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchResult searchResult = mSearchResultList.get(position);
        holder.songName.setText(searchResult.getSongName());
        holder.singerName.setText(searchResult.getSingerName());

        return convertView;
    }

    private class ViewHolder {
        TextView songName;
        TextView singerName;
    }
}
