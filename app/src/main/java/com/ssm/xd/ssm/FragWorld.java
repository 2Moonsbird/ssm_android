package com.ssm.xd.ssm;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;



public class FragWorld extends Fragment implements OnItemClickListener,View.OnClickListener {

    private Button butSend;
    private EditText editContent;

    private int user_id;
    private ArrayList<Chat> records=new ArrayList<>();
    private String message;

    public static FragWorld newInstance(ArrayList<Chat> chat_records,int id) {
        FragWorld fragment = new FragWorld();
        Bundle args = new Bundle();
        args.putSerializable("records",chat_records);
        args.putInt("user_id",id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.records=(ArrayList<Chat>) this.getArguments().getSerializable("records");
        this.user_id=this.getArguments().getInt("user_id");
        initViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_world, container, false);
    }

    private void initViews(){
        butSend.findViewById(R.id.send_world);
        editContent.findViewById(R.id.world_message);
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

    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send_world:
                message=editContent.getText().toString();
                break;
            default:
                break;
        }
    }

}
