package com.example.mi_class.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.mi_class.R;
import com.example.mi_class.activity.ChatActivity;
import com.example.mi_class.adapter.MessageAdapter;
import com.example.mi_class.domain.Message;
import com.example.mi_class.domain.message_temp;
import com.example.mi_class.tool.Base64Utils;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.MyWebSocket;

import org.java_websocket.enums.ReadyState;
import org.jetbrains.annotations.NotNull;
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

import static android.content.Context.MODE_PRIVATE;

public class MessageFragment extends Fragment {
    private ListView listView;
    private  List<Message> list;
    private MessageAdapter messageAdapter;
    public static Handler handler;
    public static List<message_temp> temp_ms_data;
    public static final int setLocalHistory = 101;
    String ph;
    HashMap<String,String> p;
    public static final int getMsData = 100;
    public static final int refresh = 102;
    public static final int start_open = 104;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        System.out.println("消息message碎片onCreateView");
        listView = (ListView) view.findViewById(R.id.message_list);
        list = new ArrayList<Message>();
        handler = new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                switch (msg.what){
                    case getMsData:
                        SharedPreferences preferences = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
                        SharedPreferences.Editor ed = preferences.edit();
                        //本地缓存聊天信息不为空
                        if(!preferences.getString("message_list","").equals(""))
                        {
                            String temp = preferences.getString("message_list","");
                            String temp1 = (String)msg.getData().getString("res");

                            if(!temp1.equals("[]"))
                            {
                                //新增消息不是空

                                //新增的消息数据做读取状态判断
                                if(!ChatActivity.name.equals("") && !ChatActivity.phone.equals(""))
                                {
                                    p = new HashMap<>();
                                    p.put("to_user_id",ChatActivity.phone);
                                    p.put("from_user_id",ChatActivity.name);
                                    p.put("time",String.valueOf(new Date().getTime()));
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpUtils.sendPostMessage(p,"utf-8","toRead");
                                        }
                                    });
                                    List<message_temp> t2 =  to_ms_data(temp1);
                                    for(int i = 0 ; i < t2.size() ; i++)
                                    {
                                        message_temp t3 = t2.get(i);
                                        if(t3.getTo_user_id().equals(ChatActivity.phone) && t3.getFrom_user_id().equals(ChatActivity.name))
                                        {
                                            t3.setState(1);
                                        }
                                    }
                                    temp1 = ms_data_to_string(t2);
                                }
                                temp = temp.substring(0,temp.length()-1)+",";
                                temp1 = temp1.substring(1);
                                temp += temp1;

                                System.out.println("now:"+temp1);
                                System.out.println("history+now："+temp);
                                ed.putString("message_list",temp);
                                ed.commit();
                            }
                            System.out.println("getmsD");
                            reList(temp);
                            //更新聊天界面信息
                            if(!temp1.equals("[]") && ChatActivity.handler != null)
                            {
                                android.os.Message sm = new android.os.Message();
                                sm.what = 100;
                                ChatActivity.mess1 = temp_ms_data;
                                ChatActivity.handler.sendMessage(sm);
                            }
                        }else{
                            //本地缓存聊天信息为空
                            ed.putString("message_list",(String)msg.getData().getString("res"));
                            ed.commit();
                            System.out.println("ook拿到数据我后端数据:"+(String)msg.getData().getString("res"));
                            reList((String)msg.getData().getString("res"));
                        }


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
                        Collections.sort(temp_ms_data, new Comparator<message_temp>() {
                            public int compare(message_temp arg0, message_temp arg1) {
                                if(arg0.getTime()==arg1.getTime()) return 0;
                                return arg0.getTime() > arg1.getTime() ? 1 : -1;
                            }
                        });
                        SharedPreferences preferences1 = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
                        SharedPreferences.Editor e = preferences1.edit();
                        String s = "[";
                        for(int i = 0 ; i < temp_ms_data.size() ; i ++)
                        {
                            s += "{\"to_user_id\":\""+temp_ms_data.get(i).getTo_user_id()+"\","+"\"time\":"+temp_ms_data.get(i).getTime()+",\"state\":"+temp_ms_data.get(i).getState()+",\"content\":\""+temp_ms_data.get(i).getContent()+"\",\"from_user_id\":\""+temp_ms_data.get(i).getFrom_user_id()+"\"},";
                            if(i == temp_ms_data.size()-1)
                            {
                                e.putString("last_time",String.valueOf(temp_ms_data.get(i).getTime()));
                            }
                        }
                        s = s.substring(0,s.length()-1);
                        s += "]";

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
                    case start_open:
                        System.out.println("开始准备数据");
                        getMessageData();
                        break;
                }
            }
        };
//        if(!MyWebSocket.myWebSocket.getReadyState().equals(ReadyState.OPEN))
//        {
//            //一上来没网
//            getMessageData();
//        }
        getMessageData();
        System.out.println("碎片创建ook");
        //默认信息（展示用）
        Message message = new Message();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_drawable_teacher);
        message.setHead_portrait(bitmap);
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
    public String ms_data_to_string(List<message_temp> temps)
    {
        String s = "[";
        for(int i = 0 ; i < temps.size() ; i ++)
        {
            s += "{\"to_user_id\":\""+temps.get(i).getTo_user_id()+"\","+"\"time\":"+temps.get(i).getTime()+",\"state\":"+temps.get(i).getState()+",\"content\":\""+temps.get(i).getContent()+"\",\"from_user_id\":\""+temps.get(i).getFrom_user_id()+"\"},";
        }
        s = s.substring(0,s.length()-1);
        s += "]";
        return s;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
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
            int cnt = 0;
            String name = iterator.next();
            long max = 0;
            String lastContent = "";
            for(int i = 0; i < temp_ms_data.size() ; i ++)
            {
                message_temp t1 = temp_ms_data.get(i);
                if(temp_ms_data.get(i).getTo_user_id().equals(name)||temp_ms_data.get(i).getFrom_user_id().equals(name))
                {
                    if(max < temp_ms_data.get(i).getTime()){
                        max = temp_ms_data.get(i).getTime();
                        lastContent = temp_ms_data.get(i).getContent();
                    }
                    //来自该用户的消息
                    if(t1.getFrom_user_id().equals(name) && t1.getState() == 0){
                        cnt++;
                    }
                }
            }
            Message message = new Message();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_drawable_teacher);
            message.setHead_portrait(bitmap);
            message.setName(name);
            message.setLast_message(new String(Base64Utils.decodeFromString(lastContent)));
            message.setUnReadCnt(cnt);
            message.setTime(max);
            list.add(message);
        }
        System.out.println("会话个数："+list.size());
        messageAdapter = new MessageAdapter(MessageFragment.this,list);
        listView.setAdapter(messageAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reList(String res){
        list = new ArrayList<Message>();
        SharedPreferences preferences = getActivity().getSharedPreferences(ph+"_ms",MODE_PRIVATE);
        String l = preferences.getString("message_list","");
//        System.out.println("message:"+l);
        if(!l.equals("")){
            try {
                //转temp list
                temp_ms_data = new ArrayList<>();
                JSONArray array = new JSONArray(res);
//                System.out.println("json：长度:"+array.length());
                for(int i = 0 ; i < array.length(); i ++)
                {
                    JSONObject j = (JSONObject) array.get(i);
                    message_temp temp = new message_temp();
                    temp.setContent((String)j.get("content"));
                    temp.setFrom_user_id((String)j.get("from_user_id"));
                    temp.setState((int)j.get("state"));
                    temp.setTime((long)j.get("time"));
//                    System.out.println("现在的i:"+i+" 内容："+temp.getContent());
                    if(i==array.length()-1)
                    {
                        SharedPreferences.Editor e = preferences.edit();
                        e.putString("last_time",String.valueOf(j.get("time")));
                        e.commit();
                    }
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
                    int cnt = 0;
                    String lastContent = "";
                    for(int i = 0; i < temp_ms_data.size() ; i ++)
                    {
                        message_temp t1 = temp_ms_data.get(i);
                        if(temp_ms_data.get(i).getTo_user_id().equals(name)||temp_ms_data.get(i).getFrom_user_id().equals(name))
                        {
                            if(max < temp_ms_data.get(i).getTime()){
                                max = temp_ms_data.get(i).getTime();
                                lastContent = temp_ms_data.get(i).getContent();
                            }
                            if(t1.getFrom_user_id().equals(name) && t1.getState() == 0){
                                cnt++;
                            }
                        }
                    }
                    Message message = new Message();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_drawable_teacher);
                    message.setHead_portrait(bitmap);
                    message.setName(name);
                    message.setLast_message(new String(Base64Utils.decodeFromString(lastContent)));
                    message.setUnReadCnt(cnt);
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
            System.out.println("activity is null:"+(getActivity()==null));
            SharedPreferences pf1 = getActivity().getSharedPreferences("user_login_info",MODE_PRIVATE);
            ph = pf1.getString("phone","");
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
                    p = new HashMap<>();
                    p.put("to_user_id",ph);
                    p.put("time",history.getString("last_time",""));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res = HttpUtils.sendPostMessage(p,"utf-8","historyMessage");
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
        System.out.println("碎片活动创建ook");
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
