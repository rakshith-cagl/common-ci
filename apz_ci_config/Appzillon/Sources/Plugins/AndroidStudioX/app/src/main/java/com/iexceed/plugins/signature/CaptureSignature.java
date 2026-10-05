package com.iexceed.plugins.signature;

import java.io.ByteArrayOutputStream;

import com.iexceed.common.UserSettings;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CaptureSignature extends Activity {
	private SharedPreferences settings;
	
	final static String properties = "USER_PREFS";
	
	private boolean isCaptured = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		AuditLog.makeString("SIGNATURE","onCreate");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		settings = getApplication().getSharedPreferences(properties, 0);
		setContentView(new SignatureLayout(getApplicationContext()));
	}

	public class SignatureLayout extends LinearLayout {

		LinearLayout buttonsLayout;
		SignatureView signatureView;
		Bitmap signatureBitmap = null;

		public SignatureLayout(Context context) {
			super(context);
			this.setOrientation(LinearLayout.VERTICAL);
			this.buttonsLayout = this.buttonsLayout();
			this.signatureView = new SignatureView(context);
			this.addView(this.buttonsLayout);
			this.addView(signatureView);

		}

		private LinearLayout buttonsLayout() {
			LinearLayout linearLayout = new LinearLayout(this.getContext());
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			linearLayout.setBackgroundColor(Color.GRAY);
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(10, 0, 10, 0);
			Button savebtn = new Button(this.getContext());
			savebtn.setPadding(20, 0, 20, 0);			
			Button clrbtn = new Button(this.getContext());
			clrbtn.setPadding(20, 0, 20, 0);
			Button cancelbtn = new Button(this.getContext());
			cancelbtn.setPadding(20, 0, 20, 0);
			savebtn.setTag("Save");
			savebtn.setText("Save");
			savebtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					AuditLog.makeString("SIGNATURE","Save Button");
					saveImage(signatureView.getSignature());

				}
			});
			clrbtn.setTag("Clear");
			clrbtn.setText("Clear");
			clrbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					AuditLog.makeString("SIGNATURE","Clear button");
					signatureView.clearSignature();

				}
			});

			cancelbtn.setTag("Cancel");
			cancelbtn.setText("Cancel");
			cancelbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					AuditLog.makeString("SIGNATURE","Cancel button");
					finish();
				}
			});

			linearLayout.addView(cancelbtn,layoutParams);
			linearLayout.addView(savebtn,layoutParams);
			linearLayout.addView(clrbtn,layoutParams);
			return linearLayout;
		}

		final void saveImage(Bitmap signature) {
			//Abhishek Bug ID 4704 START
			if(isCaptured){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				signatureBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
				byte[] b = baos.toByteArray();
				String base64sign = Base64.encodeToString(b, Base64.DEFAULT);
				// Add by Anand Kumar 
				Intent in = new Intent(getApplicationContext(),ApzCaptureSignaturePlugin.class);
				in.putExtra("signvalue",base64sign);
				//UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,"base64sign", base64sign, settings);
				setResult(RESULT_OK, in);
				finish();
			} 
			else{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_signature), Toast.LENGTH_SHORT).show();
			}
			//Abhishek Bug ID 4704 END
		}

		private class SignatureView extends View {

			private static final float STROKE_WIDTH = 5f;
			private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
			private Paint paint = new Paint();
			private Path path = new Path();
			private float lastTouchX;
			private float lastTouchY;
			private final RectF dirtyRect = new RectF();

			public SignatureView(Context context) {
				super(context);
				paint.setAntiAlias(true);
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeJoin(Paint.Join.ROUND);
				paint.setStrokeWidth(STROKE_WIDTH);
				this.setBackgroundColor(Color.WHITE);
				this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}

			protected Bitmap getSignature() {
//				AuditLog.makeString("SIGNATURE","Get signature");

				if (signatureBitmap == null) {
					signatureBitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(), Bitmap.Config.RGB_565);
				}
				final Canvas canvas = new Canvas(signatureBitmap);
				this.draw(canvas);
				return signatureBitmap;
			}

			private void clearSignature() {
				isCaptured = false;
				path.reset();
				this.invalidate();
			}

			// all touch events during the drawing
			@Override
			protected void onDraw(Canvas canvas) {
				canvas.drawPath(this.path, this.paint);
			}

			@Override
			public boolean onTouchEvent(MotionEvent event) {
				float eventX = event.getX();
				float eventY = event.getY();

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					path.moveTo(eventX, eventY);

					lastTouchX = eventX;
					lastTouchY = eventY;
					isCaptured = true;
					return isCaptured;

				case MotionEvent.ACTION_MOVE:

				case MotionEvent.ACTION_UP:

					resetDirtyRect(eventX, eventY);
					int historySize = event.getHistorySize();
					for (int i = 0; i < historySize; i++) {
						float historicalX = event.getHistoricalX(i);
						float historicalY = event.getHistoricalY(i);

						expandDirtyRect(historicalX, historicalY);
						path.lineTo(historicalX, historicalY);
					}
					path.lineTo(eventX, eventY);
					break;

				default:

					isCaptured = false;
					return isCaptured;
				}

				invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),(int) (dirtyRect.top - HALF_STROKE_WIDTH),(int) (dirtyRect.right + HALF_STROKE_WIDTH),(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

				lastTouchX = eventX;
				lastTouchY = eventY;

				isCaptured = true;
				return isCaptured;
			}

			private void expandDirtyRect(float historicalX, float historicalY) {
				if (historicalX < dirtyRect.left) {
					dirtyRect.left = historicalX;
				} else if (historicalX > dirtyRect.right) {
					dirtyRect.right = historicalX;
				}

				if (historicalY < dirtyRect.top) {
					dirtyRect.top = historicalY;
				} else if (historicalY > dirtyRect.bottom) {
					dirtyRect.bottom = historicalY;
				}

			}

			private void resetDirtyRect(float eventX, float eventY) {
				dirtyRect.left = Math.min(lastTouchX, eventX);
				dirtyRect.right = Math.max(lastTouchX, eventX);
				dirtyRect.top = Math.min(lastTouchY, eventY);
				dirtyRect.bottom = Math.max(lastTouchY, eventY);
			}

		}
	}

	public static boolean isSignauturePlugin() {
		return true;
	}
}
