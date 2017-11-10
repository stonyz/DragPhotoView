package com.wingsofts.myapplication;

import android.content.Context;
import android.widget.Toast;

import com.wingsofts.dragphotoview.DragOnLongClickListener;

import java.io.Serializable;
import java.util.List;

public class LongClickImageListener implements DragOnLongClickListener, Serializable {

    private List<String> imageList;


    public LongClickImageListener(List<String> imageList) {
        this.imageList = imageList;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    @Override
    public void onLongClick(Context context, int i) {
        Toast.makeText(context, "长按 " + imageList.get(i), Toast.LENGTH_SHORT).show();
    }
}
