package com.ssm.xd.ssm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> friends;

    public FriendListAdapter(Context context, ArrayList<User> friends){
        super();
        this.friends=friends;
        this.context=context;
    }

    @Override
    public Object getItem(int position){
        return friends.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getCount(){
        return friends.size();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        FriendListAdapter.ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_frind_list_item, parent, false);
            holder = new FriendListAdapter.ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.grid_friend_list_img);
            holder.name=(TextView) convertView.findViewById(R.id.grid_friend_list_name);
            holder.power = (TextView) convertView.findViewById(R.id.grid_friend_list_power);
            convertView.setTag(holder);
        }
        else{
            holder = (FriendListAdapter.ViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource(R.mipmap.thunder);
        holder.name.setText(friends.get(position).getUserName());
        holder.power.setText("战力"+String.valueOf(friends.get(position).getPower()));
        return convertView;
    }

    static class ViewHolder{
        ImageView img;
        TextView name;
        TextView power;
    }
}
