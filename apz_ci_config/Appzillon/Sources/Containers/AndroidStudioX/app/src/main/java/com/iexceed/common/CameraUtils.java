package com.iexceed.common;

import android.app.Activity;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.iexceed.appzillonapp.AppzillonMainScreen.ASSET_APP_LOC;
import static com.iexceed.appzillonapp.AppzillonMainScreen.SANDBOX_LOC;

public class CameraUtils {
    public static Bitmap compressImage(byte[] realData, String fileFormat,
                                       boolean uncompressed, int HTMLWIDTH, int HTMLHEIGHT, boolean isCrop,
                                       String orignalFilePath) {
        FileOutputStream out = null;
        Bitmap processingBitmap = null;
        String originalimagepath = getFilename("preCompression", fileFormat); // creating
        // appzillonTemp
        // folder



        try {
            // Write to SD Card
//            FileOutputStream outStream = new FileOutputStream(""+originalimagepath);
            FileOutputStream outStream = new FileOutputStream(AppzillonUtils.validatePath(originalimagepath,null));
            outStream.write(realData);
            outStream.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {

        }

        String filePath = getRealPathFromURI(originalimagepath);

        BitmapFactory.Options options = new BitmapFactory.Options();

        // by setting this field as true, the actual bitmap pixels are not
        // loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        if (!uncompressed) {
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            // max Height and width values of the compressed image is taken as
            // 816x612

            if (HTMLWIDTH == 0 && HTMLHEIGHT == 0) {
                // max Height and width values of the compressed image is taken as
                // 816x612
                // Abhishek Fix for 9826 START
                float maxHeight = 816.0f;
                float maxWidth = 612.0f;
                float imgRatio = actualWidth / actualHeight;
                float maxRatio = maxWidth / maxHeight;
                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    if (imgRatio < maxRatio) {
                        imgRatio = maxHeight / actualHeight;
                        actualWidth = (int) (imgRatio * actualWidth);
                        actualHeight = (int) maxHeight;
                    } else if (imgRatio > maxRatio) {
                        imgRatio = maxWidth / actualWidth;
                        actualHeight = (int) (imgRatio * actualHeight);
                        actualWidth = (int) maxWidth;
                    } else {
                        actualHeight = (int) maxHeight;
                        actualWidth = (int) maxWidth;

                    }
                }
            } else {
                // Abhishek : In place of taking hardcoded values now size is
                // dependent upon compression level
                double sizeRatio = (double) actualWidth / (double) actualHeight;

                if (HTMLWIDTH > 0 && HTMLHEIGHT == 0) {

                    actualWidth = HTMLWIDTH;
                    actualHeight = (int) (actualWidth / (sizeRatio));

                } else if (HTMLWIDTH == 0 && HTMLHEIGHT > 0) {
                    actualHeight = HTMLHEIGHT;
                    actualWidth = (int) (actualHeight * sizeRatio);

                } else {
                    actualHeight = HTMLHEIGHT;
                    actualWidth = HTMLWIDTH;
                }
            }
            processingBitmap=imageScaling(bmp,filePath,actualWidth,actualHeight);

        } else {
            processingBitmap = BitmapFactory.decodeByteArray(realData, 0, realData.length);
        }

        // check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            // exif = new ExifInterface(filePath);
            // Bug #9476 Start
            if (isCrop) {
                exif = new ExifInterface(originalimagepath);
            } else {
                exif = new ExifInterface(orignalFilePath);
            }
         processingBitmap=exifOrientation(exif,processingBitmap);
        } catch (IOException e) {

        }

        deleteDir(new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC,
                "appzillonTemp")); // deleting appzillonTemp folder
        return processingBitmap;

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    // return false;
                }
            }
        }

        // The directory is now empty so delete it
        boolean deleted = dir.delete();
        boolean create = dir.mkdir();
        return deleted;
    }

    public static String getFilename(String type, String mFileFormat) {
        File file = null;
        if (type.equalsIgnoreCase("preCompression")) {
            file = new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC,
                    "appzillonTemp/originalImages");
        } else {
            file = new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC,
                    "appzillonTemp/compressedImages");
        }

        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + mFileFormat);
        return uriSting;

    }

    static String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = JavaScriptInterface.activity.getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static byte[] getBytesFromBitmap(Bitmap croppedImageToconvert,
                                            int cmpLevel, Bitmap.CompressFormat mCompressFormat) {
        // TODO Auto-generated method stub
        byte[] tempbyte = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            croppedImageToconvert.compress(mCompressFormat, cmpLevel, baos); // mBitmap
            // is
            // the
            // bitmap
            // object
            byte[] b = baos.toByteArray();

            baos.close();
            baos = null;

            tempbyte = b;
        } catch (IOException e) {
            // TODO Auto-generated catch block

        }
        return tempbyte;
    }

    public static Bitmap compressSelfieBmpByHeightWeigth(Bitmap bitmap, int HTMLWIDTH, int HTMLHEIGHT, Activity mActivity){
        try{
            Bitmap scaledBitmap = null;
            String mTimeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());

            String mImageName = "snap_"+mTimeStamp+".jpg";

            ContextWrapper wrapper = new ContextWrapper(mActivity);

            File file = wrapper.getDir("Images",MODE_PRIVATE);

            file = new File(file, "snap_"+ mImageName+".jpg");
            int actualH=bitmap.getHeight();
            int actualW=bitmap.getWidth();


            if(HTMLWIDTH<=HTMLHEIGHT){
                HTMLHEIGHT=(HTMLWIDTH*actualH)/actualW;
            }else {
                HTMLWIDTH= (HTMLHEIGHT*actualW)/actualH;
            }
            try{

                OutputStream stream = null;
                stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                stream.flush();
                stream.close();

            }catch (IOException e)
            {
                e.printStackTrace();
            }
            scaledBitmap=  imageScaling(bitmap,file.getAbsolutePath(),HTMLWIDTH,HTMLHEIGHT);
            ExifInterface exif;
            exif = new ExifInterface(file.getAbsolutePath());
            scaledBitmap=exifOrientation(exif,scaledBitmap);
            return scaledBitmap;

        }catch(Exception e){
            return bitmap;
        }
    }

  public static  Bitmap imageScaling(Bitmap bitmap,String filePath,int HTMLWIDTH,int HTMLHEIGHT){
        try {
            //            Uri mImageUri = Uri.parse(file.getAbsolutePath());
            Bitmap scaledBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

//      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, HTMLWIDTH, HTMLHEIGHT);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(HTMLWIDTH, HTMLHEIGHT, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = HTMLWIDTH / (float) options.outWidth;
            float ratioY = HTMLHEIGHT / (float) options.outHeight;
            float middleX = HTMLWIDTH / 2.0f;
            float middleY = HTMLHEIGHT / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            return scaledBitmap;
        }catch(Exception e){
            return bitmap;
        }
    }
  public static   Bitmap  exifOrientation(ExifInterface exif,Bitmap scaledBitmap){
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                       Log.d("EXIF", "Exif: " + orientation);
                       Matrix matrix = new Matrix();
                      if (orientation == 6) {
                             matrix.postRotate(90);
                               Log.d("EXIF", "Exif: " + orientation);
                         } else if (orientation == 3) {
                             matrix.postRotate(180);
                              Log.d("EXIF", "Exif: " + orientation);
                          } else if (orientation == 8) {
                             matrix.postRotate(270);
                              Log.d("EXIF", "Exif: " + orientation);
                         }
                       scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,true);
    return scaledBitmap;
    }

}
