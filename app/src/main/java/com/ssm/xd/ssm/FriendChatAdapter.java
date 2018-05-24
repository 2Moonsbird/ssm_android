package com.ssm.xd.ssm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FriendChatAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Chat> records;
    private int user_id;

    public FriendChatAdapter(Context context, ArrayList<Chat> records,int user_id){
        super();
        this.records=records;
        this.context=context;
        this.user_id=user_id;
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

            holder.leftLayout=(LinearLayout)convertView.findViewById(R.id.friend_record_item_left);
            holder.leftTime=(TextView) convertView.findViewById(R.id.grid_friend_chat_time_left);
            holder.leftContent = (TextView) convertView.findViewById(R.id.grid_friend_chat_content_left);

            holder.rightLayout=(LinearLayout)convertView.findViewById(R.id.friend_record_item_right);
            holder.rightTime=(TextView) convertView.findViewById(R.id.grid_friend_chat_time_right);
            holder.rightContent = (TextView) convertView.findViewById(R.id.grid_friend_chat_content_right);

            convertView.setTag(holder);
        }
        else{
            holder = (FriendChatAdapter.ViewHolder) convertView.getTag();
        }
        if(records.get(position).getSendId()!=user_id)
        {
            //接受消息
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            holder.leftTime.setText(dateFormat.format(records.get(position).getTime()).toString());
            holder.leftContent.setText(records.get(position).getContent());

        }
        else{
            //发送消息
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            holder.rightTime.setText(dateFormat.format(records.get(position).getTime()).toString());
            holder.rightContent.setText(records.get(position).getContent());
        }


        return convertView;
    }

    static class ViewHolder{

        LinearLayout leftLayout;
        LinearLayout rightLayout;

        TextView leftTime;
        TextView rightTime;

        TextView leftContent;
        TextView rightContent;

    }
}
