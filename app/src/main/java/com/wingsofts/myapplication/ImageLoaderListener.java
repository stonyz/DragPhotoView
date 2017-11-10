package com.wingsofts.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wingsofts.dragphotoview.OnImageLoaderListener;

import java.io.Serializable;

/**
 * Created by guoshuyu on 2017/11/10.
 */

public class ImageLoaderListener implements OnImageLoaderListener, Serializable {

    @Override
    public void load(Context context, View view, String url) {
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(context.getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(url).into((ImageView) view);
    }
}
