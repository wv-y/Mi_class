package com.example.mi_class.tool;

import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mi_class.activity.ChatActivity;
import com.example.mi_class.domain.Message;
import com.example.mi_class.domain.message_temp;
import com.example.mi_class.fragment.MessageFragment;

import android.content.SharedPreferences;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MyWebSocket extends WebSocketClient {
//    MyWebSocket myWebSocketClient;
    private static final String TAG = "MyWebSocket";
    private static String Url = "";
    static public MyWebSocket myWebSocket;
    static public boolean OK = false;
    HashMap<String,String> p;
//    static {
//        try {
//            myWebSocket = new MyWebSocket("s");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }
//    public MyWebSocket(String url,int x) throws URISyntaxException{
//        this(url);
//        myWebSocket = new MyWebSocket(url);
//    }

    /*
     *
     * url:"ws://服务器地址:端口号/websocket"
     * */
    public MyWebSocket(String url) throws URISyntaxException {

        super(new URI(url));
        Url = url;
//        myWebSocket = new MyWebSocket(url);
        System.out.println(url);
    }

    /*
     *
     * 打开webSocket时回调
     * */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {

        Log.i(TAG, "onOpen: 打开webSocket连接");
        android.os.Message m = new android.os.Message();
        m.what = 104;
        System.out.println("开始准备发送准备handler");
        MessageFragment.handler.sendMessage(m);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(OK && MyWebSocket.myWebSocket.getReadyState().equals(ReadyState.OPEN)){
                        System.out.println("心跳");
                        String hreat = "ping";
                        if(MyWebSocket.myWebSocket != null)
                            MyWebSocket.myWebSocket.send(hreat);
                        Thread.sleep(5000);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    try {
                        if(MyWebSocket.myWebSocket != null)
                            MyWebSocket.myWebSocket.reconnectBlocking();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();


    }


    /*
     *
     * 接收到消息时回调
     * */
    @Override
    public void onMessage(String s) {
        Log.i(TAG, "收到消息" + s);
        JSONObject res = JSONObject.parseObject(s);
        message_temp ms = new message_temp();
        ms.setContent((String)res.get("content"));
        ms.setFrom_user_id((String)res.get("from_user_id"));
        ms.setState(0);
        ms.setTime(Long.parseLong((String)res.get("time")));
        ms.setTo_user_id((String)res.get("to_user_id"));
        if(!ChatActivity.name.equals("") && !ChatActivity.phone.equals(""))
        {
            if(ms.getFrom_user_id().equals(ChatActivity.name)){
                ms.setState(1);
                p = new HashMap<>();
                p.put("to_user_id",ms.getTo_user_id());
                p.put("from_user_id",ms.getFrom_user_id());
                p.put("time",String.valueOf(ms.getTime()));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpUtils.sendPostMessage(p,"utf-8","toRead");
                    }
                }).start();
            }
        }
        MessageFragment.temp_ms_data.add(ms);
        android.os.Message m = new android.os.Message();
        m.what = 102;
        MessageFragment.handler.sendMessage(m);
//        MessageData messageData = JSON.parseObject(s, MessageData.class);
//        System.out.println("msg："+messageData.getMsgData());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String info = "发送者："+messageData.getFromUserId()+"  发送时间："+sdf.format(messageData.getTime());
//        addTextView(info+"\n"+messageData.getMsgData());

    }

    /*
     *
     * 断开连接时回调
     * */
    @Override
    public void onClose(int i, String s, boolean b) {
        Log.i(TAG, "断开webSocket连接");
        boolean flag = false;
        int cnt = 0;
        if(myWebSocket!=null){
            System.out.println("当前状态："+(!MyWebSocket.myWebSocket.getReadyState().equals(ReadyState.OPEN)));
            if((!MyWebSocket.myWebSocket.getReadyState().equals(ReadyState.OPEN)))
                reconnect();
        }

    }

    /*
     *
     * 出现异常时回调
     * */
    @Override
    public void onError(Exception e) {
        System.err.println("网络错误");
        if(myWebSocket != null)
            myWebSocket.close();
        e.printStackTrace();
    }

    public void reconnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if(myWebSocket != null){

                        if (MyWebSocket.myWebSocket.reconnectBlocking()) {
                            Log.i("s", "run: 连接服务器成功");
                        } else {
                            Log.i("s", "run: 连接服务器失败");
                        }
                    }

                } catch ( InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


}
