package com.ssm.xd.ssm;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WorldAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Chat> records;
    private ArrayList<User> senders;
    private int user_id;

    public WorldAdapter(Context context, ArrayList<User> senders, ArrayList<Chat> records,int user_id){
        super();
        this.records=records;
        this.senders=senders;
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
        WorldAdapter.ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_world_item, parent, false);
            holder = new WorldAdapter.ViewHolder();

            holder.leftLayout=(LinearLayout) convertView.findViewById(R.id.world_item_left);
            holder.leftImg = (ImageView) convertView.findViewById(R.id.world_chat_img_left);
            holder.leftName=(TextView) convertView.findViewById(R.id.world_chat_sender_left);
            holder.leftContent = (TextView) convertView.findViewById(R.id.world_chat_content_left);

            holder.rightLayout=(LinearLayout) convertView.findViewById(R.id.world_item_right);
            holder.rightImg = (ImageView) convertView.findViewById(R.id.world_chat_img_right);
            holder.rightName=(TextView) convertView.findViewById(R.id.world_chat_sender_right);
            holder.rightContent = (TextView) convertView.findViewById(R.id.world_chat_content_right);

            convertView.setTag(holder);
        }
        else{
            holder = (WorldAdapter.ViewHolder) convertView.getTag();
        }

        if(records.get(position).getSendId()!=user_id)
        {
            //接受信息,显示左侧视图
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftImg.setBackgroundResource(R.mipmap.ironman);
            holder.leftName.setText(senders.get(position).getUserName());
            holder.leftContent.setText(records.get(position).getContent());
        }
        else {
            //发送的信息,显示左侧视图
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightImg.setBackgroundResource(R.mipmap.ironman);
            holder.rightName.setText(senders.get(position).getUserName());
            holder.rightContent.setText(records.get(position).getContent());
        }
        return convertView;
    }

    static class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;

        ImageView leftImg;
        ImageView rightImg;

        TextView leftName;
        TextView rightName;

        TextView leftContent;
        TextView rightContent;
    }

}
