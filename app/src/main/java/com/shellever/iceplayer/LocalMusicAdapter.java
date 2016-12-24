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
 * Date:   12/24/2016
 * Email:  shellever@163.com
 */

public class LocalMusicAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Mp3Info> mMp3InfoList;


    public LocalMusicAdapter(Context context, List<Mp3Info> mMp3InfoList) {
        this.mMp3InfoList = mMp3InfoList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMp3InfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMp3InfoList.get(position);
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
            convertView = inflater.inflate(R.layout.item_list_song_info, null);
            holder.songName = (TextView) convertView.findViewById(R.id.tv_item_song_name);
            holder.singerName = (TextView) convertView.findViewById(R.id.tv_item_singer_name);
            holder.songDuration = (TextView) convertView.findViewById(R.id.tv_item_song_duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Mp3Info mp3Info = mMp3InfoList.get(position);
        holder.songName.setText(mp3Info.getTitle());
        holder.singerName.setText(mp3Info.getArtist());
        holder.songDuration.setText(MediaUtils.formatTime(mp3Info.getDuration()));

        return convertView;
    }

    private class ViewHolder {
        TextView songName;
        TextView singerName;
        TextView songDuration;
    }

    public void setMp3InfoList(List<Mp3Info> mMp3InfoList) {
        this.mMp3InfoList = mMp3InfoList;
    }
}
