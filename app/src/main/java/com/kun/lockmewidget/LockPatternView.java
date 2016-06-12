package com.kun.lockmewidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kun on 16-6-8.
 */
public class LockPatternView extends View {
    final private int size = 3;
    private Point[][] points = new Point[size][size];
    private List<Point> selectPoints;
    private Paint defaultPaint, selectPaint, errorPaint;
    private Path mPath;
    private int mWidth, mHeight, mRadius, mInnerRadius;
    private int offsetX = 10;
    private int offsetY;
    private int mBoxWidth;
    private boolean isActionMove, isActionDown, isActionUp;
    private OnLockPattern patterListener;
    private static final double CONSTANT_COS_30 = Math.cos(Math.toRadians(30));

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
        initPaints();
        mPath = new Path();
    }

    private void initPaints() {
        defaultPaint = new Paint();
        defaultPaint.setColor(getResources().getColor(R.color.blue_78d2f6));
        defaultPaint.setStrokeWidth(2.0f);
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setAntiAlias(true);

        selectPaint = new Paint();
        selectPaint.setColor(getResources().getColor(R.color.blue_00aaee));
        selectPaint.setStrokeWidth(4.0f);
        selectPaint.setStyle(Paint.Style.STROKE);
        selectPaint.setAntiAlias(true);

        errorPaint = new Paint();
        errorPaint.setColor(getResources().getColor(R.color.red_f3323b));
        errorPaint.setStrokeWidth(4.0f);
        errorPaint.setStyle(Paint.Style.STROKE);
        errorPaint.setAntiAlias(true);
    }

    private void initPoints() {
        selectPoints = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                points[i][j] = new Point(offsetX + mBoxWidth / 2 + j * mBoxWidth, offsetY + mBoxWidth / 2 + i * mBoxWidth,
                        i, j, i * points[0].length + j, Point.POINT_NORMAL);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRadius = (mWidth - offsetX * 2) / 4 / 2 - offsetX;
        mInnerRadius = mRadius / 3;
        offsetY = (mHeight - mWidth) / 2;
        mBoxWidth = (mWidth - offsetX * 2) / 3;
        initPoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawToCanvas(canvas);
    }

    private void drawToCanvas(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                switch (points[i][j].getStatus()) {
                    case Point.POINT_NORMAL:
                        canvas.drawCircle(points[i][j].getX(), points[i][j].getY(), mRadius, defaultPaint);
                        break;
                    case Point.POINT_PRESSED:
                        selectPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawCircle(points[i][j].getX(), points[i][j].getY(), mRadius, selectPaint);
                        selectPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(points[i][j].getX(), points[i][j].getY(), mInnerRadius, selectPaint);
                        break;
                    case Point.POINT_ERROR:
                        errorPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawCircle(points[i][j].getX(), points[i][j].getY(), mRadius, errorPaint);
                        errorPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(points[i][j].getX(), points[i][j].getY(), mInnerRadius, errorPaint);
                        break;
                }
            }
        }
        if (!selectPoints.isEmpty()) {
            Point tempPoint = selectPoints.get(0);
            for (int i = 1; i < selectPoints.size(); i++) {
                Point point = selectPoints.get(i);
                if (point.getStatus() == Point.POINT_PRESSED) {
                    drawLine(tempPoint, point, canvas, selectPaint);
                    drawTriangle(tempPoint, point, canvas, selectPaint);
                } else if (point.getStatus() == Point.POINT_ERROR) {
                    drawLine(tempPoint, point, canvas, errorPaint);
                    drawTriangle(tempPoint, point, canvas, errorPaint);
                }
                tempPoint = point;
            }
        }
    }

    private void drawTriangle(Point prePoint, Point nextPoint, Canvas canvas, Paint paint) {
        float distance = LockPatternUtil.getDistanceBetweenTwoPoints
                (prePoint.getX(), prePoint.getY(), nextPoint.getX(), nextPoint.getY());
        float x = prePoint.getX();
        float y = prePoint.getY() - this.mInnerRadius * 2;

        float x1 = x - this.mInnerRadius / 2;
        float y1 = y + (float) (this.mInnerRadius * CONSTANT_COS_30);
        float x2 = x + this.mInnerRadius / 2;
        float y2 = y1;

        float angleX = LockPatternUtil.getAngleLineIntersectX(
                prePoint.getX(), prePoint.getY(), nextPoint.getX(), nextPoint.getY(), distance);
        float angleY = LockPatternUtil.getAngleLineIntersectY(
                prePoint.getX(), prePoint.getY(), nextPoint.getX(), nextPoint.getY(), distance);

        mPath.reset();
        mPath.moveTo(x, y);
        mPath.lineTo(x1, y1);
        mPath.lineTo(x2, y2);
        mPath.close();
        //slide right down and right up
        Matrix triangleMatrix = new Matrix();
        if (angleX >= 0 && angleX <= 90) {
            triangleMatrix.setRotate(180 - angleY, prePoint.getX(), prePoint.getY());
        }
        //slide left up and left down
        else {
            triangleMatrix.setRotate(angleY - 180, prePoint.getX(), prePoint.getY());
        }
        mPath.transform(triangleMatrix);
        canvas.drawPath(mPath, paint);
    }

    private void drawLine(Point prePoint, Point nextPoint, Canvas canvas, Paint paint) {
        canvas.drawLine(prePoint.getX(), prePoint.getY(),nextPoint.getX(), nextPoint.getY(),paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event.getX(), event.getY());
                break;
        }
        return true;
    }

    private void handleActionUp(float x, float y) {
        isActionMove = false;
        isActionUp = true;
        isActionDown = false;
    }

    private void handleActionMove(float x, float y) {
        isActionMove = true;
        Point point = checkSelectPoint(x, y);
        if (point != null) {
            addSelectedPoint(point);
        }
    }

    private void handleActionDown(float x, float y) {
        isActionMove = false;
        isActionDown = true;
        isActionUp = false;

        resetCanvas();
        if (this.patterListener != null) {
            this.patterListener.onStart();
        }

        Point point = checkSelectPoint(x, y);
        if (point != null) {
            addSelectedPoint(point);
        }
    }

    private void resetCanvas() {
        selectPoints.clear();
        initPoints();
        this.postInvalidate();
    }

    private void addSelectedPoint(Point point) {
        if (!selectPoints.contains(point)) {
            point.setStatus(Point.POINT_PRESSED);
            selectPoints.add(point);
        }
        this.postInvalidate();
    }

    private Point checkSelectPoint(float x, float y) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                if (LockPatternUtil.isInCircle(points[i][j], mRadius, x, y))
                    return points[i][j];
            }
        }
        return null;
    }

    static class Point {
        private int x;// the x position of circle's center point
        private int y;// the y position of circle's center point
        private int row;// the cell in which row(0~n)
        private int column;// the cell in which column(0~n)
        private int index;// the cell value
        private int status = 0;//default status
        final public static int POINT_NORMAL = 0,
                POINT_PRESSED = 1,
                POINT_ERROR = 2;

        public Point(int x, int y, int row, int column, int index, int status) {
            this.x = x;
            this.y = y;
            this.row = row;
            this.column = column;
            this.index = index;
            this.status = status;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public interface OnLockPattern {
        void onStart();

        void onComplete(boolean isPasswordRight);
    }
}
