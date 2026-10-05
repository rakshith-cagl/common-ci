package com.iexceed.plugins.createnote;

import java.util.Locale;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.DatabaseHandler;
import com.iexceed.common.StringUtils;
import com.iexceed.common.UserSettings;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class used for adding notes to local DB.
 * 
 * @author Binay ku Behera
 * 
 */
public class CreateNote extends Activity {

	private Button delete;

	private Button save;

	// DatabaseHandler na;

	private EditText note;

	private TextView txn_id;

	private DatabaseHandler dbHelper;

	private SharedPreferences settings;

	private String languageCode;

	final static String properties = "USER_PREFS";

	private String TAG = "";

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApzLogger.i(TAG, "Create Note");
		/* To change language setting */
		settings = getApplication().getSharedPreferences(properties, 0);
		languageCode = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,
				"language", StringUtils.getString(StringUtils.DEFAULT_LANG),
				settings);
		final Locale appLocale = new Locale(languageCode);
		Locale.setDefault(appLocale);
		final Configuration config2 = new Configuration();
		config2.locale = appLocale;
		getApplicationContext().getResources().updateConfiguration(config2,
				getBaseContext().getResources().getDisplayMetrics());
		/**/
		setContentView(R.layout.create_note);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		final Bundle bundle = this.getIntent().getExtras();
		final String refNo = bundle.getString("refNO");
		ApzLogger.i(TAG, "Create Note REFERENCE Number : " + refNo);
		delete = (Button) findViewById(R.id.button3);
		save = (Button) findViewById(R.id.button4);
		note = (EditText) findViewById(R.id.editText1);
		txn_id = (TextView) findViewById(R.id.textView2);
		txn_id.setText(refNo);
		dbHelper = new DatabaseHandler(this);
		final String l_note = dbHelper.getNote(refNo);
//		ApzLogger.i(TAG, "Create Note l note : " + l_note);
		note.setText(l_note);
		note.setSelection(note.getText().length());
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dbHelper.deleteNoteRow(refNo)) {
					makeToastMsg();
				}
				finish();

			}
		});
		save.setOnClickListener(new View.OnClickListener() {

			final String emptyString = "";

			@Override
			public void onClick(View v) {
				if (emptyString.equals(note.getText().toString())) {
					// do nothing
				} else {
					final Notes txn_note = new Notes(txn_id.getText()
							.toString(), note.getText().toString());
					if (emptyString.equalsIgnoreCase(l_note)) {
						dbHelper.addNotes(txn_note);
					} else {
						dbHelper.updateNote(txn_note, refNo);
					}
					finish();
				}

			}
		});

	}

	public final void makeToastMsg() {
		Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show();
	}

	@Override
	public final void onStart() {

		super.onStart();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (dbHelper != null)
				dbHelper.close();
		} catch (SQLiteException e) {
			ApzLogger.e(TAG, e.toString());
		}
	}

	@Override
	public final void onBackPressed() {
		super.onBackPressed();
		try {
			if (dbHelper != null)
				dbHelper.close();
		} catch (SQLiteException ex) {
			ApzLogger.e(TAG, ex.getLocalizedMessage());
		}
		finish();
	}
}
