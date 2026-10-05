package com.scanlibrary;

/**
 * Created by Abhishek 28/10/2016
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iexceed.appzillonapp.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressLint("NewApi")
public class PickImage extends Fragment {

    private View view;
    
    private Uri fileUri;
    
    private AppzImageScanner scanner;

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
    	openCamera();
        return view;
    }

   
    public void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createImageFile();
        file.getParentFile().mkdirs();
        fileUri = FileProvider.getUriForFile(ScanActivity.context, BuildConfig.APPLICATION_ID, file);
        if (file != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(cameraIntent, ScanActivity.START_CAMERA_REQUEST_CODE);
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(ScanActivity.IMAGE_PATH, "IMG_" + timeStamp +".jpg");
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("", "onActivityResult" + resultCode);
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            try {
            	if(requestCode == ScanActivity.START_CAMERA_REQUEST_CODE)
            		bitmap = getBitmap(fileUri);
            } catch (Exception e ) {
               
            }
        }else{
        	getActivity().finish();
        }
        if (bitmap != null) {
            postImagePick(bitmap);
        }
    }

    protected void postImagePick(Bitmap bitmap) {
        Uri uri = ScanActivity.getUri(bitmap);
       // bitmap.recycle();
        scanner.onBitmapSelect(uri);
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original = BitmapFactory.decodeFileDescriptor( fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }
}
