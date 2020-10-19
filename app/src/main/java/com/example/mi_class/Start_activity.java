package com.example.mi_class;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mi_class.tool.MyWebSocket;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class Start_activity extends AppCompatActivity {

    private LottieAnimationView openAnimationView;
//    static public MyWebSocket myWebSocketClient = null;
    private String url = "ws://192.168.43.165:8080/ws/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*
        SharedPreferences sharedPreferences = this.getSharedPreferences("login",MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun",true);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(isFirstRun){
            Log.e("debug","第一次运行");
            editor.putBoolean("isFirstRun",false);
            editor.commit();
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
        }else{
//            Log.e("debug","不是第一次运行");
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
        }*/
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_start_activity);
        openAnimationView = (LottieAnimationView) findViewById(R.id.openLottieView);

        try {   //BuildConfig.APPLICATION_ID   当前应用包名
            PackageInfo packageInfo = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID,
                    PackageManager.GET_SIGNATURES);
            String signValidString = getSignValidString(packageInfo.signatures[0].toByteArray());
            Log.e("获取应用签名", BuildConfig.APPLICATION_ID + ":" + signValidString);
        } catch (Exception e) {
            Log.e("获取应用签名", "异常:" + e);
        }

        start_main_activity();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //播放动画
        openAnimationView.setProgress(0f);
        openAnimationView.playAnimation();
 //       start_main_activity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        openAnimationView.cancelAnimation();
//        try {
//            this.myWebSocketClient.closeBlocking();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
    private String getSignValidString(byte[] paramArrayOfByte) throws NoSuchAlgorithmException, NoSuchAlgorithmException {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(paramArrayOfByte);
        return toHexString(localMessageDigest.digest());
    }

    public String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }
    //延时3s跳转到登录界面
    private void start_main_activity(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                overridePendingTransition(0, 0);
                SharedPreferences pf = getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
                if(pf.getString("phone","").equals("")){
                    //无登录态
                    Intent intent = new Intent(Start_activity.this, Login_activity.class);
                    startActivity(intent);
                }else{
                    //有登录态
                    System.out.println(pf.getString("phone","没有手机号"));
                    System.out.println(pf.getString("identity","没有身份"));
                    Intent intent = new Intent(Start_activity.this,MainActivity.class);
                    startActivity(intent);

                }
                Start_activity.this.finish();

            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,2000);
    }
}