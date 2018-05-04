package com.ssm.xd.ssm;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class FragConsumables extends Fragment implements OnItemClickListener {

    private GridConsumablesAdapter adapter;
    private GridView gridView;
    private View view;
    private ArrayList<Package> records=new ArrayList<>();
    private ArrayList<Goods> goods=new ArrayList<>();

    public static FragConsumables newInstance(ArrayList<Package> consumables, ArrayList<Goods> goods) {
        FragConsumables fragConsumables = new FragConsumables();
        Bundle bundle = new Bundle();
        bundle.putSerializable("records",consumables);
        bundle.putSerializable("goods",goods);
        fragConsumables.setArguments(bundle);
        return fragConsumables;
    }

    //从bundle中解出对象列表
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        this.records=(ArrayList<Package>) this.getArguments().getSerializable("records");
        this.goods=(ArrayList<Goods>) this.getArguments().getSerializable("goods");
    }

    // 消息提示框
    private void showConsumablesDialog(String title,String message) {
        AlertDialog alertDialog=new AlertDialog.Builder(this.getContext())
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("使用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do something
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onItemClick (AdapterView<?> parent, View view,int position, long id){
        //点击item触发
        System.out.println("clicked consumables");
        String message=new String();
        message=message+"使用时长："+goods.get(position).getGoodsAttr()+"\n";
        message=message+"详细介绍:"+goods.get(position).getGoodsIntro()+"\n";

        showConsumablesDialog(goods.get(position).getGoodsName(),message);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frag_consumables, container, false);
        gridView = (GridView) view.findViewById(R.id.grid_consumables);
        gridView.setAdapter(new GridConsumablesAdapter(getContext(),records,goods));
        gridView.setOnItemClickListener(this);
        return view;
    }
}