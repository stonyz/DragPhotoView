package com.wingsofts.dragphotoview;


import android.content.Context;
import android.view.View;

import java.io.Serializable;


public interface OnImageLoaderListener extends Serializable {
    void load(Context context, View view, String url);
}
