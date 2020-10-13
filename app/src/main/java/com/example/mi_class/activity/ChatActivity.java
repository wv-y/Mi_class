package com.example.mi_class.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private final static int updateMessage = 100;
    RecyclerView recyclerView;
    EditText editText;
    Button button;
    String phone;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        // 状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        recyclerView = findViewById(R.id.chat_recycler_list);
        editText = findViewById(R.id.chat_edit_msg);
        button = findViewById(R.id.chat_send_button);

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
            if(!data.equals("")){
                mess1 = to_ms_data(data);
                for(int i = 0 ; i < mess1.size() ; i++)
                {
                    Message msg1 = new Message();
                    //发信人是我
                    if(mess1.get(i).getFrom_user_id().equals(phone)){
                        msg1.setType(Message.TYPE_SEND);
                    }
                    //收件人是我
                    if(mess1.get(i).getTo_user_id().equals(phone)){
                        msg1.setType(Message.TYPE_RECEIVE);
                    }
                    msg1.setTime(mess1.get(i).getTime());
                    msg1.setLast_message(mess1.get(i).getContent());
                    chatList.add(msg1);
                }
            }
        }else{
            Toast.makeText(this,"账号异常",Toast.LENGTH_LONG).show();
        }





        chatAdapter = new ChatAdapter(this,chatList);
        recyclerView.setAdapter(chatAdapter);

        Intent intent = getIntent();
        name = intent.getStringExtra("chat_name");
        setTitle(name);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //发送消息
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().equals(""))
                {
                    if(MyWebSocket.myWebSocket.getReadyState().equals(ReadyState.OPEN))
                    {
                        Log.i("s",MyWebSocket.myWebSocket.getReadyState().toString());
                        long a = new Date().getTime();
                        HashMap<String,String> p = new HashMap<>();
                        p.put("to_user_id",name);
                        p.put("from_user_id",phone);
                        p.put("content",editText.getText().toString());
                        p.put("time",String.valueOf(a));
                        p.put("state","0");
                        message_temp t = new message_temp();
                        t.setTo_user_id(name);
                        t.setFrom_user_id(phone);
                        t.setTime(a);
                        t.setState(0);
                        t.setContent(editText.getText().toString());
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
    public void updateMessage()
    {
        chatList = new ArrayList<Message>();
        Collections.sort(mess1, new Comparator<message_temp>() {
            public int compare(message_temp arg0, message_temp arg1) {
                if(arg0.getTime()==arg1.getTime()) return 0;
                return arg0.getTime() > arg1.getTime() ? 1 : -1;
            }
        });
        for(int i = 0 ; i < mess1.size() ; i++)
        {
            Message msg1 = new Message();
            //发信人是我
            if(mess1.get(i).getFrom_user_id().equals(phone)){
                msg1.setType(Message.TYPE_SEND);
            }
            //收件人是我
            if(mess1.get(i).getTo_user_id().equals(phone)){
                msg1.setType(Message.TYPE_RECEIVE);
            }
            msg1.setTime(mess1.get(i).getTime());
            msg1.setLast_message(mess1.get(i).getContent());
            chatList.add(msg1);
        }
        chatAdapter = new ChatAdapter(this,chatList);
        recyclerView.setAdapter(chatAdapter);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
