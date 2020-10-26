package com.example.mi_class.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.domain.Image;
import com.example.mi_class.adapter.ImageAdapter;
import com.example.mi_class.domain.StudentData;
import com.example.mi_class.domain.TeacherData;
import com.example.mi_class.domain.User;
import com.example.mi_class.tool.HttpUtils;

import java.util.HashMap;
import java.util.Map;


public class UserInfoActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button eSchool;
    private int school_id;
    private String identity;
    private String gender;
    private EditText name;
    private RadioGroup rGender;
    private EditText id;
    private EditText department;
    private Button save;
    private User user;
    private Handler handler;
    private final int[] portraits = {
            R.drawable.portrait_1,
            R.drawable.portrait_2,
            R.drawable.portrait_3,
            R.drawable.portrait_4,
            R.drawable.portrait_5,
            R.drawable.portrait_6,
            R.drawable.portrait_7
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_user_info);

        final SharedPreferences pf = getSharedPreferences("user_login_info", Context.MODE_PRIVATE);

        setTitle("修改信息");
        if (getIntent().getBooleanExtra("FirstLogin", false)) {
            setTitle("完善信息");
        }
        identity = pf.getString("identity", "S");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        viewPager = findViewById(R.id.select_image);
        eSchool = findViewById(R.id.edit_school);
        name = findViewById(R.id.edit_username);
        id = findViewById(R.id.edit_id);
        rGender = findViewById(R.id.gender);
        department = findViewById(R.id.edit_department);
        rGender.setOnCheckedChangeListener(new MyRadioButtonListener());
        save = findViewById(R.id.save);

        if (identity.equals("T")) {
            findViewById(R.id.l_department).setVisibility(View.VISIBLE);
            TextView textView_id = findViewById(R.id.tv_id);
            textView_id.setText("工号");
        }

        initEdit();
        chooseImage();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (identity.equals("S")) {
                    StudentData studentData = new StudentData();
                    studentData.setStu_phone(pf.getString("phone", ""));
                    studentData.setStu_name(name.getText().toString());
                    studentData.setSex(gender);
                    studentData.setSchool_id(school_id);
                    studentData.setStu_id(id.getText().toString());
                    studentData.setPic_id(portraits[viewPager.getCurrentItem()]);
                    if (studentData.getStu_id().equals("") || studentData.getStu_name().equals("")
                            || studentData.getSex().equals("") || (studentData.getSchool_id() + "").equals("")
                            || (studentData.getPic_id() + "").equals("")) {
                        Toast.makeText(UserInfoActivity.this, "请完整信息", Toast.LENGTH_SHORT).show();
                    } else {
                        user = studentData;
                        postMessage(studentData);
                        SharedPreferences.Editor editor = pf.edit();
                        editor.putBoolean("FirstLogin", false);
                        editor.commit();
                    }
                } else {
                    TeacherData teacherData = new TeacherData();
                    teacherData.setTeacher_phone(pf.getString("phone", ""));
                    teacherData.setTeacher_name(name.getText().toString());
                    teacherData.setSex(gender);
                    teacherData.setSchool_id(school_id);
                    teacherData.setTeacher_id(id.getText().toString());
                    teacherData.setPic_id(portraits[viewPager.getCurrentItem()]);
                    teacherData.setDepartment(department.getText().toString());
                    if (teacherData.getTeacher_id().equals("") || teacherData.getTeacher_name().equals("")
                            || teacherData.getSex().equals("") || (teacherData.getSchool_id() + "").equals("")
                            || (teacherData.getPic_id() + "").equals("")) {
                        Toast.makeText(UserInfoActivity.this, "请完整信息", Toast.LENGTH_SHORT).show();
                    } else {
                        user = teacherData;
                        postMessage(teacherData);
                        SharedPreferences.Editor editor = pf.edit();
                        editor.putBoolean("FirstLogin", false);
                        editor.commit();
                    }
                }
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 200:
                        String info = msg.getData().getString("info");
                        if (info.equals("200")) {
                            MainActivity.user = user;
                            user.SetUser(UserInfoActivity.this);
                            startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                            onDestroy();
                            finish();
                        } else if (info.equals("199")) {
                            Toast.makeText(UserInfoActivity.this, "异常错误", Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void postMessage(StudentData student) {
        final Map<String, String> map = new HashMap<>();
        map.put("stu_name", student.getStu_name());
        map.put("stu_phone", student.getStu_phone());
        map.put("sex", student.getSex());
        map.put("school_id", student.getSchool_id() + "");
        map.put("stu_id", student.getStu_id());
        map.put("pic_id", student.getPic_id() + "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(map, "utf-8", "insertStu");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info", res);
                m.setData(b);
                m.what = 200;
                handler.sendMessage(m);
            }
        }).start();
    }

    public void postMessage(TeacherData teacher) {
        final Map<String, String> map = new HashMap<>();
        map.put("teacher_name", teacher.getTeacher_name());
        map.put("stu_phone", teacher.getTeacher_phone());
        map.put("sex", teacher.getSex());
        map.put("school_id", teacher.getSchool_id() + "");
        map.put("teacher_id", teacher.getTeacher_id());
        map.put("pic_id", teacher.getPic_id() + "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(map, "utf-8", "insertTea");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info", res);
                m.setData(b);
                m.what = 200;
                handler.sendMessage(m);
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseImage() {
        ImageAdapter imageAdapter = new ImageAdapter(this);
        for (int i = 0; i < 7; i++) {
            imageAdapter.addCardItem(new Image(portraits[i]));
        }
        viewPager.setAdapter(imageAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    public void initEdit() {
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
        eSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopupWindow();
            }
        });
    }

    private void showListPopupWindow() {
        final String[] list = {"北京信息科技大学", "北京大学", "清华大学"};//要填充的数据
        final ListPopupWindow listPopupWindow;
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));//用android内置布局，或设计自己的样式
        listPopupWindow.setAnchorView(eSchool);//以哪个控件为基准，在该处以logId为基准
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eSchool.setText(list[i]);//把选择的选项内容展示在EditText上
                school_id = i;
                listPopupWindow.dismiss();//如果已经选择了，隐藏起来
            }
        });
        listPopupWindow.show();//把ListPopWindow展示出来
    }

    class MyRadioButtonListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radio1 = findViewById(R.id.boy);
            RadioButton radio2 = findViewById(R.id.girl);
            // 选中状态改变时被触发
            switch (checkedId) {
                case R.id.boy:
                    // 当用户选择女性时
                    gender = "man";
                    radio2.setTextColor(Color.parseColor("#0099ff"));
                    radio1.setTextColor(Color.WHITE);
                    break;
                case R.id.girl:
                    // 当用户选择男性时
                    gender = "woman";
                    radio1.setTextColor(Color.parseColor("#0099ff"));
                    radio2.setTextColor(Color.WHITE);
                    break;
            }
        }
    }
}