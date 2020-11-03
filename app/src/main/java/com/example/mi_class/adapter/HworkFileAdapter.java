package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.activity.HworkAddActivity;
import com.example.mi_class.activity.HworkDetailActivity;
import com.example.mi_class.domain.File;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HworkFileAdapter extends ArrayAdapter {
    private Context context;
    private List<File> FileList;
    private final int resourceId;
    private String course_code;
    private int show_btn;
    private String identify;

    public HworkFileAdapter( Context context, int resource ,List<File> items,int n,String course_code,String identify) {
        super(context, resource,items);
        this.context = context;
        this.resourceId = resource;
        this.FileList = items;
        this.show_btn = n;
        this.course_code = course_code;
        this.identify = identify;
    }

    @NotNull
    @Override
    public View getView(final int position, View convertView, @NotNull ViewGroup parent) {
        System.out.println("哈哈哈6"+FileList.size());

        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(resourceId, null);//实例化一个对象
        ImageView del_imageview = (ImageView)view.findViewById(R.id.hwork_add_del_file);
        ImageView download_imageView = (ImageView)view.findViewById(R.id.hwork_add_upload_file);

        if(show_btn == 0){
            download_imageView.setVisibility(View.INVISIBLE);
            del_imageview.setVisibility(View.VISIBLE);
        }
        if(show_btn == 1){
            del_imageview.setVisibility(View.INVISIBLE);
            download_imageView.setVisibility(View.VISIBLE);
        }

//        File file = (File) getItem(position); // 获取当前项的实例

        download_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downUrl = "http://192.168.137.1:8080/homework/download";
//                String downUrl = "http://192.168.43.165:8080/homework/download";
                String file_id = FileList.get(position).getId();
                Uri uri;
                if (identify.equals("T")) {
                    uri = Uri.parse(downUrl+"?course_id="+course_code+"&file_id="+file_id+"&mode=tea");
                }
                else {
                    uri = Uri.parse(downUrl+"?course_id="+course_code+"&file_id="+file_id+"&mode=stu");
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });

        del_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("确定删除文件吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        TextView file_name = (TextView) view.findViewById(R.id.hwork_file_name);
        TextView file_size = (TextView) view.findViewById(R.id.hwork_file_size);
        ImageView imageView = (ImageView) view.findViewById(R.id.hwork_file_image);



        System.out.println("哈哈哈6"+FileList.size());

//        assert file!= null;
        file_name.setText(FileList.get(position).getName()); //为文本视图设置文本内容
        file_size.setText(FileList.get(position).getSize());
        imageView.setImageLevel(FileList.get(position).getImage_level());
        return view;
    }
}
