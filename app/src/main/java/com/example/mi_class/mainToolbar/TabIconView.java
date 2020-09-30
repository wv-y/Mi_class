package com.example.mi_class.mainToolbar;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;


public class TabIconView extends androidx.appcompat.widget.AppCompatImageView {

    /** 改变透明度 */
    private Paint mPaint;
    /** focus下bitmap*/
    private Bitmap mSelectedIcon;
    /** normal下bitmap*/
    private Bitmap mNormalIcon;
    /** focus bitmap矩阵*/
    private Rect mSelectedRect;
    /** normal bitmap矩阵*/
    private Rect mNormalRect;
    /** 当前选择项(mSelectedIcon)透明度
     * <p><b> mNormalIcon</b> 透明度即为 255 - mSelectedAlpha</p> */
    private int mSelectedAlpha = 0;

    public TabIconView(Context context) {
        super(context);
    }

    public TabIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化资源图片bitmap及相关绘制对象
     * @param normal normals
     * @param selected focus
     */
    public final void init(int normal, int selected, int width, int height) {
        this.mNormalIcon = drawableToBitmap(normal);
        this.mSelectedIcon = drawableToBitmap(selected);
        this.mNormalRect = new Rect(0, 0, width, height);
        this.mSelectedRect = new Rect(0, 0, width, height);
        this.mPaint = new Paint(1);
    }

    private Bitmap createBitmap(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPaint == null) {
            return;
        }
        this.mPaint.setAlpha(255 - this.mSelectedAlpha);
        canvas.drawBitmap(this.mNormalIcon, null, this.mNormalRect, this.mPaint);
        this.mPaint.setAlpha(this.mSelectedAlpha);
        canvas.drawBitmap(this.mSelectedIcon, null, this.mSelectedRect, this.mPaint);
    }

    /**
     * 改变透明度值
     * @param alpha 透明度
     */
    public final void changeSelectedAlpha(int alpha) {
        this.mSelectedAlpha = alpha;
        invalidate();
    }

    /**
     * 改变透明度百分比
     * @param offset 百分比
     */
    public final void offsetChanged(float offset) {
        changeSelectedAlpha((int) (255 * (1 - offset)));
    }

    public Bitmap drawableToBitmap(int resource) {
        Drawable drawable = getContext().getResources().getDrawable(resource);
        int w = drawable.getIntrinsicWidth();//获取宽
        int h = drawable.getIntrinsicHeight();//获取高
        Bitmap.Config btmConfig =drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, btmConfig);
        //绘制新的bitmap
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        //返回bitmap
        return bitmap;
    }
}
