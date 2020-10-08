package com.example.mi_class.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.activity.CourseDetailsActivity;
import com.example.mi_class.adapter.CourseAdapter;
import com.example.mi_class.adapter.MessageAdapter;
import com.example.mi_class.domain.Course;
import com.example.mi_class.domain.Message;

import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends Fragment {

    private ListView course_list_view;
    private List<Course> course_list;
    private CourseAdapter course_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        course_list_view = (ListView) view.findViewById(R.id.course_list);

        // 默认数据显示用
        Course course = new Course("C语言","课程码:wumu","2019-2020\n第二学期","99人已加入");
        course_list = new ArrayList<Course>();
        course_list.add(course);
        course_list.add(course);

        course_adapter = new CourseAdapter(getActivity(), R.layout.fragment_courses_list,course_list);     //初始化适配器
        course_list_view.setAdapter(course_adapter); //设置适配器，显示查询结果

        return view;


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
                startActivity(intent);
            }
        });

    }
}
