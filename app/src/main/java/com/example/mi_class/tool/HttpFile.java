package com.example.mi_class.tool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.mi_class.activity.FileActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class HttpFile implements Runnable{
    private String urlStr;
    private Map<String, String> params;
    private Map<String, java.io.File> files;
    public HttpFile(String urlStr, Map<String, String> params, Map<String, java.io.File> files){
        this.files=files;
        this.params=params;
        this.urlStr=urlStr;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(post(urlStr,params,files), "UTF-8"));
           // System.out.println(in.readLine());
            String res = in.readLine();
            Message ms = new Message();
            ms.what = 200;
            Bundle b = new Bundle();
            b.putString("info",res);
            ms.setData(b);
            FileActivity.fileHandler.sendMessage(ms);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //这个方法不要动，可以直接用
    public static InputStream post(String urlStr, Map<String, String> params, Map<String, java.io.File> files) {
        final String BOUNDARY = "-------45962402127348";
        final String FILE_ENCTYPE = "multipart/form-data";
        InputStream is = null;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setConnectTimeout(5000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection","Keep-Alive");
            con.setRequestProperty("Charset","UTF-8");
            con.setRequestProperty("Content-Type",FILE_ENCTYPE+"; boundary="+BOUNDARY);

            StringBuilder sb = null;
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());;
            if (params!= null) {
                sb = new StringBuilder();
                for (String s : params.keySet()) {
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"");
                    sb.append(s);
                    sb.append("\"\r\n\r\n");
                    sb.append(params.get(s));
                    sb.append("\r\n");
                }

                dos.write(sb.toString().getBytes("UTF-8"));
            }

            if (files != null) {
                for (String s : files.keySet()) {
                    java.io.File f = files.get(s);
                    sb = new StringBuilder();
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"");
                    sb.append("file");
                    sb.append("\"; filename=\"");
                    sb.append(f.getName());
                    sb.append("\"\r\n");
                    sb.append("Content-Type: application/file");
                    sb.append("\r\n\r\n");
                    dos.write(sb.toString().getBytes("UTF-8"));

                    FileInputStream fis = new FileInputStream(f);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, len);
                    }
                    dos.write("\r\n".getBytes());
                    fis.close();
                }

                sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("--\r\n");
                dos.write(sb.toString().getBytes());
            }
            dos.flush();

            if (con.getResponseCode() == 200)
                is = con.getInputStream();

            dos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;
    }
}
