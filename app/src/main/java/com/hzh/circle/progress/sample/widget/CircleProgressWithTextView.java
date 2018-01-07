package com.hzh.circle.progress.sample.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Package: com.hzh.circle.progress.sample.widget
 * FileName: CircleProgressWithTextView
 * Date: on 2018/1/7  下午3:16
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public class CircleProgressWithTextView extends View {
    /**
     * 绘制相关
     */
    private RectF mRect;
    private Paint mCirclePaint;
    private Paint mTextPaint;
    //画笔颜色
    private int mCircleColor = Color.parseColor("#0AA4A2");
    private int mRemainCircleColor = Color.parseColor("#EFEFF0");
    private int mTextColor = Color.parseColor("#0AA4A2");
    /**
     * View相关尺寸
     */
    private int mWidth;
    private int mHeight;
    //外圆半径
    private float mRadius;
    //当前进度
    private float mProgress;
    //进度最大值
    private float mMax = 100;
    /**
     * 弧线的开始角度，默认是0，是水平的，我们要从上面开始画
     */
    private float mStartAngle = -90f;
    /**
     * 中心点X、Y坐标
     */
    private int mCenterX;
    private int mCenterY;

    public CircleProgressWithTextView(Context context) {
        super(context);
        init();
    }

    public CircleProgressWithTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressWithTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //外圆画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(dip2px(getContext(), 1f));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        //设置笔触为圆角
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mCirclePaint.setAntiAlias(true);
        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(dip2px(getContext(), 2f));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(sp2px(getContext(), 17f));
        mTextPaint.setAntiAlias(true);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float cValue = (Float) animation.getAnimatedValue();
                setProgress(cValue);
                postInvalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //控件的总宽高
        mWidth = w;
        mHeight = h;
        //取出padding值
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        //绘制范围
        mRect = new RectF();
        mRect.left = (float) paddingLeft;
        mRect.top = (float) paddingTop;
        mRect.right = (float) mWidth - paddingRight;
        mRect.bottom = (float) mHeight - paddingBottom;
        //计算直径和半径
        float diameter = (Math.min(mWidth, mHeight)) - paddingLeft - paddingRight;
        mRadius = (float) ((diameter / 2) * 0.98);
        //计算圆心的坐标
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureSpec(widthMeasureSpec), measureSpec(heightMeasureSpec));
    }

    private int measureSpec(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        //默认大小
        int defaultSize = dip2px(getContext(), 55f);
        //指定宽高则直接返回
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            //wrap_content的情况
            result = Math.min(defaultSize, size);
        } else {//未指定，则使用默认的大小
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(0.98f, 0.98f, mCenterX, mCenterY);
        //绘制当前进度的弧线
        mCirclePaint.setColor(mCircleColor);
        float curProgress = getProgress();
        float angle = 360 * (curProgress * 1.0f / getMax());
        canvas.drawArc(mRect, mStartAngle, angle, false, mCirclePaint);
        //绘制剩下的度数的弧线
        float remainAngle = 360f - angle;
        mCirclePaint.setColor(mRemainCircleColor);
        canvas.drawArc(mRect, mStartAngle + angle, remainAngle, false, mCirclePaint);
        //画文字
        String progressText = String.valueOf((int) curProgress);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float baseLine = -(fontMetrics.ascent + fontMetrics.descent) / 2;
        float textWidth = mTextPaint.measureText(progressText);
        float startX = mCenterX - (textWidth / 2);
        float endY = mCenterY + baseLine;
        canvas.drawText(progressText, startX, endY, mTextPaint);
    }

    public float getProgress() {
        return mProgress;
    }

    public synchronized void setProgress(float mProgress) {
        this.mProgress = mProgress;
    }

    public float getMax() {
        return mMax;
    }

    public synchronized void setMax(float mMax) {
        this.mMax = mMax;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }
}
