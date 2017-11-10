package com.wingsofts.dragphotoview;


import android.content.Context;

import java.io.Serializable;

/**
 * 长按监听接口
 */
public interface DragOnLongClickListener extends Serializable {
    void onLongClick(Context context, int position);
}
