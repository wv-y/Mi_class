package com.example.mi_class.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.R;
import com.example.mi_class.Start_activity;
import com.example.mi_class.adapter.ChatAdapter;
import com.example.mi_class.adapter.MessageAdapter;
import com.example.mi_class.domain.Message;
import com.example.mi_class.domain.message_temp;
import com.example.mi_class.fragment.MessageFragment;
import com.example.mi_class.tool.Base64Utils;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.MyWebSocket;

import org.java_websocket.enums.ReadyState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private List<Message> chatList;
    private ChatAdapter chatAdapter;
    public static Handler handler = null;
    public static List<message_temp> mess1;
    HashMap<String,String> p ;
    private final static int updateMessage = 100;
    RecyclerView recyclerView;
    EditText editText;
    Button button;
    String nickName;
    int pic_id;
    public static String phone = "";
    public static String name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        // 状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Intent intent = getIntent();
        name = intent.getStringExtra("chat_name");
        nickName = intent.getStringExtra("nick_name");
        pic_id = intent.getIntExtra("pic_id",0);
        System.out.println("nick:"+nickName);
        if(nickName != null)
            setTitle(nickName);
        recyclerView = findViewById(R.id.chat_recycler_list);
        editText = findViewById(R.id.chat_edit_msg);
        button = findViewById(R.id.chat_send_button);
        System.out.println("chatA onCreate");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                switch (msg.what)
                {
                    case updateMessage:
                        updateMessage();
                        break;
                }
            }
        };
        SharedPreferences preferences = getSharedPreferences("user_login_info",MODE_PRIVATE);
        phone = preferences.getString("phone","");
        chatList = new ArrayList<Message>();
        if(!phone.equals("")){
            SharedPreferences pf = getSharedPreferences(phone+"_ms",MODE_PRIVATE);
            String data = pf.getString("message_list","");
            SharedPreferences sp = getSharedPreferences("my_info",MODE_PRIVATE);
            int my_pic_id = sp.getInt("pic_id",0);
            if(!data.equals("")){
                System.out.println("chatA 1");
                localToRead(new Date().getTime(),name,phone,data);
                System.out.println("chatA 3");
                mess1 = to_ms_data(data);
                for(int i = 0 ; i < mess1.size() ; i++)
                {
                    if(name.equals(mess1.get(i).getFrom_user_id()) || name.equals(mess1.get(i).getTo_user_id()))
                    {
                        Message msg1 = new Message();
                        //发信人是我
                        if(mess1.get(i).getFrom_user_id().equals(phone)){
                            msg1.setType(Message.TYPE_SEND);
                            msg1.setPic_id(my_pic_id);
                        }
                        //收件人是我
                        if(mess1.get(i).getTo_user_id().equals(phone)){
                            msg1.setType(Message.TYPE_RECEIVE);
                            msg1.setPic_id(pic_id);
                        }
                        msg1.setTime(mess1.get(i).getTime());
                        msg1.setLast_message(mess1.get(i).getContent());

                        chatList.add(msg1);
                    }

                }
            }
        }else{
            Toast.makeText(this,"账号异常",Toast.LENGTH_LONG).show();
        }





        chatAdapter = new ChatAdapter(this,chatList);
        recyclerView.setAdapter(chatAdapter);
        //底部
        recyclerView.scrollToPosition(chatList.size()-1);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //发送消息
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().equals(""))
                {
                    if(MyWebSocket.myWebSocket.getReadyState().equals(ReadyState.OPEN))
                    {
                        Log.i("s",MyWebSocket.myWebSocket.getReadyState().toString());
                        String content = editText.getText().toString();
                        content = Base64Utils.encodeToString(content.getBytes());
                        long a = new Date().getTime();
                        HashMap<String,String> p = new HashMap<>();
                        p.put("to_user_id",name);
                        p.put("from_user_id",phone);
                        p.put("content",content);
                        p.put("time",String.valueOf(a));
                        p.put("state","0");
                        message_temp t = new message_temp();
                        t.setTo_user_id(name);
                        t.setFrom_user_id(phone);
                        t.setTime(a);
                        t.setState(0);
                        t.setContent(content);
                        System.out.println(toJson(p));
                        MyWebSocket.myWebSocket.send(toJson(p));
                        editText.setText("");
                        mess1.add(t);
                        updateMessage();
                        MessageFragment.temp_ms_data = mess1;
                        android.os.Message m = new android.os.Message();
                        m.what = 102;
                        MessageFragment.handler.sendMessage(m);
                    }else{
                        Toast.makeText(ChatActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ChatActivity.this,"不能发送空消息",Toast.LENGTH_LONG).show();
                }


            }
        });
//        Start_activity.myWebSocketClient.send("1");
    }
    public void localToRead(long time,String from,String to,String res)
    {
        p = new HashMap<>();
        p.put("to_user_id",to);
        p.put("from_user_id",from);
        p.put("time",String.valueOf(time));
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.sendPostMessage(p,"utf-8","toRead");
            }
        }).start();
        System.out.println("chatA 2");
        if(!res.equals(""))
        {
            System.out.println("chatA 2-1");
            List<message_temp> t = to_ms_data(res);
            System.out.println("要检查的长度："+t.size());
            boolean flag = false;
            for(int i = 0 ; i < t.size() ; i++)
            {
                message_temp t1 = t.get(i);
                if(t1.getFrom_user_id().equals(from) && t1.getTo_user_id().equals(to) && t1.getTime() <= time && t1.getState()==0)
                {
                    flag = true;
                    t1.setState(1);
                }
            }
            if(flag)
            {
                System.out.println("有变");
                MessageFragment.temp_ms_data = t;
                android.os.Message sm = new android.os.Message();
                sm.what = MessageFragment.refresh;
                MessageFragment.handler.sendMessage(sm);
            }

        }

    }
    public void updateMessage()
    {
        chatList = new ArrayList<Message>();
        Collections.sort(mess1, new Comparator<message_temp>() {
            public int compare(message_temp arg0, message_temp arg1) {
                if(arg0.getTime()==arg1.getTime()) return 0;
                return arg0.getTime() > arg1.getTime() ? 1 : -1;
            }
        });
        SharedPreferences sp = getSharedPreferences("my_info",MODE_PRIVATE);
        int my_pic_id = sp.getInt("pic_id",0);
        for(int i = 0 ; i < mess1.size() ; i++)
        {
            if(name.equals(mess1.get(i).getFrom_user_id()) || name.equals(mess1.get(i).getTo_user_id()))
            {
                Message msg1 = new Message();
                //发信人是我
                if(mess1.get(i).getFrom_user_id().equals(phone)){
                    msg1.setType(Message.TYPE_SEND);
                    msg1.setPic_id(my_pic_id);
                }
                //收件人是我
                if(mess1.get(i).getTo_user_id().equals(phone)){
                    msg1.setType(Message.TYPE_RECEIVE);
                    msg1.setPic_id(pic_id);
                }
                msg1.setTime(mess1.get(i).getTime());
                msg1.setLast_message(mess1.get(i).getContent());
                chatList.add(msg1);
            }

        }
        chatAdapter = new ChatAdapter(this,chatList);
        recyclerView.setAdapter(chatAdapter);
        //底部
        recyclerView.scrollToPosition(chatList.size()-1);
    }
    public String toJson(HashMap<String,String> hash){
        String res = "{";
        Iterator<Map.Entry<String,String>> iterator = hash.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> n = iterator.next();
            res += "\"" + n.getKey()+"\":"+"\""+n.getValue()+"\",";
        }
        res = res.substring(0,res.length()-1);
        res +="}";
        return res;
    }


    public List<message_temp> to_ms_data(String res){
        List<message_temp> result = new ArrayList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(res);
            for(int i = 0 ; i < array.length(); i ++)
            {
                JSONObject j = (JSONObject) array.get(i);
                message_temp temp = new message_temp();
                temp.setContent((String)j.get("content"));
                temp.setFrom_user_id((String)j.get("from_user_id"));
                temp.setState((int)j.get("state"));
                temp.setTime((long)j.get("time"));
                temp.setTo_user_id((String)j.get("to_user_id"));
                result.add(temp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

    }

    @Override
    public void finish() {
        super.finish();
        System.out.println("finish");
        name = "";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
