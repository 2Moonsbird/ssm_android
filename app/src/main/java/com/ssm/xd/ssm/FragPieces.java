package com.ssm.xd.ssm;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import org.json.JSONObject;

import java.util.ArrayList;

public class FragPieces extends Fragment  implements OnItemClickListener {
    private GridAdapter adapter;
    private GridView gridView;
    private View view;
    int user_id;
    int position;
    private Handler progressHandler = null;
    private ArrayList<Package> records = new ArrayList<>();
    private ArrayList<Goods> goods = new ArrayList<>();

    public static FragPieces newInstance(ArrayList<Package> pieces, ArrayList<Goods> goods, int user_id) {
        FragPieces fragPieces = new FragPieces();
        Bundle bundle = new Bundle();
        bundle.putSerializable("records", pieces);
        bundle.putSerializable("goods", goods);
        bundle.putInt("user_id", user_id);
        fragPieces.setArguments(bundle);
        return fragPieces;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.records = (ArrayList<Package>) this.getArguments().getSerializable("records");
        this.goods = (ArrayList<Goods>) this.getArguments().getSerializable("goods");
        this.user_id = this.getArguments().getInt("user_id");

        progressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        reFresh();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    // 消息提示框
    private void showPiecesDialog(String title, String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext())
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("合成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (records.get(position).getGoodsNum() < goods.get(position).getGoodsAttr()) {
                            lackWarning();
                            dialog.dismiss();
                            return;

                        }
                        new Thread() {
                            public void run() {
                                //do something
                                try {
                                    PackageNetModel model = new PackageNetModel();

                                    //这个方法中包含对HttpResponse的初始化必须在线程中进行
                                    JSONObject json = model.pieceTogetherJSON(user_id, position, serverConfiguration.pieceTogetherURL);

                                    //避免挪动对象的地址，使notifyDataSetChanged失效
                                    records.clear();
                                    goods.clear();
                                    records.addAll ((ArrayList<Package>) MainActivity.JSONArraytoPackageList(json.getJSONArray("p_pieces")));
                                    goods.addAll((ArrayList<Goods>) MainActivity.JSONArraytoGoodsList(json.getJSONArray("g_pieces")));

                                    Message msg = new Message();
                                    msg.what = 1;
                                    progressHandler.handleMessage(msg);

                                } catch (Exception e) {
                                    Log.i("pieceTogether ERROR", e.toString());
                                }
                            }
                        }.start();

                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击item触发
        String message = new String();
        message = message + "合成所需数量：" + goods.get(position).getGoodsAttr() + "\n";
        message = message + "详细介绍:" + goods.get(position).getGoodsIntro() + "\n";
        this.position = position;
        showPiecesDialog(goods.get(position).getGoodsName(), message);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_package, container, false);
        gridView = (GridView) view.findViewById(R.id.grid_package);
        gridView.setAdapter(adapter = new GridAdapter(getContext(), records, goods));
        gridView.setOnItemClickListener(this);
        return view;
    }

    private void lackWarning() {
        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext())
                .setTitle("合成失败")
                .setMessage("碎片数量不足")
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void reFresh() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void reset(ArrayList<Package> pieces, ArrayList<Goods> goods) {
        this.records = pieces;
        this.goods = goods;
        reFresh();
    }
}