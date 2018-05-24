package com.ssm.xd.ssm;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.google.android.gms.common.util.Base64Utils;

import org.json.JSONObject;

import java.util.ArrayList;



public class FragWorld extends Fragment implements OnItemClickListener,View.OnClickListener {
    private WorldAdapter adapter;
    private GridView gridView;
    private View view;

    private Button butSend;
    private EditText editContent;

    private Handler activityHandler;

    private int user_id;
    private ArrayList<Chat> records=new ArrayList<>();
    private ArrayList<User> senders=new ArrayList<>();
    private String message;

    private Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    send();
                    break;
                case 2:
                    reFresh();
                default:
                    break;
            }
        }
    };
    public static FragWorld newInstance(ArrayList<User> world_senders,ArrayList<Chat> chat_records,int id) {
        FragWorld fragment = new FragWorld();
        Bundle args = new Bundle();
        args.putSerializable("senders",world_senders);
        args.putSerializable("records",chat_records);
        args.putInt("user_id",id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.records = (ArrayList<Chat>) this.getArguments().getSerializable("records");
        this.senders = (ArrayList<User>) this.getArguments().getSerializable("senders");
        this.user_id = this.getArguments().getInt("user_id");
        Main2Activity activity=(Main2Activity)getActivity();
        this.activityHandler=activity.progressHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_world, container, false);

        gridView=(GridView)view.findViewById(R.id.grid_world);
        gridView.setAdapter(adapter=new WorldAdapter(getContext(),senders,records,user_id));
        gridView.setOnItemClickListener(this);

        butSend=(Button)view.findViewById(R.id.send_world);
        editContent=(EditText)view.findViewById(R.id.world_message);

        initEvents();

        return view;
    }

    private void initEvents(){
        butSend.setOnClickListener(this);
    }

    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id){
        //点击item触发

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
                    JSONObject json=model.sendWorldJSON(user_id,message,serverConfiguration.sendWorldURL);
                    records.clear();
                    senders.clear();
                    records.addAll((ArrayList<Chat>) Main2Activity.JSONArraytoChatList(json.getJSONArray("world_records")));
                    senders.addAll((ArrayList<User>) Main2Activity.JSONArraytoUserList(json.getJSONArray("world_senders")));

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
                adapter.notifyDataSetChanged();
            }
        });
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send_world:
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
