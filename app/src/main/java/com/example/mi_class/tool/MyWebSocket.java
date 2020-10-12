package com.example.mi_class.tool;

import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import android.content.SharedPreferences;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MyWebSocket extends WebSocketClient {
//    MyWebSocket myWebSocketClient;
    private static final String TAG = "MyWebSocket";
    private static String Url = "";
    static public MyWebSocket myWebSocket;
    static public boolean OK = false;
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
