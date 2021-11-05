package com.inudev.ygbatikar.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.inudev.ygbatikar.R;

/**
 * Created by Rem on 12/9/2017 on 7:15 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class ArrowNavigationView extends View {

    private static final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mWidth = 0;
    private int mHeight = 0;
    public int mRotation;
    private Matrix mMatrix;
    private Bitmap mBitmap;
    private float mBearing;

    public ArrowNavigationView(Context context) {
        super(context);
        initialize();
    }

    public ArrowNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        mMatrix = new Matrix();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.anakpanah);
    }

    public void setmBearing(float b) {
        mBearing = b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas mCanvas) {
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        int canvasWidth = mCanvas.getWidth();
        int canvasHeight = mCanvas.getHeight();

        if (bitmapWidth > canvasWidth || bitmapHeight > canvasHeight) {
            mBitmap = Bitmap.createScaledBitmap(mBitmap,
                    (int) (bitmapWidth * 0.85), (int) (bitmapHeight * 0.85), true);
        }

        int bitmapX = mBitmap.getWidth() / 2;
        int bitmapY = mBitmap.getHeight() / 2;
        int parentX = mWidth / 2;
        int parentY = mHeight / 2;
        int centerX = parentX - bitmapX;
        int centerY = parentY - bitmapY;

        mRotation = (int) (mBearing - 360);

        mMatrix.reset();
        mMatrix.setRotate(mRotation, bitmapX, bitmapY);
        mMatrix.postTranslate(centerX, centerY);
        mCanvas.drawBitmap(mBitmap, mMatrix, mPaint);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public ArrowNavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
