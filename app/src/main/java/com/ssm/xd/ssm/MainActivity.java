package com.ssm.xd.ssm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.json.JSONArray;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnClickListener{
    //声明三个布局文件
    private LinearLayout tabEquipments;
    private LinearLayout tabConsumables;
    private LinearLayout tabPieces;

    //声明三个Tab按钮
    private ImageButton buttonEquipments;
    private ImageButton buttonConsumables;
    private ImageButton buttonPieces;

    //声明重新排序和返回按键、切换到聊天
    private Button buttonBack;
    private Button buttonreOrder;
    private Button buttontoChat;

    private Intent intent;
    private ViewPager viewPager;
    //Fragment适配器
    private MyFragmentPagerAdapter fragAdapter;

    private List<Fragment> fragments;

    int user_id=0;//userMapper.getUser();
    //data
    private ArrayList<Goods> g_consumables=new ArrayList<>();
    private  ArrayList<Goods> g_equipments=new ArrayList<>();
    private  ArrayList<Goods> g_pieces=new ArrayList<>();

    private  ArrayList<Package> p_consumables=new ArrayList<>();
    private  ArrayList<Package> p_equipments=new ArrayList<>();
    private  ArrayList<Package> p_pieces=new ArrayList<>();

    boolean[] fragmentsUpdateFlag = {false, false, false};

    private Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    initFragments();
                    initAdapter();
                    break;
                case 2:
                    reFresh();
                default:
                    break;
            }
        }
    };


    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;


        MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position % fragments.size());
            Log.i("fragment replace", "getItem:position=" + position + ",fragment:"
                    + fragment.getClass().getName() + ",fragment.tag="
                    + fragment.getTag());
            return fragments.get(position % fragments.size());
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container,
                    position);
            //得到tag，这点很重要
            String fragmentTag = fragment.getTag();


            if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
            //如果这个fragment需要更新

                FragmentTransaction ft = fm.beginTransaction();
                //移除旧的fragment
                ft.remove(fragment);
                //换成新的fragment
                fragment = fragments.get(position % fragments.size());
                //添加新fragment时必须用前面获得的tag，这点很重要
                ft.add(container.getId(), fragment, fragmentTag);
                ft.attach(fragment);
                ft.commit();

                //复位更新标志
                fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]= false;
            }


            return fragment;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        intent=getIntent();

        //通过Handeler控制，先初始化数据再初始化fragment
        initData();
        initViews();
        initEvents();
        initButtons();

        selectTab(0);//默认打开页面
    }

    //JSONArray转ArrayList<Package>
    public static ArrayList<Package> JSONArraytoPackageList(JSONArray array){
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
    public static ArrayList<Goods> JSONArraytoGoodsList(JSONArray array){
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
                    //这个方法中包含对HttpResponse的初始化必须在线程中进行
                    JSONObject json=model.getIndexJSON(user_id,serverConfiguration.indexURL);

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

        buttonBack=(Button)findViewById(R.id.back_package);
        buttonreOrder=(Button)findViewById(R.id.reOrder);
        buttontoChat=(Button)findViewById(R.id.tochat);

        viewPager=(ViewPager)findViewById(R.id.package_viewpager);
    }

    //初始化Tab的点击事件
    private void initEvents(){
        tabConsumables.setOnClickListener(this);
        tabEquipments.setOnClickListener(this);
        tabPieces.setOnClickListener(this);

        buttonBack.setOnClickListener(this);
        buttonreOrder.setOnClickListener(this);
        buttontoChat.setOnClickListener(this);
    }

    //初始化按钮，将3个ImageButton置为灰色
    private void initButtons() {
        buttonConsumables.setAlpha((float)0.5);
        buttonEquipments.setAlpha((float)0.5);
        buttonPieces.setAlpha((float)0.5);
    }

    private void initFragments(){
        fragments=new ArrayList<>();

        fragments.add(FragConsumables.newInstance(p_consumables,g_consumables,user_id));
        fragments.add(FragEquipments.newInstance(p_equipments,g_equipments,user_id));
        fragments.add(FragPieces.newInstance(p_pieces,g_pieces,user_id));
    }

    //初始化适配器
    private void initAdapter(){
        fragAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
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
        //根据点击的Tab设置对应的ImageButton为半透明
        switch (i) {
            case 0:
                buttonConsumables.setAlpha((float)0.99);
                break;
            case 1:
                buttonEquipments.setAlpha((float)0.99);
                break;
            case 2:
                buttonPieces.setAlpha((float)0.99);
                break;
            default:
                break;
        }
        //设置当前点击的Tab所对应的页面
        viewPager.setCurrentItem(i);
    }

    private void back(){
        AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
                .setTitle("返回")
                .setMessage("当前已是最上层，是否退出应用？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();
    }

    //刷新fragments
    private void reFresh(){
        FragConsumables fragConsumables=(FragConsumables)fragments.get(0);
        fragConsumables.reset(p_consumables,g_consumables);

        FragEquipments fragEquipments=(FragEquipments)fragments.get(1);
        fragEquipments.reset(p_equipments,g_equipments);

        FragPieces fragPieces=(FragPieces) fragments.get(2);
        fragPieces.reset(p_pieces,g_pieces);

        fragAdapter.notifyDataSetChanged();
    }

    private void reOrder(){
        new Thread(){
            public void run(){
                //intent = getIntent();
                //userId = Integer.parseInt(intent.getStringExtra("user_id"));
                Message msg=new Message();
                PackageNetModel model=new PackageNetModel();
                try {
                    //这个方法中包含对HttpResponse的初始化必须在线程中进行
                    JSONObject json=model.reOrderJSON(user_id,serverConfiguration.reOrderURL);

                    p_consumables=(ArrayList<Package>) JSONArraytoPackageList(json.getJSONArray("p_consumables"));
                    p_equipments=(ArrayList<Package>) JSONArraytoPackageList(json.getJSONArray("p_equipments"));
                    p_pieces=(ArrayList<Package>) JSONArraytoPackageList(json.getJSONArray("p_pieces"));
                    g_consumables=(ArrayList<Goods>) JSONArraytoGoodsList(json.getJSONArray("g_consumables"));
                    g_equipments=(ArrayList<Goods>) JSONArraytoGoodsList(json.getJSONArray("g_equipments"));
                    g_pieces=(ArrayList<Goods>) JSONArraytoGoodsList(json.getJSONArray("g_pieces"));
                    System.out.println("web request done :"+p_consumables);

                    msg.what=2;
                    progressHandler.sendMessage(msg);

                }catch (Exception e){
                    Log.i("Exception",e.toString());
                }
            }
        }.start();

    }

    //切换到Chat
    private void toChat(){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,Main2Activity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }
    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tab_consumables:
                //先将3个tab置为半透明
                initButtons();
                selectTab(0);
                break;
            case R.id.tab_equipments:
                //先将3个tab置为半透明
                initButtons();
                selectTab(1);
                break;
            case R.id.tab_pieces:
                //先将3个tab置为半透明
                initButtons();
                selectTab(2);
                break;
            case R.id.back_package:
                back();
                break;
            case R.id.reOrder:
                reOrder();
                break;
            case R.id.tochat:
                toChat();
                break;
            default:
                break;
        }
    }
}
