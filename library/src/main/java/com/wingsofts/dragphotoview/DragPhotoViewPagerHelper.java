package com.wingsofts.dragphotoview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;

/**
 * viewpager 帮助类
 * Created by guoshuyu on 2017/11/8.
 */

public class DragPhotoViewPagerHelper {

    private int mOriginLeft;
    private int mOriginTop;
    private int mOriginHeight;
    private int mOriginWidth;
    private int mOriginCenterX;
    private int mOriginCenterY;
    private int mAnimationTime = 300;
    private float mTargetHeight;
    private float mTargetWidth;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;
    private boolean isDragAnimationExit;


    public void initDragParams(Intent intent, DragPhotoView photo[], int position) {
        int originLeft = intent.getIntExtra(DragUtils.LEFT, 0);
        int originTop = intent.getIntExtra(DragUtils.TOP, 0);
        int originHeight = intent.getIntExtra(DragUtils.HEIGHT, 0);
        int originWidth = intent.getIntExtra(DragUtils.WIDTH, 0);
        int originCenterX = originLeft + originWidth / 2;
        int originCenterY = originTop + originHeight / 2;

        mAnimationTime = intent.getIntExtra(DragUtils.ANIMATION_TIME, 300);
        mOriginLeft = originLeft;
        mOriginTop = originTop;
        mOriginHeight = originHeight;
        mOriginWidth = originWidth;
        mOriginCenterX = originCenterX;
        mOriginCenterY = originCenterY;

        int[] location = new int[2];

        final DragPhotoView photoView = photo[position];
        photoView.getLocationOnScreen(location);
        float targetHeight = (float) photoView.getHeight();
        float targetWidth = (float) photoView.getWidth();
        float scaleX = (float) originWidth / targetWidth;
        float scaleY = (float) originHeight / targetHeight;

        mTargetHeight = targetHeight;
        mTargetWidth = targetWidth;
        mScaleY = scaleY;
        mScaleX = scaleX;

        float targetCenterX = location[0] + targetWidth / 2;
        float targetCenterY = location[1] + targetHeight / 2;

        float translationX = originCenterX - targetCenterX;
        float translationY = originCenterY - targetCenterY;
        photoView.setTranslationX(translationX);
        photoView.setTranslationY(translationY);

        mTranslationY = translationY;
        mTranslationX = translationX;

        photoView.setScaleX(scaleX);
        photoView.setScaleY(scaleY);

        animationEnter(photo, position);

        for (int i = 0; i < photo.length; i++) {
            photo[i].setMinScale(Math.max(scaleX, scaleY));
        }
    }


    public void animationEnter(DragPhotoView photo[], int position) {
        final DragPhotoView photoView = photo[position];
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(photoView.getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(mAnimationTime);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(photoView.getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(mAnimationTime);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(mAnimationTime);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(mAnimationTime);
        scaleXAnimator.start();
    }


    public void animationExit(final DragPhotoView view, float x, float y, float w, float h,
                              final Animator.AnimatorListener listener) {
        view.finishAnimationCallBack();
        //计算photoView的x和y的动画起始位置，因为拖拽其实只是改变内部绘制图像，不改变xy
        final float viewX = mTargetWidth / 2 + x - mTargetWidth * Math.max(mScaleX, mScaleY) / 2;
        final float viewY = mTargetHeight / 2 + y - mTargetHeight * Math.max(mScaleX, mScaleY) / 2;
        view.setX(viewX);
        view.setY(viewY);

        //计算移动的目标位置，其实就是originTop和originLeft
        float centerX = view.getX() + mOriginWidth / 2;
        float centerY = view.getY() + mOriginHeight / 2;

        float translateX = mOriginCenterX - centerX;
        float translateY = mOriginCenterY - centerY;

        //根据图片的大小，计算比例，通过比例计算出图片的空白位置
        //比如横图，其实在显示中存在上下空白，所以需要计算出空白的的大小，得到顶部偏移量
        //在计算偏移量的缩放，得到实际偏移
        //根据图片
        int width;
        int height;

        float currentScaleWidth = mTargetWidth;
        float currentScaleHeight = mTargetHeight;
        float offsetEmpty;
        final float scaleOffsetEmptyX;
        final float scaleOffsetEmptyY;
        if (view.getDrawable() != null) {
            width = view.getDrawable().getBounds().width();
            height = view.getDrawable().getBounds().height();
            if (width > 0 && height > 0) {
                if (width > height) {
                    //此处以横图充满的前提，计算出实际高度
                    float currentHeight = mTargetWidth * height / width;
                    //根据实际高度，计算出实际的空白缩放偏移
                    offsetEmpty = ((mTargetHeight - currentHeight) / 2 * Math.max(mScaleX, mScaleY));
                    translateY -= offsetEmpty;
                    currentScaleHeight = currentHeight;
                    scaleOffsetEmptyX = 0;
                    scaleOffsetEmptyY = offsetEmpty;
                } else if (width < height){
                    //此处以竖图充满的前提，计算出实际宽度
                    float currentWidth = mTargetHeight * width / height;
                    offsetEmpty = ((mTargetWidth - currentWidth) / 2 * Math.max(mScaleX, mScaleY));
                    //根据实际宽度，计算出实际的空白缩放偏移
                    translateX -= offsetEmpty;
                    currentScaleWidth = currentWidth;
                    scaleOffsetEmptyX = offsetEmpty;
                    scaleOffsetEmptyY = 0;
                } else {
                    //如果为方形图片
                    float currentWidth = mTargetWidth;
                    offsetEmpty = ((mTargetHeight - currentWidth) / 2 * Math.max(mScaleX, mScaleY));
                    //根据实际宽度，计算出实际的空白缩放偏移
                    translateY -= offsetEmpty;
                    currentScaleHeight = currentWidth;
                    scaleOffsetEmptyX = 0;
                    scaleOffsetEmptyY = offsetEmpty;
                }
            } else {
                listener.onAnimationEnd(null);
                return;
            }
        } else {
            listener.onAnimationEnd(null);
            return;
        }

        isDragAnimationExit = true;
        //算出恢复到原本大小需要的比例
        final float scaleTargetWidth = mOriginWidth / currentScaleWidth;
        final float scaleTargetHeight = mOriginHeight / currentScaleHeight;
        //算出恢复到原本大小，缩放过程中，自身大小变化是否在图片空白区域内
        //如果变大部分超过了空白区域，需要计算偏差
        final float tmpTargetViewHeight = mTargetHeight * scaleTargetHeight;
        final float tmpTargetViewWidth = mTargetWidth * scaleTargetWidth;
        final float offsetX = (tmpTargetViewWidth - mOriginWidth) / 2 - scaleOffsetEmptyX;
        final float offsetY = (tmpTargetViewHeight - mOriginHeight) / 2 - scaleOffsetEmptyY;
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setNormalScale(scaleTargetWidth, scaleTargetHeight);
                view.setEmptyOffsetMove(offsetX, offsetY);
                view.setEndToNormal(true);
                view.invalidate();
            }
        }, mAnimationTime - 50);

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(view.getX(), view.getX() + translateX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(mAnimationTime);
        translateXAnimator.start();
        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(view.getY(), view.getY() + translateY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setY((Float) valueAnimator.getAnimatedValue());
            }
        });

        translateYAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                listener.onAnimationEnd(animator);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        translateYAnimator.setDuration(mAnimationTime);
        translateYAnimator.start();

    }

    public void exitLogic(DragPhotoView photo[], int position, final Animator.AnimatorListener listener) {

        isDragAnimationExit = true;
        final DragPhotoView photoView = photo[position];
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, mTranslationX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(mAnimationTime);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, mTranslationY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(mAnimationTime);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mScaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(mAnimationTime);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mScaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                listener.onAnimationStart(animator);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                listener.onAnimationEnd(animator);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(mAnimationTime);
        scaleXAnimator.start();
    }

    public boolean isDragAnimationExit() {
        return isDragAnimationExit;
    }

}
