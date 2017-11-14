package com.wingsofts.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wingsofts.dragphotoview.DragUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView1;

    private ImageView mImageView2;

    private ImageView mImageView3;

    private ImageView mImageView4;

    private ImageView mImageView5;

    private ImageView mImageView6;

    private List<String> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mImageView1 = (ImageView) findViewById(R.id.imageView1);
        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mImageView3 = (ImageView) findViewById(R.id.imageView3);
        mImageView4 = (ImageView) findViewById(R.id.imageView4);
        mImageView5 = (ImageView) findViewById(R.id.imageView5);
        mImageView6 = (ImageView) findViewById(R.id.imageView6);
        onClick();

        mDataList.add("http://b.hiphotos.baidu.com/zhidao/pic/item/77c6a7efce1b9d16249b0023f5deb48f8c546410.jpg");
        mDataList.add("http://www.tumukeji.com/images/upload/imageArticle/1299182493.jpg");
        mDataList.add("http://dasouji.com/wp-content/uploads/2015/07/%E9%95%BF%E8%8A%B1%E5%9B%BE-1.jpg");
        mDataList.add("http://osvlwlt4g.bkt.clouddn.com/17-11-10/17291576.jpg");
        mDataList.add("http://osvlwlt4g.bkt.clouddn.com/17-11-14/58156439.jpg");
        mDataList.add("http://osvlwlt4g.bkt.clouddn.com/17-11-14/73848927.jpg");

        RequestOptions requestOptions = new RequestOptions();
        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mDataList.get(0)).into(mImageView1);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mDataList.get(1)).into(mImageView2);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mDataList.get(2)).into(mImageView3);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mDataList.get(3)).into(mImageView4);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mDataList.get(4)).into(mImageView5);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mDataList.get(4)).into(mImageView6);


    }

    public void onClick() {
        mImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DragUtils.goToDragPhotoView(MainActivity.this, v, mDataList, 0,
                        new ImageLoaderListener(), new LongClickImageListener(mDataList));
            }
        });

        mImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DragUtils.goToDragPhotoView(MainActivity.this, v, mDataList, 1,
                        new ImageLoaderListener(), new LongClickImageListener(mDataList));
            }
        });

        mImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DragUtils.goToDragPhotoView(MainActivity.this, v, mDataList, 2,
                        new ImageLoaderListener(), new LongClickImageListener(mDataList));
            }
        });

        mImageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DragUtils.goToDragPhotoView(MainActivity.this, v, mDataList, 3,
                        new ImageLoaderListener(), new LongClickImageListener(mDataList));
            }
        });
        mImageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DragUtils.goToDragPhotoView(MainActivity.this, view, mDataList, 4,
                        new ImageLoaderListener(), new LongClickImageListener(mDataList));
            }
        });
        mImageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DragUtils.goToDragPhotoView(MainActivity.this, view, mDataList, 5,
                        new ImageLoaderListener(), new LongClickImageListener(mDataList));
            }
        });
    }
}
