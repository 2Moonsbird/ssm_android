package com.ssm.xd.ssm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WorldAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Chat> records;
    private ArrayList<User> senders;

    public WorldAdapter(Context context, ArrayList<User> senders, ArrayList<Chat> records){
        super();
        this.records=records;
        this.senders=senders;
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
        WorldAdapter.ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_world_item, parent, false);
            holder = new WorldAdapter.ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.world_chat_img);
            holder.name=(TextView) convertView.findViewById(R.id.world_chat_sender);
            holder.content = (TextView) convertView.findViewById(R.id.world_chat_content);
            convertView.setTag(holder);
        }
        else{
            holder = (WorldAdapter.ViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource(R.mipmap.ironman);
        holder.name.setText(senders.get(position).getUserName());
        holder.content.setText(records.get(position).getContent());
        return convertView;
    }

    static class ViewHolder{
        ImageView img;
        TextView name;
        TextView content;
    }

}
