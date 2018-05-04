package com.ssm.xd.ssm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.android.gms.plus.PlusOneButton;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class FragPieces extends Fragment {
    private GridConsumablesAdapter adapter;
    private GridView gridView;
    private View view;
    private ArrayList<Package> records;
    private ArrayList<Goods> goods;

    public static FragPieces newInstance(ArrayList<Package> pieces, ArrayList<Goods> goods) {
        FragPieces fragPieces = new FragPieces();
        Bundle bundle = new Bundle();
        bundle.putSerializable("records",pieces);
        bundle.putSerializable("goods",goods);
        fragPieces.setArguments(bundle);
        return fragPieces;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        this.records=(ArrayList<Package>) this.getArguments().getSerializable("records");
        this.goods=(ArrayList<Goods>) this.getArguments().getSerializable("goods");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frag_pieces, container, false);
        gridView = (GridView) view.findViewById(R.id.grid_consumables);

//        records=new ArrayList<>();
//        goods=new ArrayList<>();
//
//        for(int i=0;i<40;i++){
//            Package aPackage=new Package();
//            aPackage.setGoodsId(1);
//            aPackage.setGoodsNum(10);
//            aPackage.setPackageId(1);
//            aPackage.setUserId(1);
//
//            Goods good=new Goods();
//            good.setGoodsAttr(1);
//            good.setGoodsIntro("good_intro");
//            good.setGoodsName("good_name");
//            good.setGoodsType("pieces");
//            good.setId(1);
//
//            records.add(aPackage);
//            goods.add(good);
//        }

        gridView.setAdapter(new GridConsumablesAdapter(getContext(),records,goods));
        return view;
    }
}
