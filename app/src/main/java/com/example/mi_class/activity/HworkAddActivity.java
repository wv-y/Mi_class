package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.adapter.FileAdapter;
import com.example.mi_class.adapter.HworkFileAdapter;
import com.example.mi_class.datepicker.CustomDatePicker;
import com.example.mi_class.datepicker.DateFormatUtils;
import com.example.mi_class.domain.File;
import com.example.mi_class.tool.HttpFile;
import com.example.mi_class.tool.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mi_class.tool.HttpFile.post;

public class HworkAddActivity extends AppCompatActivity {

    private TextView uplode_textview,mTvSelectedTime;   //文件上传按钮、截止时间选择器
    private EditText eidt_title,edit_value; //标题、内容
    private HworkFileAdapter file_adapter;
    private ListView file_list_view;
    private List<File> file_list;
    private CustomDatePicker mTimerPicker;
    private String info,course_code,identity;
    private long choose_time;
    //存放本地文件绝对路径
    private String path;
//    private java.io.File file;

    private String urlStr;
    private Map<String,String> params;
    private Map<String, java.io.File> files;

//    String downUrl = "http://192.168.137.1:8080/homework/download";

//    断网大学
    String posturl = "http://192.168.137.1:8080/homework/add";

//    小米sj
//    String posturl = "http://192.168.43.165:8080/homework/add";

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
        setContentView(R.layout.activity_hwork_add);


        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        choose_time = System.currentTimeMillis();

        setTitle("新建作业");
        uplode_textview = (TextView) findViewById(R.id.uplode_file);
        file_list_view = (ListView) findViewById(R.id.file_item_list);
        eidt_title = (EditText) findViewById(R.id.homework_title_add);
        edit_value = (EditText) findViewById(R.id.homework_detail_add);

        file_list = new ArrayList<File>();

        Intent intent =getIntent();
        course_code = intent.getStringExtra("course_code");
        identity = intent.getStringExtra("identity");

        findViewById(R.id.ll_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerPicker.show(mTvSelectedTime.getText().toString());
            }
        });
        mTvSelectedTime = findViewById(R.id.tv_selected_time);
        initTimerPicker();

        uplode_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

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
            Toast.makeText(HworkAddActivity.this, path + "222222", Toast.LENGTH_SHORT).show();
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
            file_list.add(file1);
//            System.out.println("哈哈哈5"+file_list.size());
            file_adapter = new HworkFileAdapter(HworkAddActivity.this,R.layout.hwork_file_item,file_list,0,course_code,identity);
            file_list_view.setAdapter(file_adapter);
            // System.out.println("path" +path);
        }catch (java.lang.NullPointerException e){
            Toast.makeText(HworkAddActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }
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
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */

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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            返回按钮
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.pub_homework:
                System.out.println("EDIT"+eidt_title.getText().toString());
                System.out.println("EDIT"+edit_value.getText().toString());
                String title = eidt_title.getText().toString();
                String value = edit_value.getText().toString();
                if(title.equals("")||value.equals("")){
                    Toast.makeText(HworkAddActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,String> a = new HashMap<String, String>();
                    Map<String,java.io.File> b = new HashMap<String, java.io.File>();
                    a.put("course_id",course_code);
                    a.put("title",eidt_title.getText().toString());
                    a.put("value",edit_value.getText().toString());
                    System.out.println("homework_choose_time"+choose_time);
                    System.out.println("homework_choose_time"+String.valueOf(choose_time));
                    a.put("jz_long",String.valueOf(choose_time));
                    for(int i=0;i<file_list.size();i++) {
                        java.io.File file = new java.io.File(file_list.get(i).getPath());
                        b.put("file",file);
                    }
                    new Thread(new HttpFile(posturl,a,b,HomeworkActivity.homework_handler)).start();
                    this.finish();
                }
//                try{
//                    file = new java.io.File(path);
//                }catch (java.lang.NullPointerException e){
//                    Toast.makeText(HworkAddActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
//                }
//                Map<String,String> a = new HashMap<String, String>();
//                Map<String,java.io.File> b = new HashMap<String, java.io.File>();
//                a.put("course_id",course_code);
//                b.put("file",file);

        }
        return super.onOptionsItemSelected(item);
    }

//    右上角按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homework_pub_btn,menu);
        return super.onCreateOptionsMenu(menu);
    }




    private void initTimerPicker() {


//        String beginTime = "2018-10-17 18:00";
//        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);


        long year = 31536L*1000000L;
        String beginTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis()+year, true);;

        mTvSelectedTime.setText(beginTime);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mTvSelectedTime.setText(DateFormatUtils.long2Str(timestamp, true));
                choose_time = timestamp;
            }
        }, beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(false);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
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
}