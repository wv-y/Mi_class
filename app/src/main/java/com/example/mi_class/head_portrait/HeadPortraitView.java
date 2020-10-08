package com.example.mi_class.head_portrait;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import androidx.appcompat.widget.AppCompatImageView;


public class HeadPortraitView extends AppCompatImageView {
    private Context context;

    private Paint paint_bitmap = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_border = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap;
    private BitmapShader shader;
    private Matrix matrix = new Matrix();
    private float border_width = dip2px(5);
    private int border_color = Color.parseColor("#999999");

    public HeadPortraitView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        //准备了一张图片做显示效果
        //Drawable drawable = getResources().getDrawable(R.drawable.soji_3);

        //将图片保存到内部存储
        //BitmapUtil.saveHeadPortraitBitmap(context, ((BitmapDrawable) drawable).getBitmap());

        //初始化头像图片，也就是获取保存的图片
        initBitmap();
    }

    public void initBitmap(){
        //从内部存储中获取头像 如果没有头像，显示为白色
        bitmap = BitmapUtil.getHeadPortraitBitmap(context);
    }


    //保存头像的方法
    public void saveBitmap(Bitmap bitmap){
//        if (tag.equals("changeable"))
//            BitmapUtil.saveHeadPortraitBitmap(context, bitmap);
    }

    //修改头像方法
    public void setBitmap(Bitmap bitmap){
        saveBitmap(bitmap);
        this.bitmap = bitmap;
        //重新new一个shader，保证重绘时图片完整（之前忘记了这一步，把自己坑死了
        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //重绘
        invalidate();
    }

    //修改边框大小的方法
    public void setBorderWidth(int width){
        border_width = dip2px(width);
        this.invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null){
            if (shader == null) {
                shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }

            int viewWidth = getWidth();
            int viewHeight = getHeight();
            //拿到最小的边长，为了画圆（半径
            int viewMinSize = Math.min(viewWidth, viewHeight);

            //设置图片缩小的比例，根据线宽进行缩小
            matrix.setScale((viewMinSize - border_width) / bitmap.getWidth(), (viewMinSize - border_width) / bitmap.getHeight());
            shader.setLocalMatrix(matrix);

            paint_bitmap.setShader(shader);
            paint_bitmap.setAntiAlias(true);

            //画笔类型描边
            paint_border.setStyle(Paint.Style.STROKE);
            paint_border.setStrokeWidth(border_width);
            paint_border.setColor(border_color);
            //抗锯齿
            paint_border.setAntiAlias(true);

            float radius = viewMinSize / 2;

            //绘制圆框
            canvas.drawCircle(radius, radius, radius - border_width / 2f, paint_border);
            //canvas.translate(border_width, border_width);
            //绘制头像
            canvas.drawCircle(radius, radius, radius-border_width, paint_bitmap);
        }else {
            Log.i("Bitmap_error", "获取不到图片");
        }
    }

    //输入dp 转换为px
    private int dip2px(int dipVal) {
        //获取屏幕密度，1dp = *px
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dipVal * scale + 0.5f);
    }

}