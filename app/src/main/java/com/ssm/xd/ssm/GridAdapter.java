package com.ssm.xd.ssm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Package> records;
    private ArrayList<Goods> goods;

    public GridAdapter(Context context, ArrayList<Package> records, ArrayList<Goods> goods){
        super();
        this.records=records;
        this.goods=goods;
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
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_package_item, parent, false);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.grid_package_item_img);
            holder.txt_name=(TextView) convertView.findViewById(R.id.grid_item_package_name);
            holder.txt_num = (TextView) convertView.findViewById(R.id.grid_package_item_num);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource(R.mipmap.shield);
        holder.txt_num.setText(String.valueOf(records.get(position).getGoodsNum()));
        holder.txt_name.setText(goods.get(position).getGoodsName());
        return convertView;
    }

    static class ViewHolder{
        ImageView img;
        TextView txt_num;
        TextView txt_name;
    }

}
