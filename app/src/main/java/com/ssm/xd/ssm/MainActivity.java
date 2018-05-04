package com.ssm.xd.ssm;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.json.JSONArray;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnClickListener {
    //声明三个布局文件
    private LinearLayout tabEquipments;
    private LinearLayout tabConsumables;
    private LinearLayout tabPieces;

    //声明三个Tab按钮
    private ImageButton buttonEquipments;
    private ImageButton buttonConsumables;
    private ImageButton buttonPieces;

    private Intent intent;
    private ViewPager viewPager;
    //Fragment适配器
    private FragmentPagerAdapter fragAdapter;

    private List<Fragment> fragments;


    //data
    private ArrayList<Goods> g_consumables=new ArrayList<>();
    private  ArrayList<Goods> g_pieces=new ArrayList<>();
    private  ArrayList<Goods> g_equipments=new ArrayList<>();

    private  ArrayList<Package> p_consumables=new ArrayList<>();
    private  ArrayList<Package> p_pieces=new ArrayList<>();
    private  ArrayList<Package> p_equipments=new ArrayList<>();

    private Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
                initViews();
                initEvents();
                initFragments();
                initAdapter();
                selectTab(0);//默认打开页面
            }
//            else if(msg.what == 2){
//
//            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        intent=getIntent();

        //通过Handeler控制，先初始化数据再初始化界面
        initData();
    }

    //JSONArray转ArrayList<Package>
    private static ArrayList<Package> JSONArraytoPackageList(JSONArray array){
        ArrayList<Package> list = new ArrayList<>();
        JSONObject jsonObject;

        if (array==null) {
            return list;//nerver return null
        }
        for (int i=0;i<array.length();i++) {
            Package newPackage=new Package();
            try{
                jsonObject=array.getJSONObject(i);

                newPackage.setGoodsId((Integer) jsonObject.get("goodsId"));
                newPackage.setPackageId((Integer)jsonObject.get("packageId"));
                newPackage.setGoodsNum((Integer)jsonObject.get("goodsNum"));
                newPackage.setUserId((Integer)jsonObject.get("userId"));

                list.add(newPackage);
            }catch (Exception e){
                Log.i("JSONArraytoList ERROR",e.toString());
            }
        }
        return list;
    }

    //JSONArray转ArrayList<Goods>
    private static ArrayList<Goods> JSONArraytoGoodsList(JSONArray array){
        ArrayList<Goods> list = new ArrayList<>();
        JSONObject jsonObject;

        if (array==null) {
            return list;//nerver return null
        }
        for (int i=0;i<array.length();i++) {
            Goods newGoods=new Goods();
            try{
                jsonObject=array.getJSONObject(i);

                newGoods.setId((Integer) jsonObject.get("id"));
                newGoods.setGoodsAttr((Integer) jsonObject.get("goodsAttr"));
                newGoods.setGoodsIntro((String) jsonObject.get("goodsIntro"));
                newGoods.setGoodsName((String) jsonObject.get("goodsName"));
                newGoods.setGoodsType((String) jsonObject.get("goodsType"));

                list.add(newGoods);
            }catch (Exception e){
                Log.i("JSONArraytoList ERROR",e.toString());
            }
        }
        return list;
    }

    //初始化数据列表
    private void initData(){
        new Thread(){
            public void run(){
                //intent = getIntent();
                //userId = Integer.parseInt(intent.getStringExtra("user_id"));
                Message msg=new Message();
                PackageNetModel model=new PackageNetModel();
                try {
                    JSONObject json=model.getIndexJSON(1,serverConfiguration.indexURL);

                    p_consumables=(ArrayList<Package>) JSONArraytoPackageList(json.getJSONArray("p_consumables"));
                    p_equipments=(ArrayList<Package>) JSONArraytoPackageList(json.getJSONArray("p_equipments"));
                    p_pieces=(ArrayList<Package>) JSONArraytoPackageList(json.getJSONArray("p_pieces"));
                    g_consumables=(ArrayList<Goods>) JSONArraytoGoodsList(json.getJSONArray("g_consumables"));
                    g_equipments=(ArrayList<Goods>) JSONArraytoGoodsList(json.getJSONArray("g_equipments"));
                    g_pieces=(ArrayList<Goods>) JSONArraytoGoodsList(json.getJSONArray("g_pieces"));

                }catch (Exception e){
                    Log.i("Exception",e.toString());
                }

                msg.what=1;
                progressHandler.sendMessage(msg);
            }
        }.start();
    }

    //初始化布局文件
    private void initViews(){
        tabConsumables=(LinearLayout)findViewById(R.id.tab_consumables);
        tabEquipments=(LinearLayout)findViewById(R.id.tab_equipments);
        tabPieces=(LinearLayout)findViewById(R.id.tab_pieces);

        buttonConsumables=(ImageButton)findViewById(R.id.tab_consumables_but);
        buttonEquipments=(ImageButton)findViewById(R.id.tab_equipments_but);
        buttonPieces=(ImageButton)findViewById(R.id.tab_pieces_but);

        viewPager=(ViewPager)findViewById(R.id.package_viewpager);
    }

    //初始化Tab的点击事件
    private void initEvents(){
        tabConsumables.setOnClickListener(this);
        tabEquipments.setOnClickListener(this);
        tabPieces.setOnClickListener(this);
    }

    //初始化按钮，将3个ImageButton置为灰色
    private void initButtons() {
        buttonConsumables.setImageResource(R.mipmap.ic_launcher);
        buttonEquipments.setImageResource(R.mipmap.ic_launcher);
        buttonPieces.setImageResource(R.mipmap.ic_launcher);
    }

    private void initFragments(){
        fragments=new ArrayList<>();

        fragments.add(FragConsumables.newInstance(p_consumables,g_consumables));
        fragments.add(FragEquipments.newInstance(p_equipments,g_equipments));
        fragments.add(FragEquipments.newInstance(p_pieces,g_pieces));
    }

    //初始化适配器
    private void initAdapter(){
        fragAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                //从集合中获取对应位置的Fragment
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                //获取集合中Fragment的总数
                return fragments.size();

            }

        };
        //设置ViewPager的适配器
        viewPager.setAdapter(fragAdapter);
        //设置ViewPager的切换监听
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置position对应的集合中的Fragment
                viewPager.setCurrentItem(position);
                initButtons();
                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void selectTab(int i) {
        //根据点击的Tab设置对应的ImageButton为绿色
        switch (i) {
            case 0:
                buttonConsumables.setImageResource(R.mipmap.currten);
                break;
            case 1:
                buttonEquipments.setImageResource(R.mipmap.currten);
                break;
            case 2:
                buttonPieces.setImageResource(R.mipmap.currten);
                break;
            default:
                break;
        }
        //设置当前点击的Tab所对应的页面
        viewPager.setCurrentItem(i);
    }

    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        //先将3个ImageButton置为灰色
        initButtons();
        switch (v.getId())
        {
            case R.id.tab_consumables:
                selectTab(0);
                break;
            case R.id.tab_equipments:
                selectTab(1);
                break;
            case R.id.tab_pieces:
                selectTab(2);
                break;
            default:
                break;
        }
    }
}