package com.wingsofts.dragphotoview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by wing on 2016/12/22.
 */

public class DragPhotoView extends PhotoView {

    private final static int MAX_MOVE_Y = 500;

    private final static long ANIMATION_DURATION = 300;

    private int mWidth;
    private int mHeight;
    private int mAlpha = 255;
    private float mDownX;
    private float mDownY;
    private float mTouchX;
    private float mTouchY;
    private float mScale = 1f;
    private float mMinScale = 0.45f;
    private float mScaleNormalX = 1f, mScaleNormalY = 1f;
    private float mOffsetEmptyMoveX = 0f, mOffsetEmptyMoveY = 0f;
    private float mMoveY;
    private float mMoveX;
    private boolean isEnd = false;
    private boolean isLongTouch = false;
    private boolean isInterceptTouch = false;
    private boolean isEndToNormal = false;
    private Paint mPaint;
    private OnTapClickListener mTapListener;
    private OnPreViewFinishedListener mFinishedListener;


    public interface OnTapClickListener {
        void onTap(DragPhotoView view);
    }

    public interface OnPreViewFinishedListener {
        void onMoveDown(DragPhotoView view);
        void onFinish(DragPhotoView view, float x, float y, float w, float h);
        void onNormal(DragPhotoView view);
    }

    public DragPhotoView(Context context) {
        this(context, null);
    }

    public DragPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public DragPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
        if (isEndToNormal) {
            //缩放恢复原本大小导致的xy位置，基于中心点
            //根据图片当前的缩放比例，减去恢复到目标需要的比例算到偏差半径，再减去空白区偏移量,比如放大部分超过了原本的空白区
            canvas.translate(mMoveX + (mWidth * (mScaleNormalX - mScale)) / 2 - mOffsetEmptyMoveX,
                    (mMoveY + (mHeight * (mScaleNormalY - mScale)) / 2) - mOffsetEmptyMoveY);
            canvas.scale(mScaleNormalX, mScaleNormalY, mWidth / 2, mHeight / 2);
        } else {
            canvas.translate(mMoveX, mMoveY);
            canvas.scale(mScale, mScale, mWidth / 2, mHeight / 2);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public void setOnLongClickListener(final OnLongClickListener l) {
        OnLongClickListener longClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongTouch = true;
                if (l != null) {
                    return l.onLongClick(v);
                }
                return false;
            }
        };
        super.setOnLongClickListener(longClickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (getScale() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isEnd = !isEnd;
                    mDownX = event.getX();
                    mDownY = event.getY();
                    mTouchX = x;
                    mTouchY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mTouchX;
                    float deltaY = y - mTouchY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);

                    //viewpager中横向
                    if (mScale >= 1 && (absDeltaX >= absDeltaY)) {
                        return super.dispatchTouchEvent(event);
                    }

                    if (deltaY < 0 && mMoveX != 0) {
                        if (!isInterceptTouch) {
                            mScale = 1;
                            return super.dispatchTouchEvent(event);
                        }
                    }

                    if (mMoveY >= 0 && event.getPointerCount() == 1) {
                        resolveMove(event);
                        if (mMoveY != 0) {
                            isInterceptTouch = true;
                        }
                        return true;
                    }

                    if (mMoveY >= 0 && mScale < 0.97) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mScale == 1 && (mMoveX != 0 || mMoveY != 0)) {
                        doAnimation();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (event.getPointerCount() == 1) {
                        if (mScale < 1) {
                            //the MoveY is 80%, then close the preview.
                            if (mMoveY > MAX_MOVE_Y*0.8) {
                                if (mFinishedListener != null) {
                                    mFinishedListener.onFinish(this, mMoveX, mMoveY, mWidth, mHeight);
                                }
                            } else {
                                doAnimation();
                                if (mFinishedListener != null) {
                                    mFinishedListener.onNormal(this);
                                }
                            }
                        } else {
                            if (!isLongTouch) {
                                postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mMoveX == 0 && mMoveY == 0 && isEnd) {
                                            if (mTapListener != null) {
                                                mTapListener.onTap(DragPhotoView.this);
                                            }
                                        }
                                        isEnd = false;
                                    }
                                }, 350);
                            } else if (mScale == 1 && (mMoveX != 0 || mMoveY != 0)) {
                                doAnimation();
                            }
                        }
                    }
                    isInterceptTouch = false;
                    isLongTouch = false;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void resolveMove(MotionEvent event) {
        float moveY = event.getY();
        float moveX = event.getX();
        mMoveX = moveX - mDownX;
        mMoveY = moveY - mDownY;
        float tmpMoveY = mMoveY;
        if (mMoveY < 0) {
            mMoveY = 0;
        }
        float percent = mMoveY / MAX_MOVE_Y;
        if (mScale >= mMinScale && mScale <= 1f) {
            mScale = 1 - percent;
            mAlpha = (int) (255 * (1 - percent));
            if (mAlpha > 255) {
                mAlpha = 255;
            } else if (mAlpha < 0) {
                mAlpha = 0;
            }
        }
        if (mScale < mMinScale) {
            mScale = mMinScale;
        } else if (mScale > 1f) {
            mScale = 1;
        }
        if (tmpMoveY < 0) {
            return;
        }
        if (mFinishedListener != null) {
            mFinishedListener.onMoveDown(this);
        }

        invalidate();
    }


    private void doAnimation() {
        doScaleAnimation().start();
        doTranslateXAnimation().start();
        doTranslateYAnimation().start();
        doAlphaAnimation().start();
    }

    private ValueAnimator doAlphaAnimation() {
        final ValueAnimator animator = ValueAnimator.ofInt(mAlpha, 255);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAlpha = (int) valueAnimator.getAnimatedValue();
            }
        });
        return animator;
    }

    private ValueAnimator doTranslateYAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mMoveY, 0);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mMoveY = (float) valueAnimator.getAnimatedValue();
            }
        });

        return animator;
    }

    private ValueAnimator doTranslateXAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mMoveX, 0);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mMoveX = (float) valueAnimator.getAnimatedValue();
            }
        });

        return animator;
    }

    private ValueAnimator doScaleAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mScale, 1);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return animator;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public void setOnTapListener(OnTapClickListener listener) {
        mTapListener = listener;
    }

    public void OnPreViewFinishedListener(OnPreViewFinishedListener listener) {
        mFinishedListener = listener;
    }

    public void setNormalScale(float x, float y) {
        mScaleNormalX = x;
        mScaleNormalY = y;
    }

    public void setEndAlpha() {
        mAlpha = 0;
        invalidate();
    }

    public void setEmptyOffsetMove(float x, float y) {
        mOffsetEmptyMoveX = x;
        mOffsetEmptyMoveY = y;
    }

    public void setEndToNormal(boolean endToNormal) {
        isEndToNormal = endToNormal;
    }

    public float getImageScale() {
        return mScale;
    }

    public void finishAnimationCallBack() {
        mMoveX = -mWidth / 2 + mWidth * mScale / 2;

        mMoveY = -mHeight / 2 + mHeight * mScale / 2;
        invalidate();
    }
}
