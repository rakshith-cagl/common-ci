package com.scanlibrary;

/**
 * Created by Abhishek 28/10/2016
 */

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.util.Base64;
import android.view.Window;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.CameraUtils;
import com.iexceed.common.MediaUtils;
import com.iexceed.appzillonapp.BuildConfig;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.appzillonapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.iexceed.appzillonapp.AppzillonMainScreen.ASSET_APP_LOC;
import static com.iexceed.appzillonapp.AppzillonMainScreen.SANDBOX_LOC;
import static com.theartofdev.edmodo.cropper.CropImage.getActivityResult;

@SuppressLint("NewApi")
public class ScanActivity extends Activity implements AppzImageScanner {

	public static final int REQUEST_CAMERA = 1;

	public static final int REQUEST_STORAGE = 2;

	public final static int START_CAMERA_REQUEST_CODE = 3;

	public final static String SCANNED_RESULT = "scannedResult";

	public static String IMAGE_PATH = "";

	public final static String SELECTED_BITMAP = "selectedBitmap";

	private static final String TAG = null;

	public static Context context;

	private String fileName;

	private String crop;

	private int cmpLevel;

	private int HTMLWIDTH;

	private int HTMLHEIGHT;

	final int BUFFER_SIZE = 1024 * 8;

	private boolean setCropping;

	public static int OCRCODE = 4;

	static Activity activity;

	static WebView webView;

	static String callBackId;

	static {
		System.loadLibrary("opencv_java3");
		System.loadLibrary("Scanner");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.doc_scan);
		context = this;
		IMAGE_PATH = SANDBOX_LOC + File.separator + ASSET_APP_LOC + "scanSample";

		Intent intent = getIntent();
		String str = intent.getStringExtra("json");

		try {
			JSONObject jsonObj = new JSONObject(str);
			fileName = jsonObj.optString("fileName");
			crop = jsonObj.optString("crop");
			String compressionLevel = jsonObj.optString("quality");

			if (!compressionLevel.equals("")) {
				cmpLevel = Integer.parseInt(compressionLevel);
			} else {
				cmpLevel = 100;
			}
			String targetWidth = jsonObj.optString("targetWidth");
			if (!targetWidth.isEmpty() && targetWidth != null) {
				HTMLWIDTH = Integer.parseInt(targetWidth);
			}
			String targetHeight = jsonObj.optString("targetHeight");
			if (!targetHeight.isEmpty() && targetHeight != null) {
				HTMLHEIGHT = Integer.parseInt(targetHeight);
			}

			if (!crop.equalsIgnoreCase("") && crop.equalsIgnoreCase("Y")) {
				setCropping = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}


			init();

	}

	private void init() {
		PickImage fragment = new PickImage();
		android.app.FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.content, fragment);
		fragmentTransaction.commit();
	}

	@Override
	public void onBitmapSelect(Uri uri) {
		CropScannedImage fragment = new CropScannedImage();
		Bundle bundle = new Bundle();
		bundle.putParcelable(SELECTED_BITMAP, uri);
		fragment.setArguments(bundle);
		android.app.FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.content, fragment);
		fragmentTransaction.addToBackStack(CropScannedImage.class.toString());
		fragmentTransaction.commit();
	}

	@Override
	public void onScanFinish(Uri uri) {
		Result fragment = new Result();
		Bundle bundle = new Bundle();
		bundle.putParcelable(SCANNED_RESULT, uri);
		fragment.setArguments(bundle);
		android.app.FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.content, fragment);
		fragmentTransaction.addToBackStack(Result.class.toString());
		fragmentTransaction.commit();
	}

	@Override
	public void onResult(Bitmap bmp) {

		if (bmp != null) {
			String copyImagePath = copyScannedImage(bmp);
			if (setCropping) {
				startCropImageActivity(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, new File(copyImagePath)));
			} else {
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				Bitmap finalBitmap = BitmapFactory.decodeFile(copyImagePath,
						bmOptions);
				byte[] finalBitmapBytes = CameraUtils.getBytesFromBitmap(
						finalBitmap, cmpLevel, Bitmap.CompressFormat.JPEG);
				if(ApzDocScannerPlugin.OCR) {
					String OCR_text = ApzOCR.detectText(finalBitmapBytes);
					JSONObject sJson = new JSONObject();
					try {
						sJson.put("OCR_text",OCR_text );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
					}
					ApzPluginUtil.sendSuccess(callBackId,sJson,false,activity,webView,true);
					finish();

				}

				Bitmap finalCompressedImage = CameraUtils.compressImage(
						finalBitmapBytes, ".jpg", true, HTMLWIDTH, HTMLHEIGHT,
						setCropping, copyImagePath);

				processBitmap(finalCompressedImage);
			}

		}else{
			Intent in = new Intent();
			setResult(RESULT_CANCELED, in);
			finish();
		}

	}

	private void startCropImageActivity(Uri imageUri) {
		try {
			CropImage.activity(imageUri)
					.setGuidelines(CropImageView.Guidelines.ON)
					.setMultiTouchEnabled(true)
					.start(this);
		} catch (Exception e) {
			ApzLogger.i("PROBKEM", "" + e.toString());
		}
	}

	private String copyScannedImage(Bitmap bmp) {
		String imagePath = null;
		FileOutputStream out = null;
		String originalimagepath = CameraUtils.getFilename("preCompression" ,".jpg"); // creating
		// appzillonTemp
		// folder
		byte[] finalBitmapBytes = CameraUtils.getBytesFromBitmap(bmp, cmpLevel,Bitmap.CompressFormat.JPEG);
		try {
			// Write to SD Card
			FileOutputStream outStream = new FileOutputStream(originalimagepath);
			outStream.write(finalBitmapBytes);
			outStream.close();
			imagePath = originalimagepath;

		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		} finally {

		}
		return imagePath;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.d("", "onActivityResult" + resultCode);
		if (resultCode == Activity.RESULT_OK) {
			try {
				if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
					CropImage.ActivityResult result = getActivityResult(data);

						BitmapFactory.Options bmOptions = new BitmapFactory.Options();
						// bmOptions.inJustDecodeBounds = true;
						Bitmap bitmap = BitmapFactory.decodeFile(result.getUri().getPath(),
								bmOptions);
						byte[] finalBitmapBytes = CameraUtils.getBytesFromBitmap(bitmap,
								cmpLevel, Bitmap.CompressFormat.JPEG);
					if(ApzDocScannerPlugin.OCR) {
						String OCR_text = ApzOCR.detectText(finalBitmapBytes);
						JSONObject sJson = new JSONObject();
						try {
							sJson.put("OCR_text",OCR_text );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							
						}
						ApzPluginUtil.sendSuccess(callBackId,sJson,false,activity,webView,true);
						finish();
					}

						Bitmap finalCompressedImage = CameraUtils.compressImage(
								finalBitmapBytes, ".jpg", true, HTMLWIDTH,
								HTMLHEIGHT, setCropping, result.getUri().getPath());

						processBitmap(finalCompressedImage);

				}/*else if(requestCode == OCRCODE){
					JSONObject sJson = new JSONObject();
					try {
						sJson.put("OCR_text", data.getStringExtra("text"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
					}
					ApzPluginUtil.sendSuccess(callBackId,sJson,false,activity,webView,true);

				}*/
			} catch (Exception e) {
				
			}
		}else{
			Intent in = new Intent();
			setResult(RESULT_CANCELED, in);
			finish();
		}

	}

	public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1,
			float x2, float y2, float x3, float y3, float x4, float y4);

	public native Bitmap getGrayBitmap(Bitmap bitmap);

	public native Bitmap getMagicColorBitmap(Bitmap bitmap);

	public native Bitmap getBWBitmap(Bitmap bitmap);

	public native float[] getPoints(Bitmap bitmap);

	public static Uri getUri(Bitmap bitmap) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(
				context.getContentResolver(), bitmap, "Title", null);
		return Uri.parse(path);
	}

	public static Bitmap getBitmap(Uri uri) throws IOException {
		Bitmap bitmap = MediaStore.Images.Media.getBitmap(
				context.getContentResolver(), uri);
		return bitmap;
	}

	public static void setWebView(WebView wv,Activity a,String c) {
		webView = wv;
		activity = a;
		callBackId = c;
	}

	private void processBitmap(Bitmap croppedandcompressedIMG) {

		String pathToImage = saveImage(croppedandcompressedIMG);
		try {
			if (pathToImage != null) {

				String base64Image = getBase64Image(croppedandcompressedIMG);
				if (base64Image != null) {
					//Natasha's changes 24-07-2017 for App Crashing
					/*Intent in = new Intent();
			    	in.putExtra("encodedImage", base64Image);
			    	setResult(RESULT_OK, in);*/
					JSONObject sJson = new JSONObject();
					try {
						sJson.put("encodedImage", base64Image);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
					}
					CameraUtils.deleteDir(new File(IMAGE_PATH));
					ApzPluginUtil.sendSuccess(callBackId,sJson,false,activity,webView,true);
			    	System.gc();
			        finish();
				} else {
					//Natasha's changes 24-07-2017 for App Crashing
					/*Intent in = new Intent();
					in.putExtra("error", "Exception in scanning image");
					setResult(RESULT_CANCELED, in);*/
					CameraUtils.deleteDir(new File(IMAGE_PATH));
					ApzPluginUtil.sendError(callBackId,"APZ-CNT-313",null,activity,webView,true);
				}

			} else {
				//Natasha's changes 24-07-2017 for App Crashing
				/*Intent in = new Intent();
				in.putExtra("error", "Exception in saving image");
				setResult(RESULT_CANCELED, in);*/

				ApzPluginUtil.sendError(callBackId,"APZ-CNT-313",null,activity,webView,true);
			}
			HTMLHEIGHT = 0;
			HTMLWIDTH = 0;
			System.gc();
			finish();
		} catch (Exception e) {
			//Natasha's changes 24-07-2017 for App Crashing
			/*Intent in = new Intent();
			in.putExtra("error", "Exception in base64 image");
			setResult(RESULT_CANCELED, in);*/
			ApzPluginUtil.sendError(callBackId,"APZ-CNT-328",null,activity,webView,true);
			System.gc();
			finish();
		}

	}

	private String saveImage(Bitmap bitmp) {

		String photoFile;
		String photoFilepath = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmyyhhmmss");
		String date = dateFormat.format(new Date());
		photoFile = fileName + date + ".jpg";
		try {

			File pictureFileDir = getDir("photo");
			if (pictureFileDir != null) {
				String filenameWithPath = pictureFileDir.getPath()
						+ File.separator + photoFile;
				File pictureFile = new File(filenameWithPath);
				if (pictureFile.exists()) {
					if (pictureFile.delete()) {
						// Abhishek 18 March 2015 to handle the crash while
						// sending broadcast in KITKAT and above START
						// getApplicationContext().sendBroadcast(new
						// Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://"+
						// Environment.getExternalStorageDirectory())));
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
							Intent mediaScanIntent = new Intent(
									Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
							Uri contentUri = Uri
									.parse("file://"
											+ Environment
													.getExternalStorageDirectory()); // out
																						// is
																						// your
																						// output
																						// file
							mediaScanIntent.setData(contentUri);
							this.sendBroadcast(mediaScanIntent);
						} else {
							sendBroadcast(new Intent(
									Intent.ACTION_MEDIA_MOUNTED,
									Uri.parse("file://"
											+ Environment
													.getExternalStorageDirectory())));
						}
						// Abhishek 18 March 2015 to handle the crash while
						// sending broadcast in KITKAT and above END
						try {

							boolean isCreated = pictureFile.createNewFile();

						} catch (IOException e) {
							
						} catch (OutOfMemoryError e) {
							//Natasha's changes 24-7-2017 for App crashing
							/*Intent in = new Intent();
							in.putExtra("error", e.getMessage());
							setResult(RESULT_CANCELED, in);*/
							ApzPluginUtil.sendError(callBackId,"APZ-CNT-313",null,activity,webView,true);
						}

					}
				}
				// File pictureFile = new File(filenameWithPath);
				FileOutputStream fos = new FileOutputStream(pictureFile);

				final BufferedOutputStream bos = new BufferedOutputStream(fos,
						BUFFER_SIZE);
				bitmp.compress(Bitmap.CompressFormat.JPEG, cmpLevel, bos);
				bos.flush();
				bos.close();
				fos.close();
				photoFilepath = filenameWithPath;

				MediaScannerConnection.scanFile(getApplicationContext(),
						new String[] { pictureFile.toString() }, null,
						new MediaScannerConnection.OnScanCompletedListener() {
							public void onScanCompleted(String path, Uri uri) {
//								ApzLogger.i(TAG, "ExternalStorage Scanned "
//										+ path + ":");
//								ApzLogger.i(TAG, "ExternalStorage -> uri="
//										+ uri);
							}
						});
			}

		}

		catch (FileNotFoundException e) {
			
			ApzLogger.e(TAG, e.getMessage());
		} catch (IOException e) {
			
			ApzLogger.e(TAG, e.getMessage());
		} catch (OutOfMemoryError e) {
			//Natasha's changes 24-7-2017 for App crashing
			/*Intent in = new Intent();
			in.putExtra("error", e.getMessage());
			setResult(RESULT_CANCELED, in);*/
			ApzPluginUtil.sendError(callBackId,"APZ-CNT-313",null,activity,webView,true);
		}
		return photoFilepath;

	}

	private File getDir(String loc) {
		if (MediaUtils.isSDCardPresent()) {
			// Abhishek 20 April 2015 Updated path to specific app sandbox START
			File sdDir = new File(AppzillonMainScreen.SANDBOX_LOC
					+ File.separator + AppzillonMainScreen.ASSET_APP_LOC+ "/" + loc);
			// Abhishek 20 April 2015 Updated path to specific app sandbox END
			try {
				if (sdDir.exists()) {
					return sdDir;
				} else {
					if (sdDir.mkdirs()) {
						return sdDir;
					} else {
						return null;
					}
				}
			} catch (Exception e) {
				
				ApzLogger.e(TAG, "Create directory failed : " + e.getMessage());
			}
		} else {

			//Natasha's changes 24-7-2017 for App crashing
			/*Intent in = new Intent();
			in.putExtra("sdcard_status",
					getResources().getString(R.string.sdcard_unavailable));
			setResult(RESULT_CANCELED, in);*/

			ApzPluginUtil.sendError(callBackId,"APZ-CNT-313",null,activity,webView,true);
		}

		return null;
	}

	private String getBase64Image(Bitmap bitmap) {
		String returnBase64 = null;
		try {
			// Abhishek Bug 2722 : to over come Out of memory exception
			// Abhishek 22 April 2015,Bug id 4703,COMMENTED to resize all the
			// images START
			// mBitmap = Bitmap.createScaledBitmap(mBitmap,
			// mImageView.getWidth(),mImageView.getHeight(), true);
			// Abhishek 22 April 2015,Bug id 4703,COMMENTED to resize all the
			// images END
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// bitmap.compress(Bitmap.CompressFormat.JPEG, cmpLevel, baos); //
			// mBitmap is the bitmap object
			bitmap.compress(Bitmap.CompressFormat.JPEG, cmpLevel, baos); // mBitmap is
																	// the
																	// bitmap
																	// object
			byte[] b = baos.toByteArray();
			String base64Image = Base64.encodeToString(b, Base64.DEFAULT);
			returnBase64 = base64Image;
			baos.close();
			baos = null;
			//bitmap.recycle();
			bitmap = null;

		} catch (OutOfMemoryError e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return returnBase64;
	}
	
	public void onBackPressed() {
		
		finish();
	}

}


