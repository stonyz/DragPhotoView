package com.wingsofts.dragphotoview;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ViewPager模式下的PhotoView
 */
public class PhotoViewPagerActivity extends AppCompatActivity {

    protected ViewPager mViewPager;

    protected LinearLayout mBottomBar;

    protected List<String> mImageList;

    protected PhotoView[] mPhotoViews;

    protected DragOnLongClickListener mLongClick;

    protected OnImageLoaderListener mOnImageLoaderListener;

    protected int mCurrImagePosition;

    protected int mPreImageBottomBarPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drag_photo);

        getIntentLogic();
        initView();
        initListener();
        initBottomBar();
    }

    protected void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        // 初始化页码标记栏
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        initPhotoViewList();
        if (mImageList.size() <= 1) {
            mBottomBar.setVisibility(View.INVISIBLE);
        }
    }

    protected void getIntentLogic() {
        mImageList = getIntent().getStringArrayListExtra(DragUtils.IMAGE_STRING_LIST);
        mCurrImagePosition = getIntent().getIntExtra(DragUtils.SHOW_POSITION, 0);
        mPreImageBottomBarPosition = mCurrImagePosition;
        mLongClick = (DragOnLongClickListener) getIntent().getSerializableExtra(DragUtils.LONG_CLICK_LISTENER);
        mOnImageLoaderListener = (OnImageLoaderListener) getIntent().getSerializableExtra(DragUtils.IMAGE_LOAD_LISTENER);
    }

    protected void initListener() {
        for (int i = 0; i < mPhotoViews.length; i++) {
            mPhotoViews[i] = (PhotoView) View.inflate(this, getPhotoViewLayoutId(), null);
            initPhotoViewLogic(mPhotoViews[i], i);
        }
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImageList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mPhotoViews[position]);
                mOnImageLoaderListener.load(getApplicationContext(), mPhotoViews[position], mImageList.get(position));
                return mPhotoViews[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mPhotoViews[position]);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

        mViewPager.setCurrentItem(mCurrImagePosition);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 切换页码标记栏原点状态
                switchBottomBarIndex(mPreImageBottomBarPosition, false);
                switchBottomBarIndex(position, true);
                mPreImageBottomBarPosition = position;
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });
    }

    protected int getPhotoViewLayoutId() {
        return R.layout.item_normal_viewpager;
    }


    protected void initPhotoViewList() {
        mPhotoViews = new PhotoView[mImageList.size()];
    }

    protected void initPhotoViewLogic(PhotoView photo, final int position) {
        photo.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finishRightNow();
            }
        });
        photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClick != null) {
                    mLongClick.onLongClick(v.getContext(), position);
                }
                return false;
            }
        });
    }

    private void initBottomBar() {
        ImageView image ;
        int margin = (int) getResources().getDimension(R.dimen.barImage_distance);
        for (int i = 0; i < mImageList.size(); i++) {
            image = new ImageView(this);
            image.setImageResource(R.drawable.normal);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(margin, 0, 0, 0);
            image.setTag("p" + i);
            mBottomBar.addView(image, layoutParams);
        }
        switchBottomBarIndex(mCurrImagePosition, true);
    }

    private void switchBottomBarIndex(final int position, final boolean selected) {
        ImageView imageView = (ImageView) mBottomBar.findViewWithTag("p" + position);
        if (null != imageView) {
            imageView.setImageResource(selected ? R.drawable.selected : R.drawable.normal);
        }
    }

    protected void finishRightNow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public void onBackPressed() {
        finishRightNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLongClick = null;
        mOnImageLoaderListener = null;
    }

}
