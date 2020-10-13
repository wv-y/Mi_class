package com.example.mi_class.activity;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import com.example.mi_class.R;
import com.example.mi_class.domain.Image;
import com.example.mi_class.adapter.ImageAdapter;

import java.util.Arrays;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private EditText eSchool;

    List<Image> images = Arrays.asList(
            new Image( R.drawable.portrait_1),
            new Image( R.drawable.portrait_2),
            new Image( R.drawable.portrait_3),
            new Image( R.drawable.portrait_4),
            new Image( R.drawable.portrait_5),
            new Image( R.drawable.portrait_6),
            new Image( R.drawable.portrait_7));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_user_info);

        // 设置标题
        setTitle("修改信息");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        viewPager = findViewById(R.id.select_image);
        eSchool = findViewById(R.id.edit_school);
        initEdit();
        chooseImage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseImage(){
        ImageAdapter imageAdapter = new ImageAdapter(this);
        imageAdapter.addCardItem(new Image( R.drawable.portrait_1));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_2));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_3));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_4));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_5));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_6));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_7));

        viewPager.setAdapter(imageAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    public void initEdit(){
        eSchool.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() >= (eSchool.getWidth() - eSchool
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        eSchool.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_down), null);
                        showListPopupWindow();
                        return true;
                    }
                }
                return false;

            }
        });
    }

    private void showListPopupWindow() {
        final String[] list = {"北京信息科技大学", "北京大学", "清华大学"};//要填充的数据
        final ListPopupWindow listPopupWindow;
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list));//用android内置布局，或设计自己的样式
        listPopupWindow.setAnchorView(eSchool);//以哪个控件为基准，在该处以logId为基准
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eSchool.setText(list[i]);//把选择的选项内容展示在EditText上
                listPopupWindow.dismiss();//如果已经选择了，隐藏起来
            }
        });
        listPopupWindow.show();//把ListPopWindow展示出来
    }
}