package com.example.mi_class;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class Start_activity extends AppCompatActivity {

    private LottieAnimationView openAnimationView;

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
    }
    //延时3s跳转到登录界面
    private void start_main_activity(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Start_activity.this, Login_activity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                Start_activity.this.finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,1000);
    }
}