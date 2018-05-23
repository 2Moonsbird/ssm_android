package com.ssm.xd.ssm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.json.JSONArray;

import android.os.Handler;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Main2Activity extends FragmentActivity implements OnClickListener{
    //声明2个布局文件
    private LinearLayout tabWorld;
    private LinearLayout tabFriend;

    //声明2个Tab按钮
    private ImageButton buttonWorld;
    private ImageButton buttonFriend;

    //fragments
    private Fragment fragWorld;
    private Fragment fragFriend;

    //声明重新排序和返回按键、切换到聊天
    private Button buttonBack;
    private Button buttontoPackage;

    private Intent intent=new Intent();

    int user_id=0;
    private ArrayList<Chat> world_records;
    private ArrayList<User> world_senders;
    private ArrayList<Chat> friend_records=new ArrayList<>();
    private ArrayList<User> friends=new ArrayList<>();


    public Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    selectTab(0);//默认打开页面
                    break;
                case 2:
                    initData();
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        intent=getIntent();

        //通过Handeler控制，先初始化数据再初始化fragment
        initData();

        initViews();
        initEvents();
        initButtons();

    }

    //JSONArray转ArrayList<Package>
    public static ArrayList<User> JSONArraytoUserList(JSONArray array){
        ArrayList<User> list = new ArrayList<>();
        JSONObject jsonObject;

        if (array==null) {
            return list;//nerver return null
        }
        for (int i=0;i<array.length();i++) {
            User newUser=new User();
            try{
                jsonObject=array.getJSONObject(i);

                newUser.setId((Integer)jsonObject.get("id"));
                newUser.setdTactics((Integer)jsonObject.get("dTactics"));
                newUser.setFriendsNum((Integer)jsonObject.get("friendsNum"));
                newUser.setMoney((Integer)jsonObject.get("money"));
                newUser.setoTactics((Integer)jsonObject.get("oTactics"));
                newUser.setPackageNum((Integer)jsonObject.get("packageNum"));
                newUser.setPower((Integer)jsonObject.get("power"));
                newUser.setSetting((Integer)jsonObject.get("setting"));
                newUser.setUserinfo((Integer)jsonObject.get("userinfo"));
                newUser.setUserName((String)jsonObject.get("userName"));

                list.add(newUser);
            }catch (Exception e){
                Log.i("JSONArraytoList ERROR",e.toString());
            }
        }
        return list;
    }

    //JSONArray转ArrayList<Chat>
    public static ArrayList<Chat> JSONArraytoChatList(JSONArray array){
        ArrayList<Chat> list = new ArrayList<>();
        JSONObject jsonObject;

        if (array==null) {
            return list;//nerver return null
        }
        for (int i=0;i<array.length();i++) {
            Chat newChat=new Chat();
            try{
                jsonObject=array.getJSONObject(i);

                newChat.setId((Integer) jsonObject.get("id"));
                newChat.setSendId((Integer)jsonObject.get("sendId"));
                newChat.setReceiveId((Integer)jsonObject.get("receiveId"));
                JSONObject time=jsonObject.getJSONObject("time");
                Long timestamp=time.getLong("time");
                newChat.setTime(new Timestamp(timestamp));
                newChat.setContent((String)jsonObject.get("content"));

                list.add(newChat);
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
                    JSONObject json=model.getChatJSON(user_id,serverConfiguration.getChatURL);

                    world_records=(ArrayList<Chat>) JSONArraytoChatList(json.getJSONArray("world_records"));
                    world_senders=(ArrayList<User>)JSONArraytoUserList(json.getJSONArray("world_senders"));
                    friends=(ArrayList<User>)JSONArraytoUserList(json.getJSONArray("friends"));
                    friend_records=(ArrayList<Chat>) JSONArraytoChatList(json.getJSONArray("friend_records"));

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
        tabWorld=(LinearLayout)findViewById(R.id.tab_world);
        tabFriend=(LinearLayout)findViewById(R.id.tab_friend);

        buttonWorld=(ImageButton)findViewById(R.id.tab_world_img);
        buttonFriend=(ImageButton)findViewById(R.id.tab_friend_img);

        buttontoPackage=(Button)findViewById(R.id.topackage);
        buttonBack=(Button)findViewById(R.id.back_chat);
    }

    //初始化点击事件
    private void initEvents(){
        tabWorld.setOnClickListener(this);
        tabFriend.setOnClickListener(this);

        buttontoPackage.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
    }

    //初始化按钮，将tab置为半透明
    private void initButtons() {
        tabWorld.setAlpha((float)0.5);
        tabFriend.setAlpha((float)0.5);
    }


    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            case 0:
                tabWorld.setAlpha((float)0.99);
                if (fragWorld == null) {
                    fragWorld = FragWorld.newInstance(world_senders,world_records,user_id);
                    transaction.add(R.id.id_content, fragWorld);
                } else {
                    transaction.show(fragWorld);
                }
                break;
            case 1:
                tabFriend.setAlpha((float)0.99);
                if (fragFriend == null) {
                    fragFriend = FragFriend.newInstance(friends,friend_records,user_id);
                    transaction.add(R.id.id_content, fragFriend);
                } else {
                    transaction.show(fragFriend);
                }
                break;
            default:
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fragWorld != null) {
            transaction.hide(fragWorld);
        }
        if (fragFriend != null) {
            transaction.hide(fragFriend);
        }
    }

    private void back(){
        AlertDialog alertDialog=new AlertDialog.Builder(Main2Activity.this)
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

    //切换到Chat
    private void toPackage(){
        Intent intent=new Intent();
        intent.setClass(Main2Activity.this,MainActivity.class);
        startActivity(intent);
        Main2Activity.this.finish();
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tab_world:
                //先将tab置为透明
                initButtons();
                selectTab(0);
                break;
            case R.id.tab_friend:
                //先将tab置为透明
                initButtons();
                selectTab(1);
                break;
            case R.id.back_chat:
                back();
                break;
            case R.id.topackage:
                toPackage();
                break;
            default:
                break;
        }
    }
}
