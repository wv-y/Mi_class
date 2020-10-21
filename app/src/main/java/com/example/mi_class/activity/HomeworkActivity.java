package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.adapter.HomeworkAdapter;
import com.example.mi_class.domain.Homework;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeworkActivity extends AppCompatActivity {

    private List<Homework> homework_list;
    private HomeworkAdapter homework_adapter;
    private RecyclerView homework_recyclerview;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_homework);

        homework_recyclerview = (RecyclerView) findViewById(R.id.homework_recycler_view);
        homework_list = new ArrayList<>();

//        RecyclerView 需要一个layoutManager，也就是布局管理器
//        布局管理器能确定RecyclerView内各个子视图（项目视图）的位置
//        并能决定何时重新使用对用户已不可见的项目视图
//        安卓为我们预先准备好了三种视图管理器：LinearLayoutManager、
//        GridLayoutManager、StaggeredGridLayoutManager（详见文档）
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        homework_recyclerview.setLayoutManager(layoutManager);
//        初始化数据，（固定数据测试用)
        initInfo();

        homework_adapter = new HomeworkAdapter(homework_list);
        homework_recyclerview.setAdapter(homework_adapter);

        // 设置标题
        setTitle("所有作业");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initInfo(){
        Homework homework1 = new Homework("第一次作业","请完成附件文档中的题目",new Date().getTime(),new Date().getTime());
        Homework homework2 = new Homework("第二次作业" +
                "第二次作业" +
                "第二次作业" +
                "第二次作业" +
                "第二次作业第二次作业" +
                "第二次作业第二次作业" +
                "第二次作业第二次作业","请完成附请完成附件文档中的题目请完成附件文档中的题目" +
                "请完成附件文档中的题目请完成附件文档中的题目请完成附件文档中的题目" +
                "请完成附件文档中的题目请完成附件文档中的题目请完成附件文档中的题目" +
                "请完成附件文档中的题目请完成附件文档中的题目请完成附件文档中的题目" +
                "件文档中的题目",new Date().getTime(),new Date().getTime());

        Homework homework3 = new Homework("第3次作业","请完成附件文档中的题目",new Date().getTime(),new Date().getTime());
        homework_list.add(homework1);
        homework_list.add(homework2);
        homework_list.add(homework3);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            返回按钮
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_cart:
                initPopupWindow();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_add_btn,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initPopupWindow(){
        View popupView  = HomeworkActivity.this.getLayoutInflater().inflate(R.layout.popwindow_homework,null);
        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT,true);

        popupWindow.setContentView(popupView);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        Button new_homework = (Button)popupView.findViewById(R.id.popwindow_new_homework_button);
        Button cancel = (Button) popupView.findViewById(R.id.popwindow_cancel_button);

//                菜单背景色
        ColorDrawable colorDrawable = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(colorDrawable);



        View rootView =  LayoutInflater.from(HomeworkActivity.this).inflate(R.layout.activity_homework,null);

//        设置屏幕透明度
        backgroundAlpha(0.5f);

        popupWindow.setOnDismissListener(new popupDismissListener());
//        位置
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);

//                点击事件
        new_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeworkActivity.this,"aaaa",Toast.LENGTH_LONG).show();
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    /**
     * 改回背景透明度
     */
    class popupDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            backgroundAlpha(1.0f);
        }
    }

    /**
     * 设置屏幕背景透明度
     * @param bgAlaph
     */
    public void backgroundAlpha(float bgAlaph){
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = bgAlaph;   //0.0-1.0
        getWindow().setAttributes(layoutParams);
    }
}