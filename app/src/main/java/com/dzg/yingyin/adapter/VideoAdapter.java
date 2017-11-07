package com.dzg.yingyin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.yingyin.R;
import com.dzg.yingyin.bean.VideoBean;
import com.dzg.yingyin.ui.activity.MediaPlayerActivity;

import java.util.ArrayList;

public class VideoAdapter extends BaseAdapter {
    private ArrayList<VideoBean> mList;
    private LayoutInflater inflater;
    private Context mContext;

    public VideoAdapter(Context context, ArrayList<VideoBean> list) {
        this.mList = list;
        mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void onDateChange(ArrayList<VideoBean> list) {
        this.mList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final VideoBean bean = mList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_videoitem, null);
            holder.thumbnail_iv = convertView.findViewById(R.id.videoitem_thumbnail);
            holder.title_tv = convertView.findViewById(R.id.videoitem_title);
            holder.time_tv = convertView.findViewById(R.id.videoitem_time);
            holder.size_tv = convertView.findViewById(R.id.videoitem_size);
            holder.relativeLayout=convertView.findViewById(R.id.videoitem);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//		holder.thumbnail_iv.setText(entity.getName());
        Glide.with(mContext).load(bean.getUrl()).into(holder.thumbnail_iv);
        holder.title_tv.setText(bean.getTitle());
        holder.time_tv.setText(bean.getTime());
        holder.size_tv.setText(bean.getSize());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("filepath",bean.getFilePath());
                intent.putExtra("videoname",bean.getTitle());
                intent.setClass(mContext, MediaPlayerActivity.class);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView thumbnail_iv;
        TextView title_tv;
        TextView time_tv;
        TextView size_tv;
    }
}
