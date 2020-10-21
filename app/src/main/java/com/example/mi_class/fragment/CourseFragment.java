package com.example.mi_class.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mi_class.R;
import com.example.mi_class.activity.CourseDetailsActivity;
import com.example.mi_class.adapter.CourseAdapter;
import com.example.mi_class.domain.Course;
import com.example.mi_class.tool.HttpUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CourseFragment extends Fragment {

    private final static int SET_ADAPTER = 330;

    private ListView course_list_view;
    private List<Course> course_list;
    private CourseAdapter course_adapter;
    private TextView is_null;
    private boolean is_show = false;
    public static Handler course_hadler;
    private String ph,identity;
    private Map<String,String> params;
    private SharedPreferences sp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        course_list_view = (ListView) view.findViewById(R.id.course_list);
        is_null = (TextView) view.findViewById(R.id.is_null);
        init_data(); // 初始化数据

        /*sp = getActivity().getSharedPreferences("course_list",MODE_PRIVATE);
        String local_course_list = sp.getString("local_course_list","");
        Log.d("local1",local_course_list);
        if(local_course_list.equals("-999") ||local_course_list.equals("") ) {
            is_null.setVisibility(View.VISIBLE);
        }
        else {
            course_list = get_course_list(local_course_list);
            course_adapter = new CourseAdapter(getActivity(), R.layout.fragment_courses_list, course_list);
            course_list_view.setAdapter(course_adapter); //设置适配器，显示查询结果
            if(identity.equals("T")){
                Log.d("inster",identity);
                tea_course_list();
            } else if(identity.equals("S")){
                stu_course_list();
            }
        }*/

        course_hadler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case SET_ADAPTER: // 接收活动刷新course_list的请求
                        Log.d("刷新","shi");
                        if(identity.equals("T"))
                            tea_course_list();
                        if(identity.equals("S"))
                            stu_course_list();
                        break;
                    case 331:
                        String info = msg.getData().getString("info");
                        Log.d("tea_search",info);
                        if(info.equals("[]")) {
                            save_course_list(info);
                            is_null.setVisibility(View.VISIBLE);
                            Log.d("刷新","无数据");
                        }else if(info.equals("-999")){
                            Log.d("init1","local_course_list");
                            sp = getActivity().getSharedPreferences("course_list",MODE_PRIVATE);
                            String local_course_list = sp.getString("local_course_list","");
                            Log.d("local1",local_course_list);
                            if(local_course_list.equals("-999") ||local_course_list.equals("") ) {
                                is_null.setVisibility(View.VISIBLE);
                            }
                            else {
                                course_list = get_course_list(local_course_list);
                                course_adapter = new CourseAdapter(getActivity(), R.layout.fragment_courses_list, course_list);
                                course_list_view.setAdapter(course_adapter); //设置适配器，显示查询结果
                            }
                            Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
                        }else {
                            is_null.setVisibility(View.GONE);
                            course_list = get_course_list(info);    // 解析服务器数据
                            save_course_list(info); //保存到缓存
                            course_adapter = new CourseAdapter(getActivity(), R.layout.fragment_courses_list, course_list);     //初始化适配器
                            course_list_view.setAdapter(course_adapter); //设置适配器，显示查询结果
                        }
                        break;
                }
            }
        };

        return view;


    }

    @Override
    public void onStart() {
        super.onStart();/*
        if (identity.equals("T")) {
            Log.d("inster", identity);
            tea_course_list();
        } else if (identity.equals("S")) {
            stu_course_list();
        }*/
        sp = getActivity().getSharedPreferences("course_list",MODE_PRIVATE);
        String local_course_list = sp.getString("local_course_list","");
        if(local_course_list.equals("-999") ||local_course_list.equals("") ) {
            is_null.setVisibility(View.VISIBLE);
        }
        else {
            course_list = get_course_list(local_course_list);
            course_adapter = new CourseAdapter(getActivity(), R.layout.fragment_courses_list, course_list);
            course_list_view.setAdapter(course_adapter); //设置适配器，显示查询结果
        }
        if(identity.equals("T")){
            Log.d("inster",identity);
            tea_course_list();
        } else if(identity.equals("S")){
            stu_course_list();
        }

    }

    // 老师向服务器查询course_list
    public void tea_course_list(){
        params = new HashMap<String,String>();
        params.put("teacher_phone",ph);
        Log.d("查询",ph);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","teaSearchCourse");
                Log.d("inster",res);
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                message.setData(b);
                message.what = 331;
                course_hadler.sendMessage(message);
            }
        }).start();
    }

    // 学生向服务器查询course_list
    public void stu_course_list(){
        Log.d("stu",ph);
        params = new HashMap<String,String>();
        params.put("stu_phone",ph);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","stuSearchCourse");
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                message.setData(b);
                message.what = 331;
                course_hadler.sendMessage(message);
            }
        }).start();
    }


    // 解析返回的数据
    public List<Course> get_course_list(String str){
        List<Course> list = new ArrayList<Course>();
        try {
            JSONArray array = new JSONArray(str);
            System.out.println(array);
            for (int i = 0; i < array.length(); i++) {
                Course course = new Course();
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("course_name");
                String code = object.getString("course_id");
                String introduce = object.getString("course_introduce");
                String time = object.getString("time");
                String num = object.getString("num");
                //Log.e("1", "name：" + name + "  age：" + age + "  sex：" + sex);
                course.setCourse_name(name);
                course.setCourse_code(code);
                course.setCourse_introduce(introduce);
                if(identity.equals("T")) {
                    course.setCourse_semester("创建时间："+time);
                }else{
                    course.setCourse_semester("加入时间："+time);
                }
                course.setCouse_member_number(num+"人已加入");
                list.add(course);
                System.out.println("cour_name"+name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        course_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 跳转到课程详情并且携带课程码
                Course course = course_list.get(i);
                String course_code = course.getCourse_code();
                Intent intent = new Intent(getActivity(), CourseDetailsActivity.class);
                intent.putExtra("course_code",course_code);
                intent.putExtra("course_name",course.getCourse_name());
                intent.putExtra("course_introduce",course.getCourse_introduce());

                startActivity(intent);
            }
        });
    }

    // 将查询到的新课程列表保存到缓存
    public void save_course_list(String str){
        sp = getActivity().getSharedPreferences("course_list",MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.putString("local_course_list",str);
        edit.apply();
        edit.commit();
    }

    // 从本地缓存初始化手机号和身份
    public void init_data(){
        sp = getActivity().getSharedPreferences("user_login_info",MODE_PRIVATE);
        ph = sp.getString("phone","");
        identity = sp.getString("identity","");
    }
}
