package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mi_class.R;
import com.example.mi_class.adapter.FileAdapter;
import com.example.mi_class.domain.File;
import com.example.mi_class.tool.HttpFile;
import com.example.mi_class.tool.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileActivity extends AppCompatActivity {

    private List<File> file_list;
    private FileAdapter file_adapter;
    private ListView file_list_view;
    private ImageView search_file,file_cha,file_cha_tea,file_cha_stu,file_type;
    private TextView file_path,file_null,file_name_tea,file_name_stu,file_size_stu,file_size_tea,file_time_stu,file_time_tea;
    private Button file_commit,file_download_tea,file_download_stu,file_delete;
    private String info,course_code,file_name,file_size,fb_time,identity,file_id;
    public static Handler fileHandler;
    private final static int file_upLod = 200;
    private final static int get_filelist = 201;
    private final static int set_filelist = 202;
    private final static int delete_file = 203;
    private AlertDialog dialog;
    private Map<String,String> params;
    //存放本地文件绝对路径
    String path;
    String downUrl = "http://192.168.137.1:8080/sharedfile/download";
    String posturl = "http://192.168.137.1:8080/sharedfile/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_file);
       // setContentView(R.layout.pushfile_dialog);

        // 设置标题
        setTitle("课程文件");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        file_list_view = (ListView) findViewById(R.id.file_list_view);
        file_null = findViewById(R.id.file_null);
        View fileView = getLayoutInflater().inflate(R.layout.file_list,null);
        file_type = fileView.findViewById(R.id.file_image);

        init();
        verifyStoragePermissions(this);
        file_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(((TextView)(view.findViewById(R.id.file_name))).getText());
                file_name = ((TextView)(view.findViewById(R.id.file_name))).getText().toString();
                file_size = ((TextView)(view.findViewById(R.id.file_size))).getText().toString();
                fb_time = ((TextView)(view.findViewById(R.id.file_time))).getText().toString();
                file_id = ((TextView)(view.findViewById(R.id.file_id))).getTag().toString();
                fileDialog();
            }
        });
      /*  // 默认数据显示用
        File file1= new File("课程资料一.doc","未下载","400KB","2020-10-09");
        File file2 = new File("课程资料二.doc","未下载","2M","2020-11-11");
        File file = new File(file_name,file_style,file_size,file_time);

        file_list1 = new ArrayList<File>();
        file_list1.add(file1);
        file_list1.add(file2);

        file_adapter = new FileAdapter(FileActivity.this, R.layout.file_list, file_list1);     //初始化适配器
        file_list_view.setAdapter(file_adapter);*/

        fileHandler = new Handler(){
            public void handleMessage(@NonNull Message msg){
               super.handleMessage(msg);
               switch (msg.what){
                   case set_filelist:
                       get_file_list(course_code);
                       break;
                   case file_upLod:
                       String file_upBack = msg.getData().getString("info");
                       if("上传成功".equals(file_upBack)){
                           Toast.makeText(FileActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                           //关闭对话框前将路径清空
                           path = null;
                           get_file_list(course_code);
                           dialog.cancel();
                       } else
                           Toast.makeText(FileActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                       break;
                   case get_filelist:
                       info = msg.getData().getString("info");
                       System.out.println("info"+info);
                       if(info.equals("-999")) {
                           Toast.makeText(FileActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                       }else if(info.equals("[]")){
                           file_null.setVisibility(View.VISIBLE);
                           file_list = get_new_file_list(info);
                           file_adapter = new FileAdapter(FileActivity.this,R.layout.file_list,file_list);
                           file_list_view.setAdapter(file_adapter);
                       }else {
                           file_null.setVisibility(View.GONE);
                           file_list = get_new_file_list(info);
                           file_adapter = new FileAdapter(FileActivity.this,R.layout.file_list,file_list);
                           file_list_view.setAdapter(file_adapter);
                       }
                       break;
                   case delete_file:
                       info = msg.getData().getString("info");
                       System.out.println("info是："+info);
                       if(info.equals("删除文件")){
                           Toast.makeText(FileActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                           dialog.cancel();
                           get_file_list(course_code);
                       }
                       else
                           Toast.makeText(FileActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                       break;
               }

            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        if(identity.equals("T")) {
            inflater.inflate(R.menu.main_add_btn, menu);
            return true;
        }else
            return false;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_cart:
                showDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //打开对话框
    public void showDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(FileActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        //LayoutInflater inflater = LayoutInflater.from(SearchFileActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.pushfile_dialog,null);
        /*inflater.inflate(R.layout.pushfile_dialog,null);*/
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);

        search_file = dialogView.findViewById(R.id.search_file);
        file_cha = dialogView.findViewById(R.id.file_cha);
        file_path = dialogView.findViewById(R.id.file_path);
        file_commit = dialogView.findViewById(R.id.file_commit);
        //打开文件管理器
        search_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);


            }
        });
        file_cha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                path = null;

            }
        });
        file_commit.setOnClickListener(new View.OnClickListener() {
            java.io.File file;
            @Override
            public void onClick(View v) {
                try{
                    file = new java.io.File(path);
                }catch (java.lang.NullPointerException e){
                    Toast.makeText(FileActivity.this, "请从本地文件夹选取上传文件", Toast.LENGTH_SHORT).show();
                }
                Map<String,String> a = new HashMap<String, String>();
                Map<String,java.io.File> b = new HashMap<String, java.io.File>();
                a.put("course_id",course_code);
                b.put("file",file);
                new Thread(new HttpFile(posturl,a,b)).start();
                dialog = alertDialog;


            }
        });

    }
    //打开文件处理对话框
    public void fileDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(FileActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        View dialogView;
        dialog = alertDialog;
        //LayoutInflater inflater = LayoutInflater.from(SearchFileActivity.this);
        if(identity.equals("T")){
            dialogView = getLayoutInflater().inflate(R.layout.file_teacher,null);
            file_name_tea=dialogView.findViewById(R.id.file_name_tea);
            System.out.println("fileName是"+file_name_tea);
            file_name_tea.setText(file_name);
            file_time_tea=dialogView.findViewById(R.id.file_time_tea);
            file_time_tea.setText(fb_time);
            file_size_tea=dialogView.findViewById(R.id.file_size_tea);
            file_size_tea.setText(file_size);

            file_download_tea=dialogView.findViewById(R.id.file_download_tea);
            file_download_tea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //downLoad_file(course_code,file_id);
                    Uri uri = Uri.parse(downUrl+"?course_id="+course_code+"&file_id="+file_id);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            file_delete = dialogView.findViewById(R.id.file_delete);
            file_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_file(course_code,file_id);

                }
            });
            file_cha_tea = dialogView.findViewById(R.id.file_cha_tea);
            file_cha_tea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
            Window window = alertDialog.getWindow();
            //去掉背景白色实现对话框四个角完全曲化
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setContentView(dialogView);
        }else {
            dialogView = getLayoutInflater().inflate(R.layout.file_student, null);
            file_name_stu=dialogView.findViewById(R.id.file_name_stu);
            file_name_stu.setText(file_name);
            file_time_stu=dialogView.findViewById(R.id.file_time_stu);
            file_time_stu.setText(fb_time);
            file_size_stu=dialogView.findViewById(R.id.file_size_stu);
            file_size_stu.setText(file_size);
            Window window = alertDialog.getWindow();
            //去掉背景白色实现对话框四个角完全曲化
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setContentView(dialogView);
            file_download_stu=dialogView.findViewById(R.id.file_download_stu);
            file_download_stu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //downLoad_file(course_code,file_id);
                    Uri uri = Uri.parse(downUrl+"?course_id="+course_code+"&file_id="+file_id);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            file_cha_stu = dialogView.findViewById(R.id.file_cha_stu);
            file_cha_stu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
        }
        /*inflater.inflate(R.layout.pushfile_dialog,null);*/










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
    //初始化
    public void init(){
        Intent intent = getIntent();
        course_code=intent.getStringExtra("course_code");
        identity = getIntent().getStringExtra("identity");
        String fileList = getIntent().getStringExtra("file_list");
        System.out.println("info"+fileList);
        if(fileList.equals("[]")){
            System.out.println("fileList"+fileList);
            file_null.setVisibility(View.VISIBLE);
        }else
        {
            file_null.setVisibility(View.GONE);
            file_list = get_new_file_list(fileList);
            file_adapter = new FileAdapter(FileActivity.this,R.layout.file_list,file_list);
            file_list_view.setAdapter(file_adapter);
        }
    }
    //删除文件
    public void delete_file(String code,String id){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("file_id",id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info",HttpUtils.sendPostMessage(params,"utf-8","sharedfile/remove"));
                System.out.println("bundle是"+bundle);
                message.setData(bundle);
                message.what = delete_file;
                fileHandler.sendMessage(message);
            }
        }).start();
    }
    //获取文件列表
    public void get_file_list(String code){
        params = new HashMap<>();
        params.put("course_id",code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","sharedfile/getList"));
                message.setData(bundle);
                System.out.println("bundle是"+bundle);
                message.what = get_filelist;
                fileHandler.sendMessage(message);

            }
        }).start();
    }

    //解析文件列表
    public List<File> get_new_file_list(String str){
        List<File> list = new ArrayList<>();
        String name,type;
        int typeLevel;
        try{
            JSONArray array = new JSONArray(str);
            for(int i=0;i<array.length();i++){
                File file = new File();
                JSONObject jsonObject = array.getJSONObject(i);
                file.setName(jsonObject.getString("file_name"));
                name = jsonObject.getString("file_name");
                file.setTime(jsonObject.getString("fb_time"));

                file.setSize(jsonObject.getString("file_size"));

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



















    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                file_path.setText(path);
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后版本
                Log.v("uri",uri.toString());
                path = getPath(this, uri);
                file_path.setText(path);
                if(path == null){
                    Toast.makeText(this, "请从本地文件夹选取上传文件", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                file_path.setText(path);
                Toast.makeText(FileActivity.this, path + "222222", Toast.LENGTH_SHORT).show();
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

    //权限
    private  final int REQUEST_EXTERNAL_STORAGE = 1;
    private  String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public  void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

}