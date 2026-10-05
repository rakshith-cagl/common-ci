package com.scanlibrary;

/**
 * Created by Abhishek 28/10/2016
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

import com.iexceed.appzillonapp.R;

@SuppressLint("NewApi")
public class Result extends Fragment {

private View view;
    
    private ImageView scannedImageView;
    
    private Button doneButton;
    
    private Button moreButton;
    
    private Bitmap original;
    
    private Button originalButton;
    
    private Button MagicColorButton;
    
    private Button grayModeButton;
    
    private Button bwButton;
    
    private Bitmap transformed;
    
    private AppzImageScanner scanner;

    public Result() {
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof AppzImageScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (AppzImageScanner) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.doc_result, null);
        init();
        return view;
    }

    private void init() {
        scannedImageView = (ImageView) view.findViewById(R.id.scannedImage);
        originalButton = (Button) view.findViewById(R.id.original);
        originalButton.setOnClickListener(new OriginalButtonClickListener());
        MagicColorButton = (Button) view.findViewById(R.id.magicColor);
        MagicColorButton.setOnClickListener(new MagicColorButtonClickListener());
        grayModeButton = (Button) view.findViewById(R.id.grayMode);
        grayModeButton.setOnClickListener(new GrayButtonClickListener());
        bwButton = (Button) view.findViewById(R.id.BWMode);
        bwButton.setOnClickListener(new BWButtonClickListener());
        Bitmap bitmap = getBitmap();
        setScannedImage(bitmap);
        doneButton = (Button) view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new DoneButtonClickListener());
        
        moreButton = (Button) view.findViewById(R.id.addMore);
       // moreButton.setOnClickListener(new AddMoreClickListener());
    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            original = ScanActivity.getBitmap(uri);
            getActivity().getContentResolver().delete(uri, null, null);
            return original;
        } catch (IOException e) {
            
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanActivity.SCANNED_RESULT);
        return uri;
    }

    public void setScannedImage(Bitmap scannedImage) {
        scannedImageView.setImageBitmap(scannedImage);
    }

    private class DoneButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Bitmap bitmap = transformed;
            if (bitmap == null) {
                bitmap = original;
            }
            scanner.onResult(bitmap);
            //original.recycle();
            
        }
    }
    
    private class BWButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            transformed = ((ScanActivity) getActivity()).getBWBitmap(original);
            scannedImageView.setImageBitmap(transformed);
        }
    }

    private class MagicColorButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            transformed = ((ScanActivity) getActivity()).getMagicColorBitmap(original);
            scannedImageView.setImageBitmap(transformed);
        }
    }

    private class OriginalButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            transformed = original;
            scannedImageView.setImageBitmap(original);
        }
    }

    private class GrayButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            transformed = ((ScanActivity) getActivity()).getGrayBitmap(original);
            scannedImageView.setImageBitmap(transformed);
        }
    }
    
//    private class AddMoreClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//        	Bitmap bitmap = transformed;
//            if (bitmap == null) {
//                bitmap = original;
//            }
//        	scanner.addMore(bitmap);
//        	
////        	if (original != null) {
////        		original.recycle();
////        		original = null;
////        	}
//        }
//    }
    
//    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
//        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight()+bmp2.getHeight()+10, bmp1.getConfig());
//        Canvas canvas = new Canvas(bmOverlay);
//        canvas.drawBitmap(bmp1, new Matrix(), null);
//        canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
//        return bmOverlay;
//    }
    

}
