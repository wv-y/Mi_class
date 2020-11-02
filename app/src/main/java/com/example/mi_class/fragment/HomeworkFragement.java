package com.example.mi_class.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.R;
import com.example.mi_class.adapter.HworkCommitAdapter;
import com.example.mi_class.domain.StuLogInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeworkFragement extends Fragment {
    private List<StuLogInfo> stuLogInfos =  new ArrayList<>();
    private String course_id,fb_time;
    private View view;

    public static HomeworkFragement newInstance(List<StuLogInfo> stuLogInfoList,String code,String time) {
        
        Bundle args = new Bundle();
        args.putParcelableArrayList("stuLogInfoList", (ArrayList<? extends Parcelable>) stuLogInfoList);
        args.putString("fb_time",time);
        args.putString("course_id",code);
        HomeworkFragement fragment = new HomeworkFragement();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_homework_all,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stuLogInfos = Objects.requireNonNull(getArguments()).getParcelableArrayList("stuLogInfoList");
        course_id = getArguments().getString("course_id");
        fb_time = getArguments().getString("fb_time");


        System.out.println("stuLoginFos"+stuLogInfos);
        TextView textView = view.findViewById(R.id.homework_stu_null);
        textView.setVisibility(View.GONE);
        if(stuLogInfos.size()==0){
            textView.setVisibility(View.VISIBLE);
            textView.setText("暂无同学");
        }
        RecyclerView recyclerView = view.findViewById(R.id.homework_all_student);
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new HworkCommitAdapter(recyclerView.getContext(),stuLogInfos,course_id,fb_time));
    }


}
