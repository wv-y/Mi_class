package com.example.mi_class.tool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.mi_class.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QrcodeTool extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback{

    private Camera camera;
    private SurfaceView sv;
    private SurfaceHolder sh;


    public  void checkCamera(Activity activity) {
        String[] PERMISSIONS_STORAGE={Manifest.permission.CAMERA};
        int permission = ActivityCompat.checkSelfPermission(activity,Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,1);

            Intent intent=new Intent();
            setResult(RESULT_CANCELED,intent);
            Log.d("QRCODE","ef");
            finish();

        }
    }
    public static Bitmap getQRCode(String value,int width,int height){
        Map<EncodeHintType, Object> map=new HashMap<EncodeHintType, Object>();
        map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        map.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, width, height, map);
        } catch (Exception e) {
            return null;
        }
        int[] temp=new int[width*height];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(bitMatrix.get(j,i))
                    temp[i*width+j]=0xff000000;
                else temp[i*width+j]=0xffffffff;
            }
        }
        Bitmap bitmp=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        bitmp.setPixels(temp,0,width,0,0,width,height);
        return bitmp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_qrcode_tool);
        sv=findViewById(R.id.sv);
        checkCamera(this);
        sv.getHolder().addCallback(this);
    }


    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        MultiFormatReader formatReader = new MultiFormatReader();
        int width=camera.getParameters().getPreviewSize().width,height=camera.getParameters().getPreviewSize().height;

        int[] rgbs=new int[width*height];

        decode(rgbs,bytes,width,height);

        BinaryBitmap temp=new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(width,height,rgbs)));
        try {
            Result result=formatReader.decode(temp);
            if(result!=null){
                Intent intent=new Intent();
                intent.putExtra("qrcode",result.getText());
                setResult(RESULT_OK,intent);

                finish();
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
    private void decode(int[] rgba, byte[] yuv420sp, int width, int height){
        final int frameSize = width * height;
        for (int j=0,yp=0;j<height;j++) {
            int uvp=frameSize+(j>>1)*width,u=0,v=0;
            for (int i=0;i<width;i++,yp++) {
                int y=(0xff&((int)yuv420sp[yp]))-16;
                if(y<0)y=0;
                if((i&1)==0){
                    v=(0xff&yuv420sp[uvp++])-128;
                    u=(0xff&yuv420sp[uvp++])-128;
                }
                int y1192=1192*y;
                int r=(y1192+1634*v);
                int g=(y1192-833*v-400*u);
                int b=(y1192+2066*u);

                if(r<0)r=0;
                else if(r>262143)r=262143;
                if(g<0)g=0;
                else if(g>262143)g=262143;
                if(b<0)b=0;
                else if(b>262143)b=262143;
                rgba[yp]=0xff000000|((r<<6)&0xff0000)|((g>>2)&0xff00)|((b>>10)&0xff);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        sh=surfaceHolder;
        camera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        camera.setDisplayOrientation(90);
        Camera.Parameters p=camera.getParameters();
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        try {
            camera.setPreviewDisplay(sh);
            camera.setPreviewCallback(this);
            camera.startPreview();
            camera.cancelAutoFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    camera.cancelAutoFocus();
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(camera==null)return;
        surfaceHolder.removeCallback(this);
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.lock();
        camera.release();
        camera=null;
    }
}