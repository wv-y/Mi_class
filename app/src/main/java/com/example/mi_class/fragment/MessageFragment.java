package com.example.mi_class.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mi_class.R;
import com.example.mi_class.activity.ChatActivity;
import com.example.mi_class.adapter.MessageAdapter;
import com.example.mi_class.domain.Message;
import com.example.mi_class.domain.message_temp;
import com.example.mi_class.tool.HttpUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MessageFragment extends Fragment {
    private ListView listView;
    private  List<Message> list;
    private MessageAdapter messageAdapter;
    public static Handler handler;
    public static List<message_temp> temp_ms_data;
    private static final int setLocalHistory = 101;
    String ph;
    HashMap<String,String> p;
    private static final int getMsData = 100;
    private static final int refresh = 102;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        System.out.println("消息message碎片onCreateView");
        listView = (ListView) view.findViewById(R.id.message_list);
        list = new ArrayList<Message>();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                switch (msg.what){
                    case getMsData:
                        SharedPreferences preferences = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
                        SharedPreferences.Editor ed = preferences.edit();
                        ed.putString("message_list",(String)msg.getData().getString("res"));
                        ed.commit();
                        System.out.println("ook拿到数据我后端数据:"+(String)msg.getData().getString("res"));
                        reList((String)msg.getData().getString("res"));
                        break;
                    case setLocalHistory:
                        Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_LONG).show();
                        SharedPreferences p = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
                        String show = p.getString("message_list","");
                        System.out.println("拿到本地数据："+show);
                        reList(show);
                        break;
                    case refresh:
                        reList();
                        SharedPreferences preferences1 = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
                        String s = "[";
                        for(int i = 0 ; i < temp_ms_data.size() ; i ++)
                        {
                            s += "{\"to_user_id\":\""+temp_ms_data.get(i).getTo_user_id()+"\","+"\"time\":"+temp_ms_data.get(i).getTime()+",\"state\":"+temp_ms_data.get(i).getState()+",\"content\":\""+temp_ms_data.get(i).getContent()+"\",\"from_user_id\":\""+temp_ms_data.get(i).getFrom_user_id()+"\"},";
                        }
                        s = s.substring(0,s.length()-1);
                        s += "]";
                        SharedPreferences.Editor e = preferences1.edit();
                        e.putString("message_list",s);
                        e.commit();
                        if(ChatActivity.handler != null)
                        {
                            android.os.Message sm = new android.os.Message();
                            sm.what = 100;
                            ChatActivity.mess1 = temp_ms_data;
                            ChatActivity.handler.sendMessage(sm);
                        }

                        break;
                }
            }
        };
        getMessageData();
        System.out.println("碎片创建ook");
        //默认信息（展示用）
        Message message = new Message();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_drawable_teacher);
        message.setHead_portrait(bitmap);
        message.setName("张三");
        message.setLast_message("你好啊！");
        message.setTime(200);
        list.add(message);
        messageAdapter = new MessageAdapter(MessageFragment.this,list);
        listView.setAdapter(messageAdapter);
        setHasOptionsMenu(true);
        return view;
    }
    // 隐藏消息碎片中的menu
   @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.menu.main_add_btn,false);
    }

    public void reList()
    {
        list = new ArrayList<Message>();
        //转temp list
        HashSet<String> session = new HashSet<>();
        for(int i = 0 ; i < temp_ms_data.size();i++)
        {
            if(!temp_ms_data.get(i).getFrom_user_id().equals(ph)){
                session.add(temp_ms_data.get(i).getFrom_user_id());
            }
            if (!temp_ms_data.get(i).getTo_user_id().equals(ph)){
                session.add(temp_ms_data.get(i).getTo_user_id());
            }
        }
        Iterator<String> iterator = session.iterator();
        while(iterator.hasNext()){
            String name = iterator.next();
            long max = 0;
            String lastContent = "";
            for(int i = 0; i < temp_ms_data.size() ; i ++)
            {
                if(temp_ms_data.get(i).getTo_user_id().equals(name)||temp_ms_data.get(i).getFrom_user_id().equals(name))
                {
                    if(max < temp_ms_data.get(i).getTime()){
                        max = temp_ms_data.get(i).getTime();
                        lastContent = temp_ms_data.get(i).getContent();
                    }
                }
            }
            Message message = new Message();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_drawable_teacher);
            message.setHead_portrait(bitmap);
            message.setName(name);
            message.setLast_message(lastContent);
            message.setTime(max);
            list.add(message);
        }
        System.out.println("会话个数："+list.size());
        messageAdapter = new MessageAdapter(MessageFragment.this,list);
        listView.setAdapter(messageAdapter);
    }

    public void reList(String res){
        list = new ArrayList<Message>();
        SharedPreferences preferences = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
        String l = preferences.getString("message_list","");
        System.out.println("message:"+l);
        if(!l.equals("")){
            try {
                //转temp list
                temp_ms_data = new ArrayList<>();
                JSONArray array = new JSONArray(res);
                for(int i = 0 ; i < array.length(); i ++)
                {
                    JSONObject j = (JSONObject) array.get(i);
                    message_temp temp = new message_temp();
                    temp.setContent((String)j.get("content"));
                    temp.setFrom_user_id((String)j.get("from_user_id"));
                    temp.setState((int)j.get("state"));
                    temp.setTime((long)j.get("time"));
                    temp.setTo_user_id((String)j.get("to_user_id"));
                    temp_ms_data.add(temp);
                }
                HashSet<String> session = new HashSet<>();
                for(int i = 0 ; i < temp_ms_data.size();i++)
                {
                    if(!temp_ms_data.get(i).getFrom_user_id().equals(ph)){
                        session.add(temp_ms_data.get(i).getFrom_user_id());
                    }
                    if (!temp_ms_data.get(i).getTo_user_id().equals(ph)){
                        session.add(temp_ms_data.get(i).getTo_user_id());
                    }
                }
                Iterator<String> iterator = session.iterator();
                while(iterator.hasNext()){
                    String name = iterator.next();
                    long max = 0;
                    String lastContent = "";
                    for(int i = 0; i < temp_ms_data.size() ; i ++)
                    {
                        if(temp_ms_data.get(i).getTo_user_id().equals(name)||temp_ms_data.get(i).getFrom_user_id().equals(name))
                        {
                            if(max < temp_ms_data.get(i).getTime()){
                                max = temp_ms_data.get(i).getTime();
                                lastContent = temp_ms_data.get(i).getContent();
                            }
                        }
                    }
                    Message message = new Message();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_drawable_teacher);
                    message.setHead_portrait(bitmap);
                    message.setName(name);
                    message.setLast_message(lastContent);
                    message.setTime(max);
                    list.add(message);
                }
                System.out.println("会话个数："+list.size());
                messageAdapter = new MessageAdapter(MessageFragment.this,list);
                listView.setAdapter(messageAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void getMessageData(){
        try{
            SharedPreferences pf = getActivity().getSharedPreferences("user_login_info",MODE_PRIVATE);
            ph = pf.getString("phone","");

            if(!ph.equals("")){
                SharedPreferences history = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
                if(history.getString("last_time","").equals("")){
                    //没有过聊天记录或者缓存被清空，需加载所有记录 并写入时间 写入聊天内容缓存
                    p = new HashMap<>();
                    p.put("to_user_id",ph);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res = HttpUtils.sendPostMessage(p,"utf-8","allHistoryMessage");
                            if(!res.equals("-999"))
                            {
                                Bundle b = new Bundle();
                                b.putString("res",res);
                                android.os.Message m = new android.os.Message();
                                m.setData(b);
                                m.what = getMsData;
                                handler.sendMessage(m);
                            }else{
                                android.os.Message m = new android.os.Message();
                                m.what = setLocalHistory;
                                handler.sendMessage(m);
                            }

                        }
                    }).start();
                }else{
                    //有聊天历史记录只需要加载其时间之后的记录即可 并更新时间 写入聊天内容缓存
                }
            }else{
                Toast.makeText(getActivity(),"登录信息有误",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("获得数据库聊天记录失败");
        }

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setHasOptionsMenu(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = list.get(position);
                String name = message.getName();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chat_name",name);
                startActivity(intent);
            }
        });
    }
}
