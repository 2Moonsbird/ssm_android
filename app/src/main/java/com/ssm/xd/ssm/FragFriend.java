package com.ssm.xd.ssm;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ArrayRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import org.json.JSONObject;

import java.util.ArrayList;


public class FragFriend extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private FriendListAdapter listAdapter;
    private FriendChatAdapter chatAdapter;
    private GridView listGridView;
    private GridView chatGridView;
    private View view;

    private Button butSend;
    private EditText editContent;

    private Handler activityHandler;

    private int user_id;
    private ArrayList<Chat> records=new ArrayList<>();
    private ArrayList<User> friends=new ArrayList<>();
    private String message;

    private Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //发送信息
                    send();
                    break;
                case 2:
                    //刷新UI
                    reFresh();
                default:
                    break;
            }
        }
    };

    public static FragFriend newInstance(ArrayList<User> friends, ArrayList<Chat> records,int id) {
        FragFriend fragment = new FragFriend();
        Bundle args = new Bundle();
        args.putSerializable("friends",friends);
        args.putSerializable("records",records);
        args.putInt("user_id",id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.friends = (ArrayList<User>) getArguments().getSerializable("friends");
            this.records = (ArrayList<Chat>) getArguments().getSerializable("records");
            this.user_id = getArguments().getInt("user_id");
        }
        Main2Activity activity = (Main2Activity)getActivity();
        this.activityHandler=activity.progressHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friend, container, false);

        listGridView=(GridView)view.findViewById(R.id.grid_friend_list);
        listGridView.setAdapter(listAdapter=new FriendListAdapter(getContext(),friends));
        listGridView.setOnItemClickListener(this);

        chatGridView=(GridView)view.findViewById(R.id.grid_friend_chat);
        chatGridView.setAdapter(chatAdapter=new FriendChatAdapter(getContext(),records,user_id));
        chatGridView.setOnItemClickListener(this);

        butSend=(Button)view.findViewById(R.id.send_friend);
        editContent=(EditText)view.findViewById(R.id.friend_message);

        initEvents();

        return view;
    }

    private void initEvents(){
        butSend.setOnClickListener(this);
    }

    @Override
    public void onItemClick (AdapterView<?> parent, View view, final int position, long id){
        //点击item触发
        new Thread(){
            public void run(){
                Message msg = new Message();
                PackageNetModel model=new PackageNetModel();
                try {
                    //这个方法中包含对HttpResponse的初始化必须在线程中进行
                    JSONObject json=model.indexFriendJSON(user_id,position,serverConfiguration.indexFriendURL);
                    records.clear();
                    friends.clear();
                    records.addAll((ArrayList<Chat>) Main2Activity.JSONArraytoChatList(json.getJSONArray("friend_records")));
                    friends.addAll((ArrayList<User>) Main2Activity.JSONArraytoUserList(json.getJSONArray("friends")));

                }catch (Exception e){
                    Log.i("Exception",e.toString());
                }
                msg.what=2;
                progressHandler.sendMessage(msg);

            }
        }.start();
    }

    //发送按钮绑定的方法
    private void send(){
        if(message==null)
        {
            AlertDialog alertDialog=new AlertDialog.Builder(this.getContext())
                    .setTitle("warning")
                    .setMessage("发送内容不能为空")
                    .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
            return;
        }

        new Thread(){
            public void run(){
                Message msg = new Message();
                PackageNetModel model=new PackageNetModel();
                try {
                    //这个方法中包含对HttpResponse的初始化必须在线程中进行
                    JSONObject json=model.sendFriendJSON(user_id,message,serverConfiguration.sendFriendURL);
                    records.clear();
                    friends.clear();
                    records.addAll((ArrayList<Chat>) Main2Activity.JSONArraytoChatList(json.getJSONArray("friend_records")));
                    friends.addAll((ArrayList<User>) Main2Activity.JSONArraytoUserList(json.getJSONArray("friends")));

                }catch (Exception e){
                    Log.i("Exception",e.toString());
                }
                msg.what=2;
                progressHandler.sendMessage(msg);

            }
        }.start();
    }

    private void reFresh() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.notifyDataSetChanged();
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send_friend:
                message=editContent.getText().toString();
                Message msg=new Message();
                msg.what=1;
                progressHandler.sendMessage(msg);
                editContent.setText("");
                break;
            default:
                break;
        }
    }


}
