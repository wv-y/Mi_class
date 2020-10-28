package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mi_class.R;
import com.example.mi_class.domain.Course;
import com.example.mi_class.fragment.CourseFragment;
import com.example.mi_class.fragment.MessageFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;



public class CourseAdapter extends  ArrayAdapter{
    private Context context;
    private List<Course> courseList;
    private final int resourceId;

    public CourseAdapter(Context context, int resource, List<Course> items) {
        super(context,resource,items);
        this.resourceId = resource;
        this.courseList = items;
    }


    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        Course course = (Course) getItem(position); // 获取当前项的实例
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

        TextView course_name = (TextView) view.findViewById(R.id.course_name);
        TextView course_code = (TextView) view.findViewById(R.id.course_code);
        TextView course_semester = (TextView) view.findViewById(R.id.course_semester);
        TextView course_member_number = (TextView) view.findViewById(R.id.course_member_number);
        RelativeLayout course_layout = (RelativeLayout) view.findViewById(R.id.course_layout);

        assert course != null;
        course_name.setText(course.getCourse_name()); //为文本视图设置文本内容
        course_code.setText("课程码："+course.getCourse_code());
        course_semester.setText(course.getCourse_semester());
        course_member_number.setText(course.getCouse_member_number());
       /*if((position+1)%3 == 1)
            course_layout.setBackgroundResource(R.drawable.back);
        if((position+1)%3 == 2)
            course_layout.setBackgroundResource(R.drawable.back02);
        if((position+1)%3 == 0)
            course_layout.setBackgroundResource(R.drawable.back05);*/

        return view;
    }
}
