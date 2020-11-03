package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.adapter.HworkFileAdapter;
import com.example.mi_class.domain.File;
import com.example.mi_class.tool.HttpFile;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.process_dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HworkDetailActivity extends AppCompatActivity {

    private Map<String,String> params;

    private HworkFileAdapter file_adapter;

    private TextView fb_textview,jz_textview,file_list_text,commit_homework,commit_file_text;
    private EditText title_edittext,value_edittext;

    private ListView file_list_view,upload_homework_list;

    private List<File> homework_file_list;

    private List<File> commit_file_list;

    //存放本地文件绝对路径
    private String path;

    private String course_code,identity,info,jz_time,fb_time,title,value,phone_number;

    private int state; //截止和提交，    t未截止0，已截止1, s未截止-未提交2，s未截止-已提交3，s已截止-未提交4, s已截止-已提交4

    private final static int delete_homework = 205;
    private final static int get_studentlist = 206;

    String downUrl = "http://192.168.137.1:8080/homework/download";
//    String posturl = "http://192.168.43.165:8080/homework/put";

    //    断网大学
    String posturl = "http://192.168.137.1:8080/homework/put";

    //    小米sj
//    String posturl = "http://192.168.43.165:8080/homework/put";

//    private com.example.mi_class.tool.process_dialog process_dialog;


    // 进制位
    final static int JZ = 1024;
    // 1Byte
    final static int B = 1;
    // 1KB
    final static long KB = B * JZ;
    // 1MB
    final static long MB = KB * JZ;
    // 1GB
    final static long GB = MB * JZ;
    // 1TB
    final static long TB = GB * JZ;
    // 1PB
    final static long PB = TB * JZ;
    // EB (最多7EB)
    final static long EB = PB * JZ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_hwork_detail);


        initInfo();

        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("作业详情");
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            返回按钮
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.delete_work:  //删除作业
                AlertDialog.Builder builder = new AlertDialog.Builder(HworkDetailActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除作业吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteHomework(course_code,fb_time);
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

                return true;
            case R.id.check_stu:    //查看未交学生
                getStuList(course_code,fb_time);
            case R.id.commit_homework://截止和提交，    t未截止0，已截止1, s未截止-未提交2，s未截止-已提交3，s已截止-未提交4, s已截止-已提交5
                System.out.println("State"+state);
                if(state==2){
                    if(commit_file_list.size()>0){
                        commitHomework(course_code,fb_time,phone_number);
                        this.finish();
                    }
                    else {
                        Toast.makeText(HworkDetailActivity.this,"请选择文件提交",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(state==3){
                    Toast.makeText(HworkDetailActivity.this,"作业已提交",Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                else if(state==4||state==5){
                    Toast.makeText(HworkDetailActivity.this,"已截止，无法提交",Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                else
                    this.finish();

        }
        return super.onOptionsItemSelected(item);
    }

    //    右上角按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(identity.equals("T")) {
            getMenuInflater().inflate(R.menu.homework_detail_btn,menu);
            return super.onCreateOptionsMenu(menu);
        }
        else{
            getMenuInflater().inflate(R.menu.homework_commit_btn,menu);
            return super.onCreateOptionsMenu(menu);
        }


    }

    //    初始化信息
    private void initInfo(){



        Intent intent = getIntent();
        course_code = intent.getStringExtra("course_code");
        phone_number = intent.getStringExtra("phone_number");
        fb_time = intent.getStringExtra("fb_time");
        jz_time = intent.getStringExtra("jz_time");
        value = intent.getStringExtra("value");
        title = intent.getStringExtra("title");
        state = intent.getIntExtra("state",0);

        System.out.println("homeworkstate"+state);
        identity = intent.getStringExtra("identity");
//        获得活动传递的作业列表
        String homeworkFileList =  intent.getStringExtra("homework_file_list");
        System.out.println("homeworkListfile"+homeworkFileList);

        file_list_text = (TextView) findViewById(R.id.file_list_text);
        fb_textview = (TextView) findViewById(R.id.homework_pubdate_detail);
        jz_textview = (TextView) findViewById(R.id.homework_subdate_detail);
        title_edittext = (EditText) findViewById(R.id.homework_title_detail);
        value_edittext = (EditText) findViewById(R.id.homework_detail_detail);
        file_list_view = (ListView) findViewById(R.id.homework_file_list);
        upload_homework_list = (ListView) findViewById(R.id.upload_homework_list);
        commit_file_text= (TextView) findViewById(R.id.commit_file_text);

        commit_homework = (TextView) findViewById(R.id.homework_uplode_file);


        commit_file_list = new ArrayList<File>();


        if(identity.equals("S")){
            String stuHworkFileList =  intent.getStringExtra("homework_stu_file_list");
            System.out.println("stuHworkFileList"+stuHworkFileList);
            commit_file_list = get_file_list(stuHworkFileList);
            file_adapter = new HworkFileAdapter(HworkDetailActivity.this,R.layout.hwork_file_item,commit_file_list,1,course_code,identity);
            upload_homework_list.setAdapter(file_adapter);
        }
//截止和提交，    t未截止0，已截止1, s未截止-未提交2，s未截止-已提交3，s已截止-未提交4, s已截止-已提交5

//        上传文件按钮
        if(identity.equals("T")){
            commit_homework.setVisibility(View.GONE);
            commit_file_text.setVisibility(View.GONE);
        }
        else if(state == 3|| state==5){
            commit_file_text.setVisibility(View.VISIBLE);
            commit_homework.setVisibility(View.GONE);

        }
        else if(state==4){
            commit_file_text.setVisibility(View.VISIBLE);
            commit_homework.setVisibility(View.GONE);
            commit_file_text.setText("已截止");
        }
        else {
            //            commit_file_text.setVisibility(View.VISIBLE);
            commit_homework.setVisibility(View.VISIBLE);
            commit_homework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseFile();
                }
            });
            commit_file_text.setVisibility(View.VISIBLE);
        }

        if(homeworkFileList.equals("[]")){
            file_list_text.setText("无附件");
//            作业列表为空的情况
        }else
        {
            homework_file_list = get_file_list(homeworkFileList);
            file_adapter = new HworkFileAdapter(HworkDetailActivity.this,R.layout.hwork_file_item,homework_file_list,1,course_code,identity);
            file_list_view.setAdapter(file_adapter);
        }



//        File file = new File();


        fb_textview.setText("发布时间："+fb_time);
        jz_textview.setText(jz_time);
        title_edittext.setText(title);
        value_edittext.setText(value);


    }

    //    选择文件
    public void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        this.startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后版本
                Log.v("uri",uri.toString());
                path = getPath(this, uri);
                if(path == null){
                    Toast.makeText(this, "请从本地文件夹选取上传文件", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                }
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                Toast.makeText(HworkDetailActivity.this, path + "222222", Toast.LENGTH_SHORT).show();
            }
            java.io.File file = null;
            try{
                file = new java.io.File(path);
//            System.out.println("哈哈哈1"+path);

                File file1 = new File();
                file1.setPath(path);
//            System.out.println("哈哈哈2"+file1.getName());
                file1.setName(file.getName());
//            System.out.println("哈哈哈3"+file1.getName());
                file1.setSize(sizeFormat(file.length()));
//            System.out.println("哈哈哈4"+file_list.size());
                commit_file_list.add(file1);
//            System.out.println("哈哈哈5"+file_list.size());
                file_adapter = new HworkFileAdapter(HworkDetailActivity.this,R.layout.hwork_file_item,commit_file_list,0,course_code,identity);
                upload_homework_list.setAdapter(file_adapter);
                // System.out.println("path" +path);
            }catch (java.lang.NullPointerException e){
                Toast.makeText(HworkDetailActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //    文件大小格式
    public static String sizeFormat(long size, int precision) {
        if (precision > 6) {
            precision = 6;
        } else if (precision < 0) {
            precision = 0;
        }
        String format = "%." + precision + "f %s";
        Double val = 0.0;
        String unit = "B";
        if (size <= 0) {
            return String.format(format, val, unit);
        }
        long T = B;
        if (size >= B && size < MB) {
            // KB范围
            T = KB;
            unit = "KB";
        } else if (size < GB) {
            // MB 范围
            T = MB;
            unit = "MB";
        } else if (size < TB) {
            // GB
            T = GB;
            unit = "GB";
        } else if (size < PB) {
            // TB
            T = TB;
            unit = "TB";
        } else if (size < EB) {
            // PB
            T = PB;
            unit = "PB";
        } else if (size >= EB) {
            T = EB;
            unit = "EB";
        }

        val = (double) (size / T + (size * 1.0 % T / T));
        // size%1024=KB
        // size%(1024*1024)=MB
        // size%(1024*1024*1024)=GB
        // size%(1024*1024*1024*1024)=TB
        // size%(1024*1024*1024*1024*1024)=PB
        // size%(1024*1024*1024*1024*1024*1024)=EB
        // size%(1024*1024*1024*1024*1024*1024*1024)=ZB
        // size%(1024*1024*1024*1024*1024*1024*1024*1024)=YB
        // size%(1024*1024*1024*1024*1024*1024*1024*1024*1024)=BB

        return String.format(format, val, unit);
    }

    /**
     * 格式化显示文件大小:<br>
     * 1KB=1024B<br>
     * 1MB=1024KB<br>
     * 1GB=1024MB<br>
     * 1TB=1024GB<br>
     * 1PB=1024TB<br>
     * 1EB=1024PB<br>
     * 1ZB =1024EB<br>
     * 1YB =1024ZB<br>
     * 1BB=1024YB<br>
     *
     * @param size
     * @return
     */
    public static String sizeFormat(Long size) {
        return sizeFormat(size, 2);
    }

    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];


                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {


                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));


                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];


                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }


                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};


                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {


        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};


        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }




    //解析文件列表(String->List<File>
    public List<File> get_file_list(String str){
        List<File> list = new ArrayList<>();
        String name,type;
        int typeLevel;
        try{
            JSONArray array = new JSONArray(str);
            for(int i=0;i<array.length();i++){
                File file = new File();
                JSONObject jsonObject = array.getJSONObject(i);
                file.setName(jsonObject.getString("file_name"));    //文件名
                name = jsonObject.getString("file_name");
                file.setTime(jsonObject.getString("fb_time"));  //发布时间
                file.setSize(jsonObject.getString("file_size"));    //文件大小
                System.out.println("homeworklistmode"+jsonObject.getString("mode"));    //stu和tea
                file.setId(jsonObject.getString("file_id"));
                //设置文件类型
                type=getFileType(name);
                System.out.println("name是："+name);
                System.out.println("type是："+type);
                switch (type){
                    case "png": typeLevel=0;
                        break;
                    case "jpg": typeLevel=0;
                        break;
                    case "jpeg": typeLevel=0;
                        break;
                    case "gif": typeLevel=0;
                        break;
                    case "webp": typeLevel=0;
                        break;
                    case "mp3": typeLevel=1;
                        break;
                    case "wav": typeLevel=1;
                        break;
                    case "mp4": typeLevel=3;
                        break;
                    case "avi": typeLevel=3;
                        break;
                    case "pdf": typeLevel=2;
                        break;
                    case "ppt": typeLevel=2;
                        break;
                    case "doc": typeLevel=2;
                        break;
                    case "docx": typeLevel=2;
                        break;
                    case "html": typeLevel=2;
                        break;
                    default: typeLevel = 4;
                        break;
                }

                file.setImage_level(typeLevel);
                list.add(file);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    //捕获文件的类型
    public String getFileType(String str){
        String s[] = str.split("\\.");
        System.out.println("name："+str);
        System.out.println("s[]："+s);
        System.out.println("length"+s.length);
        if(s.length>0){
            return s[s.length-1];
        }else
            return "list_null";
    }


    //删除文件
    public void deleteHomework(String code,String time){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","homework/remove"));
                System.out.println("bundle是"+bundle);
                message.setData(bundle);
                message.what = delete_homework;
                HomeworkActivity.homework_handler.sendMessage(message);
            }
        }).start();
        HworkDetailActivity.this.finish();
    }

//    获取学生提交情况
    public void getStuList(String code,String time){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","homework/getLog"));
                message.setData(bundle);
                message.what = get_studentlist;
                HomeworkActivity.homework_handler.sendMessage(message);
            }
        }).start();
    }

//    提交作业
    public void commitHomework(String code,String time,String phone){

        Map<String,String> a = new HashMap<String, String>();
        Map<String,java.io.File> b = new HashMap<String, java.io.File>();
        a.put("course_id",code);
        a.put("fb_time",time);
        a.put("stu_phone",phone);

        for(int i=0;i<commit_file_list.size();i++) {
            java.io.File file = new java.io.File(commit_file_list.get(i).getPath());
            b.put("file",file);
        }

        HomeworkActivity.process_dialog.setCancelable(false);
        HomeworkActivity.process_dialog.show();
        new Thread(new HttpFile(posturl,a,b,HomeworkActivity.homework_handler)).start();

    }

}