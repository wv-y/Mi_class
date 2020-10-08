package com.example.mi_class.head_portrait;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    public static String HP_FILE_NAME = "headPortrait.jpg";
    public static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + HP_FILE_NAME;

    public static Bitmap getHeadPortraitBitmap(Context context) {

        try {
            FileInputStream in = context.openFileInput(HP_FILE_NAME);
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            in.close();

            if (bitmap != null){
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        return bitmap;

    }

    public static void saveHeadPortraitBitmap(Context context, Bitmap bitmap){
        try {
            FileOutputStream out = context.openFileOutput(HP_FILE_NAME, Context.MODE_PRIVATE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            out.write(stream.toByteArray());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("IOException_save", "保存出错");
        }
    }
}
