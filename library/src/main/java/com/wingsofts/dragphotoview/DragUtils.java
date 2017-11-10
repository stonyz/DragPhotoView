package com.wingsofts.dragphotoview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoshuyu on 2017/11/10.
 */

public class DragUtils {

    public static final String IMAGE_STRING_LIST = "IMAGE_STRING_LIST";
    public static final String SHOW_POSITION = "SHOW_POSITION";
    public static final String TOP = "TOP";
    public static final String LEFT = "LEFT";
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT = "HEIGHT";
    public static final String LONG_CLICK_LISTENER = "LONG_CLICK_LISTENER";
    public static final String IMAGE_LOAD_LISTENER = "IMAGE_LOAD_LISTENER";


    public static void goToDragPhotoView(final Activity context, View imageView, final List<String> imageList, int currImageIndex,
                                         OnImageLoaderListener imageLoaderListener, DragOnLongClickListener dragOnLongClickListener) {
        Intent intent = new Intent(context, DragPhotoViewPagerActivity.class);
        int location[] = new int[2];
        imageView.getLocationOnScreen(location);
        intent.putExtra(DragUtils.LEFT, location[0]);
        intent.putExtra(DragUtils.TOP, location[1]);
        intent.putExtra(DragUtils.HEIGHT, imageView.getHeight());
        intent.putExtra(DragUtils.WIDTH, imageView.getWidth());
        intent.putExtra(DragUtils.IMAGE_STRING_LIST, (ArrayList) imageList);
        intent.putExtra(DragUtils.SHOW_POSITION, currImageIndex);
        intent.putExtra(DragUtils.LONG_CLICK_LISTENER, dragOnLongClickListener);
        intent.putExtra(DragUtils.IMAGE_LOAD_LISTENER, imageLoaderListener);
        context.startActivity(intent);
        context.overridePendingTransition(0, 0);
    }


    public static void goToPhotoView(final Activity context, final List<String> imageList, int currImageIndex,
                                                    OnImageLoaderListener imageLoaderListener, DragOnLongClickListener dragOnLongClickListener) {
        Intent intent = new Intent(context, PhotoViewPagerActivity.class);
        intent.putExtra(DragUtils.IMAGE_STRING_LIST, (ArrayList) imageList);
        intent.putExtra(DragUtils.SHOW_POSITION, currImageIndex);
        intent.putExtra(DragUtils.LONG_CLICK_LISTENER, dragOnLongClickListener);
        intent.putExtra(DragUtils.IMAGE_LOAD_LISTENER, imageLoaderListener);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
