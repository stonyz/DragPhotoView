package com.wingsofts.dragphotoview;

import android.animation.Animator;
import android.view.View;
import android.view.ViewTreeObserver;

import android.view.WindowManager;

import com.github.chrisbanes.photoview.PhotoView;

/**
 * ViewPager模式下的可拖拽的预览Activity
 */
public class DragPhotoViewPagerActivity extends PhotoViewPagerActivity {

    private DragPhotoViewPagerHelper mPagerHelper = new DragPhotoViewPagerHelper();

    private Animator.AnimatorListener mFinishListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            exitFullWhenFinish();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            finishRightNow();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    protected void getIntentLogic() {
        super.getIntentLogic();
    }

    @Override
    protected int getPhotoViewLayoutId() {
        return R.layout.item_viewpager;
    }

    @Override
    protected void initPhotoViewList() {
        mPhotoViews = new DragPhotoView[mImageList.size()];
    }

    @Override
    protected void initPhotoViewLogic(PhotoView photo, final int position) {
        final DragPhotoView photoView = (DragPhotoView) photo;
        photoView.OnPreViewFinishedListener(new DragPhotoView.OnPreViewFinishedListener() {
            @Override
            public void onMoveDown(DragPhotoView view) {

            }

            @Override
            public void onFinish(DragPhotoView view, float mx, float my, float vw, float vh) {
                if (position != mCurrImagePosition) {
                    finishRightNow();
                    return;
                }
                exitFullWhenFinish();
                mPagerHelper.animationExit(view, mx, my, vw, vh, mFinishListener);
            }

            @Override
            public void onNormal(DragPhotoView view) {

            }
        });

        photoView.setOnTapListener(new DragPhotoView.OnTapClickListener() {
            @Override
            public void onTap(DragPhotoView view) {
                exitWithAnimation(mViewPager.getCurrentItem());
            }
        });

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mLongClick != null && photoView.getImageScale() >= 1) {
                    mLongClick.onLongClick(view.getContext(), position);
                }
                return false;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mViewPager.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        mPagerHelper.initDragParams(getIntent(), (DragPhotoView[]) mPhotoViews, mCurrImagePosition);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mPagerHelper.isDragAnimationExit()) {
            return;
        }
        mPhotoViews[mViewPager.getCurrentItem()].setScale(1, true);
        exitWithAnimation(mViewPager.getCurrentItem());
    }

    private void exitWithAnimation(int position) {
        if (mPagerHelper.isDragAnimationExit()) {
            return;
        }
        if (position != mCurrImagePosition) {
            finishRightNow();
            return;
        }
        mPagerHelper.exitLogic((DragPhotoView[]) mPhotoViews, position, mFinishListener);
    }
}
