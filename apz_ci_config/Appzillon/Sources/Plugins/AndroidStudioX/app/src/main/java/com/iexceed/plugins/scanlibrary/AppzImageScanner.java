package com.scanlibrary;

/**
 * Created by Abhishek 28/10/2016
 */

import android.graphics.Bitmap;
import android.net.Uri;

public interface AppzImageScanner {

    void onBitmapSelect(Uri uri);

    void onScanFinish(Uri uri);
    
    void onResult(Bitmap bmp);
    
//    void addMore(Bitmap bmp);
}
