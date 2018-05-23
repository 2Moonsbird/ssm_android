package com.ssm.xd.ssm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FriendChatAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Chat> records;

    public FriendChatAdapter(Context context, ArrayList<Chat> records){
        super();
        this.records=records;
        this.context=context;
    }

    @Override
    public Object getItem(int position){
        return records.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getCount(){
        return records.size();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        FriendChatAdapter.ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_friend_record_item, parent, false);
            holder = new FriendChatAdapter.ViewHolder();
            holder.time=(TextView) convertView.findViewById(R.id.grid_friend_chat_time);
            holder.content = (TextView) convertView.findViewById(R.id.grid_friend_chat_content);
            convertView.setTag(holder);
        }
        else{
            holder = (FriendChatAdapter.ViewHolder) convertView.getTag();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        holder.time.setText(dateFormat.format(records.get(position).getTime()).toString());
        holder.content.setText(records.get(position).getContent());
        return convertView;
    }

    static class ViewHolder{
        TextView time;
        TextView content;
    }
}
