package com.iexceed.plugins.autocapturedocument;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.images.Size;
import com.iexceed.appzillonapp.R;

import static com.iexceed.appzillonapp.R.*;
import static com.iexceed.appzillonapp.R.color.object_confirmed_bg_gradient_end;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mFontColor;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mOverlayColor;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.cardFrame;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.screenHeight;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.screenWidth;

public class FrameGraphicOverlay extends View {
    Context mContext;
    Paint outerFillColor;
    FrameLayout frameLayout;
   // Xfermode mode=new PorterDuffXfermode(PorterDuff.Mode.CLEAR);


    public FrameGraphicOverlay(Context context){
        super(context);
    }

    public FrameGraphicOverlay(Context context,AttributeSet attrs) {
        super(context,attrs);
        mContext=context;
        frameLayout=cardFrame;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        screenWidth=(float)getWidth();
        screenHeight=(float)getHeight();
      //  RectF rect = new RectF(cardFrame.getLeft()*widthScaleFactor, cardFrame.getTop()*heightScaleFactor, cardFrame.getRight()*widthScaleFactor, cardFrame.getBottom()*heightScaleFactor);
        RectF rect = new RectF(cardFrame.getLeft(), cardFrame.getTop(), cardFrame.getRight(), cardFrame.getBottom());
        outerFillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(mOverlayColor.equalsIgnoreCase("")) {
            outerFillColor.setColor(ContextCompat.getColor(mContext, color.object_detected_bg_gradient_end));
        }else {
            outerFillColor.setColor( Color.parseColor(mOverlayColor));
        }
        outerFillColor.setStyle(Paint.Style.FILL);
        canvas.drawPaint(outerFillColor);
        outerFillColor.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(rect, outerFillColor);
    }
}
