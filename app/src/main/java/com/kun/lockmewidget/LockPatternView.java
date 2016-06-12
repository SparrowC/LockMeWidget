package com.kun.lockmewidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kun on 16-6-8.
 */
public class LockPatternView extends View {
    final private int size = 3;
    private Point[][] points = new Point[size][size];
    private Bitmap pointNormal, pointPressed, pointError;
    private boolean isInitPoints;
    private int width, height;
    private int offsetY, offsetX;
    private Paint paint;

    public LockPatternView(Context context) {
        this(context, null);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    private void initPoint() {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        int spacing;
        if (width < height) {
            //竖屏
            spacing = width / 4;
            offsetY = (height - width) / 2 + spacing / 2;
            offsetX = spacing / 2;
        } else {
            //横屏
            spacing = height / 4;
            offsetX = (width - height) / 2 + spacing / 2;
            offsetY = spacing / 2;
        }
        width = height;


        final BitmapFactory.Options options = new BitmapFactory.Options();

        // Calculate inSampleSize
        options.inSampleSize = 2;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        pointNormal = BitmapFactory.decodeResource(getResources(), R.drawable.locus_round_original, options);
        pointPressed = BitmapFactory.decodeResource(getResources(), R.drawable.locus_round_click, options);
        pointError = BitmapFactory.decodeResource(getResources(), R.drawable.locus_round_click_error, options);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        offsetX += pointNormal.getWidth() / 2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                points[i][j] = new Point(offsetX + spacing * i, offsetY + spacing * j);
            }
        }
    }

    private void points2Canvas(Canvas canvas) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                switch (points[i][j].state) {
                    case Point.POINT_NORMAL:
                        canvas.drawBitmap(pointNormal, points[i][j].x, points[i][j].y, paint);
                        break;
                    case Point.POINT_PRESSED:
                        canvas.drawBitmap(pointPressed, points[i][j].x, points[i][j].y, paint);
                        break;
                    case Point.POINT_ERROR:
                        canvas.drawBitmap(pointError, points[i][j].x, points[i][j].y, paint);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInitPoints) {
            isInitPoints = true;
            initPoint();
        }
        points2Canvas(canvas);
    }


    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        //先根据宽度进行缩小
        while (width / inSampleSize > reqWidth) {
            inSampleSize++;
        }
        //然后根据高度进行缩小
        while (height / inSampleSize > reqHeight) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    static class Point {
        int x, y;
        int state;
        final public static int POINT_NORMAL = 0,
                POINT_PRESSED = 1,
                POINT_ERROR = 2;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
            state = 0;
        }

    }
}
