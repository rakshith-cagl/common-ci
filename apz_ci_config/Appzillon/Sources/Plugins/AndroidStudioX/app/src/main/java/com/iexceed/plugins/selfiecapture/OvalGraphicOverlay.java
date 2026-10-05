package com.iexceed.plugins.selfiecapture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.iexceed.appzillonapp.R;


import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceOverlayColor;
import static com.iexceed.plugins.selfiecapture.LivePreviewActivity.ovalFrame;

public class OvalGraphicOverlay extends View {
    Context mContext;
    Paint outerFillColor;
    FrameLayout frameLayout;

    public OvalGraphicOverlay(Context context) {
        super(context);
        mContext=context;

    }

    public OvalGraphicOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rect = new RectF(ovalFrame.getLeft(), ovalFrame.getTop(), ovalFrame.getRight(), ovalFrame.getBottom());
        outerFillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerFillColor.setColor( Color.parseColor(faceOverlayColor));
        outerFillColor.setStyle(Paint.Style.FILL);
        outerFillColor.setAlpha(255);
        canvas.drawPaint(outerFillColor);
        outerFillColor.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawOval(rect, outerFillColor);
    }
}
