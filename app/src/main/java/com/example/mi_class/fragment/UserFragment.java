package com.example.mi_class.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.mi_class.R;
import com.example.mi_class.activity.UserInfoActivity;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
            }
        });
        view.findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackDialog();
            }
        });
        view.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });
    }

    public void showFeedbackDialog(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_feedback,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();

        Button commit = view.findViewById(R.id.commit);
        final EditText feedback = view.findViewById(R.id.feedback);
        final EditText email = view.findViewById(R.id.email);

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("")){
                    Toast.makeText(getContext(),"请填写邮箱地址",Toast.LENGTH_SHORT).show();
                }
                else if(feedback.getText().toString().equals("")){
                    Toast.makeText(getContext(),"请填写反馈内容",Toast.LENGTH_SHORT).show();
                }
                else{
                    //保存数据
                }
            }
        });
        dialog.show();
    }

    public void showInfoDialog(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_info,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();

        dialog.show();
    }

}
