package com.iexceed.plugins.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;
import android.telephony.PhoneNumberUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Contacts extends ApzPlugin{

/* used to hold current search criteria i.e firstName,lastName,phNo */

	private static String searchType;

	/* list to hold the search criteria */

	private Map<String, String> searchValues;

	private final String TAG = "CONTACTS";

	private static ApzPlugin pluginObj;

	private static final int PICK_CONTACTS = 108;

	private String mCallbackId;

	private String mAction;

	private JSONObject mJsonObject;
	private String[] permissions;

	private Contacts(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null)
		{
			pluginObj = new Contacts(webView, activity);
		}
		return pluginObj;
	}
	private void addContacts(JSONObject jsonObj) {
		ApzLogger.i(TAG,"addContacts");
		JSONObject details = null;
		String firstName = "";
		String lastName = "";
		String phoneMobile = "";
		String phoneHome = "";
		String phoneWork = "";
		String mail = "";
		String address = "";
		String website = "";
		try {
			details = new JSONObject(jsonObj.getString("details"));
			firstName = details.getString("firstName");
			lastName = details.getString("lastName");
			phoneMobile = details.getString("phoneMobile");
			phoneHome = details.getString("phoneHome");
			phoneWork = details.getString("phoneWork");
			mail = details.getString("mail");
			address = details.getString("address");
			website = details.getString("website");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, this.activity, this.webView, true);
			return;
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			return;
		}
		if (firstName == null || firstName.equals("")) {
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-041", null, this.activity, this.webView, true);//First Name cannot be empty

			return;
		}

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null)
				.build());

		// ------------------------------------------------------Names(firstName and lastName)
		if (firstName != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(	ContactsContract.Data.MIMETYPE,	ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(	ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,	firstName)
					.withValue(	ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,lastName).build());
		}

		// ------------------------------------------------------ Mobile Number
		if (phoneMobile != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(	ContactsContract.Data.MIMETYPE,	ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,phoneMobile)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,	ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.build());
		}

		// ------------------------------------------------------ Home Numbers
		if (phoneHome != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(	ContactsContract.Data.MIMETYPE,	ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,phoneHome)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,	ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
					.build());
		}

		// ------------------------------------------------------ Work Numbers
		if (phoneWork != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(	ContactsContract.Data.MIMETYPE,	ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,phoneWork)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,	ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
					.build());
		}

		// ------------------------------------------------------ Email
		if (mail != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,	mail)
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,ContactsContract.CommonDataKinds.Email.TYPE_HOME)
					.build());
		}

		// ------------------------------------------------------ address
		if (address != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(	ContactsContract.Data.MIMETYPE,	ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,address)
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
					.build());
		}
		// ------------------------------------------------------ website
		if (website != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Website.DATA,website)
					.withValue(ContactsContract.CommonDataKinds.Website.TYPE,ContactsContract.CommonDataKinds.Website.TYPE_WORK)
					.build());
		}

		// Asking the Contact provider to create a new contact
		String newId = null;
		try {
			ContentProviderResult[] cpResults = this.activity.getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			if (cpResults.length >= 0) {
				// to know te newly added contact id
				newId = cpResults[0].uri.getLastPathSegment();
			}
		} catch (RemoteException e) {
			ApzLogger.e(TAG,e.toString());
		} catch (OperationApplicationException e) {
			ApzLogger.e(TAG,e.toString());
		}
		if (newId != null) {
			JSONObject resjson = new JSONObject();
			try {
				resjson.put("successMessage", "Success");
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
			ApzPluginUtil.sendSuccess(mCallbackId, resjson, false, this.activity, this.webView, true);
		} else {
			ApzLogger.e(TAG,"APZ-CNT-041");
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-041", null, this.activity, this.webView, true);//Can not be created
		}
	}

	/**
	 * Returns list of displayNames present in contact
	 * @return
	 */
	private JSONArray filterContacts(String firstName, String lastName,String phoneMobile, String phoneHome, String phoneWork) {
		try {
			ApzLogger.i(TAG,"filterContacts");
			searchValues = new HashMap<String, String>();
			searchValues.put("firstName", firstName);
			searchValues.put("lastName", lastName);
			searchValues.put("phoneMobile", phoneMobile);
			searchValues.put("phoneWork", phoneWork);
			searchValues.put("phoneHome", phoneHome);

		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			return null;
		}

		/* Search by Number */
		JSONArray genericSearchRes = new JSONArray();
		String searchString = "";
//		if(firstName.equals("") && lastName.equals("") && phoneMobile.equals("") && phoneHome.equals("")|| phoneWork.equals("")){
		if(firstName.equals("") && lastName.equals("") && phoneMobile.equals("") && phoneHome.equals("") && phoneWork.equals("")){
			searchString = firstName;
			searchType = "firstName";
			genericSearchRes = genericSearch(searchString, "");
		}
		else if (!firstName.equals("")) {
			searchString = firstName;
			searchType = "firstName";
			genericSearchRes = genericSearch(searchString, "firstName");
		} else if (!lastName.equals("")) {
			searchString = lastName;
			searchType = "lastName";
			genericSearchRes = genericSearch(searchString, "lastName");
		} else if (!phoneMobile.equals("")) {
			searchString = phoneMobile;
			searchType = "phNo";
			genericSearchRes = genericSearch(searchString, "phNo");
		} else if (!phoneHome.equals("")) {
			searchString = phoneHome;
			searchType = "phNo";
			genericSearchRes = genericSearch(searchString, "phNo");
		} else if (!phoneWork.equals("")) {
			searchType = "phNo";
			searchString = phoneWork;
			genericSearchRes = genericSearch(searchString, "phNo");
		}




		return genericSearchRes;
	}

	/**
	 * Returns list of names matching the search critera to
	 * filterContacts(JSONObject) function
	 *
	 * @param name
	 * @return JSONArray with search results
	 */
	private JSONArray genericSearch(String name, String criteria) {
		ApzLogger.i(TAG,"genericSearch");
		/* List will store all the displayName details */
		Set<String> displayNameList = new HashSet<String>();
		if (criteria.equals("phNo")) {
			try {
				Double.parseDouble(name);
				ContentResolver localContentResolver = this.activity.getApplicationContext().getContentResolver();
				Cursor contactLookupCursor = localContentResolver.query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(name)), new String[] {PhoneLookup.DISPLAY_NAME, PhoneLookup._ID }, null,	null, null);
				try {
					if (contactLookupCursor.moveToNext()) {
						do {
							String contactName = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
							displayNameList.add(contactName);
						} while (contactLookupCursor.moveToNext());
						contactLookupCursor.close();

					} else {
						contactLookupCursor.close();
						return null;
					}
				} catch (IllegalStateException e) {
					ApzLogger.e(TAG,e.toString());

				}
			} catch (NumberFormatException e) {
				ApzLogger.e(TAG,e.toString());
				return null;
			}
		}
		/* Search by Number end */

		/* if not a number search */
		else if (displayNameList.isEmpty()) {
			String[] projection1 = null;
			String selection1 = null;
			String[] selectionArgs1 = null;
			if (criteria.equals("")) {
				projection1 = new String[] {ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME };
				selection1 = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " LIKE ?";
				selectionArgs1 = new String[] { name.trim() + "%" };
			}
			else if (criteria.equals("firstName")) {
				projection1 = new String[] {ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,	ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME };
				selection1 = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME	+ " LIKE ?";
				selectionArgs1 = new String[] { name.trim() + "%" };
			} else if (criteria.equals("lastName")) {
				projection1 = new String[] {ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME };
				selection1 = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME+ " LIKE ?";
				selectionArgs1 = new String[] { name.trim() + "%" };
			} else
				return null;
			Cursor nameCur = this.activity.getApplicationContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI,projection1,selection1,selectionArgs1,ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
			if (nameCur.moveToNext()) {
				do {

					String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME)).trim();
//					ApzLogger.d(TAG,"Names:display : " + display);
//					if (!displayNameList.contains(display)) {
//						displayNameList.add(display);
//					}
					displayNameList.add(display);
				} while (nameCur.moveToNext());
				nameCur.close();
			} else {
				nameCur.close();
				return null;
			}
		}

		if (displayNameList == null || displayNameList.isEmpty()) {

			return null;
		}
		final JSONArray searchResultTotal = new JSONArray();
		ApzLogger.i(TAG,"displayNameList Size:" + displayNameList.size());
		for (String str : displayNameList) {
			String displayName = str;

			Uri searchContacts = ContactsContract.Contacts.CONTENT_URI;
			String[] projection = new String[] { ContactsContract.Contacts._ID,	ContactsContract.Contacts.DISPLAY_NAME };
			String selection = ContactsContract.Contacts.DISPLAY_NAME+ " LIKE ?";
			String[] selectionArgs = new String[] { displayName.trim() };
			String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+ " COLLATE LOCALIZED ASC";
			Cursor c = null;
			if (Build.VERSION.SDK_INT < 11) {
				c = activity.managedQuery(searchContacts, projection,selection, selectionArgs, sortOrder);
			} else {
				CursorLoader cursorLoader = new CursorLoader(this.activity.getApplicationContext(),searchContacts, projection, selection, selectionArgs,	sortOrder);
				c = cursorLoader.loadInBackground();
			}

			if(c!=null){
				if (c.moveToFirst()) {
					do {
						JSONObject searchResult = new JSONObject();
						String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
						String imagebase64 =retrieveContactPhoto(contactID);

						try {
							searchResult.put("contactID", contactID);
							Cursor emailCursor = this.activity.getApplicationContext().getContentResolver()
									.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID+ " = ? ",new String[] { contactID }, null);
							while (emailCursor.moveToNext()) {
								String value = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
								int type = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
								if (ContactsContract.CommonDataKinds.Email.TYPE_HOME == type) {
									searchResult.put("email", value);

								}
							}
							emailCursor.close();
							if (!searchResult.has("email")) {
								searchResult.put("email", "");
							}

							Cursor addressCursor = this.activity.getApplicationContext().getContentResolver()
									.query(ContactsContract.Data.CONTENT_URI,null,ContactsContract.Data.CONTACT_ID+ " = ? AND "+ ContactsContract.Data.MIMETYPE+ "= ?",	new String[] {contactID,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE },null);
							while (addressCursor.moveToNext()) {
								int type = addressCursor.getInt(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
								if (ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME == type
										|| ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK == type
										|| ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER == type) {
									String street = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));

									searchResult.put("street", street);
								}
							}
							addressCursor.close();
							if (!searchResult.has("street")) {
								searchResult.put("street", "");
							}
							Cursor websiteCursor = this.activity.getApplicationContext().getContentResolver()
									.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE	+ "= ?",new String[] {contactID,ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE },null);
							while (websiteCursor.moveToNext()) {
								int type = websiteCursor.getInt(websiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
								if (ContactsContract.CommonDataKinds.Website.TYPE_HOME == type
										|| ContactsContract.CommonDataKinds.Website.TYPE_WORK == type
										|| ContactsContract.CommonDataKinds.Website.TYPE_OTHER == type) {
									String websiteUrl = websiteCursor.getString(websiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));

									searchResult.put("website", websiteUrl);
								}
							}
							websiteCursor.close();
							if (!searchResult.has("website")) {
								searchResult.put("website", "");
							}

							Cursor nameCursor = this.activity.getApplicationContext().getContentResolver()
									.query(ContactsContract.Data.CONTENT_URI,null,ContactsContract.Data.MIMETYPE	+ "= ? AND "+ ContactsContract.RawContactsEntity.CONTACT_ID	+ "= ?",new String[] {ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,contactID }, null);
							while (nameCursor.moveToNext()) {
								String given = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
								String family = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
								if (given != null) {
									searchResult.put("firstName", given);
								}
								if (family != null) {
									searchResult.put("lastName", family);
								}

							}
							nameCursor.close();
							if (!searchResult.has("firstName")) {
								searchResult.put("firstName", "");
							}
							if (!searchResult.has("lastName")) {
								searchResult.put("lastName", "");
							}
							/* Phone numbers */
							Cursor phoneCursor = this.activity.getApplicationContext()
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",new String[] { contactID }, null);
							while (phoneCursor.moveToNext()) {
								String no = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
								if (ContactsContract.CommonDataKinds.Phone.TYPE_HOME == type) {
									searchResult.put("phoneHome", no);
								} else if (ContactsContract.CommonDataKinds.Phone.TYPE_WORK == type) {
									searchResult.put("phoneWork", no);
								} else if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == type) {
									searchResult.put("phoneMobile", no);
								}
							}
							if (!searchResult.has("phoneHome")) {
								searchResult.put("phoneHome", "");
							}
							if (!searchResult.has("phoneWork")) {
								searchResult.put("phoneWork", "");
							}
							if (!searchResult.has("phoneMobile")) {
								searchResult.put("phoneMobile", "");
							}
							phoneCursor.close();
							/* before adding to list check other search criteria */
							if (matchCriteria(searchResult)) {
								searchResult.put("encodedImage", imagebase64);
								searchResultTotal.put(searchResult);
							}


							/* phones */
							/* Email,Address,Website end */
						} catch (JSONException e) {
							ApzLogger.e(TAG,e.toString());
							return null;
						}

					} while (c.moveToNext());
				} else {

				}
				c.close();
			}else{
				//c cursore is null
			}

		}
		return searchResultTotal;

	}

	/* Used to narrow down the search criteria */
	private boolean matchCriteria(JSONObject searchResult) {
		ApzLogger.i(TAG,"matchCriteria");
		try {
			if (searchType.equals("firstName")) {

				if ((searchResult.getString("lastName").contains(searchValues.get("lastName")) || searchValues.get(	"lastName").equals(""))
						&& (searchResult.getString("phoneMobile").equalsIgnoreCase(searchValues.get("phoneMobile")) || searchValues.get("phoneMobile").equals(""))
						&& (searchResult.getString("phoneHome").equalsIgnoreCase(searchValues.get("phoneHome")) || searchValues.get("phoneHome").equals(""))
						&& (searchResult.getString("phoneWork").equalsIgnoreCase(searchValues.get("phoneWork")) || searchValues.get("phoneWork").equals(""))) {
					return true;

				}

			} else if (searchType.equals("lastName")) {
				if ((searchResult.getString("phoneMobile").equalsIgnoreCase(searchValues.get("phoneMobile")) || searchValues.get("phoneMobile").equals(""))
						&& (searchResult.getString("phoneHome").equalsIgnoreCase(searchValues.get("phoneHome")) || searchValues.get("phoneHome").equals(""))
						&& (searchResult.getString("phoneWork").equalsIgnoreCase(searchValues.get("phoneWork")) || searchValues.get("phoneWork").equals(""))) {
					return true;

				}

			} else if (searchType.equals("phNo")) {
				if ((searchResult.getString("phoneHome").equalsIgnoreCase(searchValues.get("phoneHome")) || searchValues.get("phoneHome").equals(""))
						&& (searchResult.getString("phoneWork").equalsIgnoreCase(searchValues.get("phoneWork")) || searchValues.get("phoneWork").equals(""))) {
					return true;

				}

			}

		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}

		return false;
	}

	private void searchContacts(JSONObject searchCriteriaJsonObj) {
		ApzLogger.i(TAG,"searchContacts");
		JSONObject searchCriteria = null;
		String firstName = "";
		String lastName = "";
		String phoneMobile = "";
		String phoneHome = "";
		String phoneWork = "";
		try {
			searchCriteria = new JSONObject(searchCriteriaJsonObj.getString("searchCriteria"));
			firstName = searchCriteria.getString("firstName").trim();
			lastName = searchCriteria.getString("lastName").trim();
			phoneMobile = searchCriteria.getString("phoneMobile").trim();
			phoneHome = searchCriteria.getString("phoneHome").trim();
			phoneWork = searchCriteria.getString("phoneWork").trim();


		} catch (JSONException e) {

			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, this.activity, this.webView, true);

			return;
		} catch (Exception e) {
			return;
		}
		/**/
		final JSONArray searchResultTotal = filterContacts(firstName, lastName,	phoneMobile, phoneHome, phoneWork);

		if (searchResultTotal == null || !(searchResultTotal.length() > 0)) {
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-044", null, this.activity, this.webView, true);//Contact not found

			return;
		}
//		ApzLogger.i(TAG,"Contact SearchRes:" + searchResultTotal.toString());
		if (searchResultTotal.length() > 0) {
			JSONObject result = new JSONObject();
			try{
				result.put("contacts", searchResultTotal);
			}catch(Exception ex){

			}
			ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);

		} else {
			ApzLogger.e(TAG,"APZ-CNT-044");
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-044", null, this.activity, this.webView, true);//No Contact Found.
		}
	}

	private void deleteContact(JSONObject jsonObj) {
		ApzLogger.i(TAG,"deleteContact");
		JSONObject deleteCriteria = null;
		String firstName = "";
		String lastName = "";
		String phoneMobile = "";
		String phoneHome = "";
		String phoneWork = "";
		try {
			deleteCriteria = new JSONObject(jsonObj.getString("deleteCriteria"));
			firstName = deleteCriteria.getString("firstName");
			lastName = deleteCriteria.getString("lastName");
			phoneMobile = deleteCriteria.getString("phoneMobile");
			phoneHome = deleteCriteria.getString("phoneHome");
			phoneWork = deleteCriteria.getString("phoneWork");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-044", null, this.activity, this.webView, true);//No Contact Found.
			return;
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			return;
		}
		final JSONArray deleteResult = filterContacts(firstName, lastName,phoneMobile, phoneHome, phoneWork);

		if (deleteResult == null || !(deleteResult.length() > 0)) {
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-044", null, this.activity, this.webView, true);//No Contact Found.
			return;
		}

		if (deleteResult.length() == 1) {
			boolean isDeleted = false;
			/* finding raw_contact_id for contacts to be deleted */
			Cursor rawContCur = null;

			try {
				rawContCur = this.activity.getApplicationContext().getContentResolver().query(
						RawContacts.CONTENT_URI,
						new String[] { RawContacts._ID },
						RawContacts.CONTACT_ID + "=?",
						new String[] { ((JSONObject) deleteResult.get(0))
								.getString("contactID") }, null);
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
				return;
			}
			List<String> rawIdList = new ArrayList<String>();
			if (rawContCur.moveToFirst()) {
				do {
					String rawId = rawContCur.getString(rawContCur.getColumnIndex(RawContacts._ID));
					rawIdList.add(rawId);
				} while (rawContCur.moveToNext());

				rawContCur.close();
			}


			/* delete all rowcontact_id related to contact_id */

			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			for (int j = 0; j < rawIdList.size(); j++) {
				ops.add(ContentProviderOperation
						.newDelete(RawContacts.CONTENT_URI)
						.withSelection(RawContacts._ID + " = ?",
								new String[] { rawIdList.get(j) }).build());
			}
			try {
				ContentProviderResult[] res = this.activity.getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
				if (res != null) {
					isDeleted = true;
				}

			} catch (RemoteException e) {
				ApzLogger.e(TAG,e.toString());
				return;
			} catch (OperationApplicationException e) {
				ApzLogger.e(TAG,e.toString());
				return;
			}
			/**/

			if (isDeleted) {
				JSONObject result = new JSONObject();
				try {
					result.put("successMessage", "Deleted");
				} catch (JSONException e) {
				}
				ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);

			} else {
				ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-043", null, this.activity, this.webView, true);//Deletion Failed.
			}

		} else {
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-045", null, this.activity, this.webView, true);//Multiple Contact Exist
		}
	}

	private void editContacts(JSONObject jsonObj) {
		ApzLogger.i(TAG,"editContact");
		JSONObject updateJson = null;
		String firstNameNew = "";
		String lastNameNew = "";
		String phoneMobileNew = "";
		String phoneHomeNew = "";
		String phoneWorkNew = "";
		String websiteNew = "";
		String mailNew = "";
		String addressNew = "";
		JSONObject searchCriteria = null;
		String firstName = "";
		String lastName = "";
		String phoneMobile = "";
		String phoneHome = "";
		String phoneWork = "";
		try {
			searchCriteria = new JSONObject(jsonObj.getString("searchCriteria"));
			firstName = searchCriteria.getString("firstName");
			lastName = searchCriteria.getString("lastName");
			phoneMobile = searchCriteria.getString("phoneMobile");
			phoneHome = searchCriteria.getString("phoneHome");
			phoneWork = searchCriteria.getString("phoneWork");
			updateJson = new JSONObject(jsonObj.getString("details"));
			firstNameNew = updateJson.getString("firstName");
			lastNameNew = updateJson.getString("lastName");
			phoneMobileNew = updateJson.getString("phoneMobile");
			phoneHomeNew = updateJson.getString("phoneHome");
			phoneWorkNew = updateJson.getString("phoneWork");
			websiteNew = updateJson.getString("website");
			mailNew = updateJson.getString("mail");
			addressNew = updateJson.getString("address");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, this.activity, this.webView, true);//Multiple Contact Exist
			return;
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			return;
		}
		/**/
		final JSONArray editSearchResult = filterContacts(firstName, lastName,phoneMobile, phoneHome, phoneWork);

		if (editSearchResult == null || !(editSearchResult.length() > 0)) {
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-044", null, this.activity, this.webView, true);//Contact Not Found
			return;
		}
//		ApzLogger.i(TAG,"Contact editSearchResult:"+ editSearchResult.toString());
		// if single record exist ,then perform edit else Multiple contact exist
		if (editSearchResult.length() == 1) {

			try {
				// if single contact is present
				String contactID = ((JSONObject) editSearchResult.get(0)).getString("contactID");
				boolean isUpdated = false;
				ContentResolver cr = activity.getApplicationContext().getContentResolver();
				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation
						.newUpdate(RawContacts.CONTENT_URI)
						.withValue(RawContacts.ACCOUNT_TYPE,null)
						.withValue(RawContacts.ACCOUNT_NAME,null).build());

				if (!firstNameNew.equals("")) {
					ContentProviderOperation.Builder contactBuilder = ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(
									ContactsContract.Data.RAW_CONTACT_ID
											+ "=? AND "
											+ ContactsContract.Data.MIMETYPE
											+ "=?",
									new String[] {
											contactID,
											ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE })
							.withValue(
									ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
									firstNameNew);
					ops.add(contactBuilder.build());
				}
				if (!lastNameNew.equals("")) {
					ContentProviderOperation.Builder contactBuilder = ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(
									ContactsContract.Data.RAW_CONTACT_ID
											+ "=? AND "
											+ ContactsContract.Data.MIMETYPE
											+ "=?",
									new String[] {
											contactID,
											ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE })
							.withValue(
									ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
									lastNameNew);
					ops.add(contactBuilder.build());
				}
				/* get_ email row id */
				String phone_rowId = "";
				String homePhone_rowId = "";
				String workPhone_rowId = "";
				Cursor phoneCursor = activity.getApplicationContext().getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ? ", new String[] { contactID }, null);
				while (phoneCursor.moveToNext()) {

					int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
					if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == type) {
						phone_rowId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
					}
					if (ContactsContract.CommonDataKinds.Phone.TYPE_HOME == type) {
						homePhone_rowId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
					}
					if (ContactsContract.CommonDataKinds.Phone.TYPE_WORK == type) {
						workPhone_rowId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
					}

				}
//				ApzLogger.i(TAG,"Contacts:Phone=" + phone_rowId +" HomePhone=" + homePhone_rowId+" WorkPhone=" + workPhone_rowId);
				phoneCursor.close();

				/**/

				if (!phoneMobileNew.equals("")) {
					ops.add(ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(
									ContactsContract.CommonDataKinds.Phone._ID
											+ "=? AND "
											+ ContactsContract.Data.MIMETYPE
											+ "=?",
									new String[] {
											phone_rowId,
											ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE })
							.withValue(
									ContactsContract.CommonDataKinds.Phone.DATA,
									phoneMobileNew)
							.withValue(
									ContactsContract.CommonDataKinds.Phone.TYPE,
									ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
							.build());
				}
				if (!phoneWorkNew.equals("")) {
					ops.add(ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(
									ContactsContract.CommonDataKinds.Phone._ID
											+ "=? AND "
											+ ContactsContract.Data.MIMETYPE
											+ "=?",
									new String[] {
											workPhone_rowId,
											ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE })
							.withValue(ContactsContract.CommonDataKinds.Phone.DATA,	phoneWorkNew)
							.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
							.build());
				}
				if (!phoneHomeNew.equals("")) {
					ops.add(ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(
									ContactsContract.CommonDataKinds.Phone._ID
											+ "=? AND "
											+ ContactsContract.Data.MIMETYPE
											+ "=?",
									new String[] {
											homePhone_rowId,
											ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE })
							.withValue(
									ContactsContract.CommonDataKinds.Phone.DATA,
									phoneHomeNew)
							.withValue(
									ContactsContract.CommonDataKinds.Phone.TYPE,
									ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
							.build());
				}

				/* get_ email row id */
				String Email_rowId = "";
				Cursor emailCursor = activity.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,	null,ContactsContract.CommonDataKinds.Email.CONTACT_ID+ " = ? ", new String[] { contactID }, null);
				while (emailCursor.moveToNext()) {
					Email_rowId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID));

				}

				emailCursor.close();

				/**/

				if (!mailNew.equals("") && !(Email_rowId.equals(""))) {
					ops.add(ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(ContactsContract.CommonDataKinds.Email._ID+ "=? AND "	+ ContactsContract.Data.MIMETYPE+ "=?",	new String[] {Email_rowId,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE })
							.withValue(ContactsContract.CommonDataKinds.Email.DATA,mailNew)
							.withValue(	ContactsContract.CommonDataKinds.Email.TYPE,ContactsContract.CommonDataKinds.Email.TYPE_HOME)
							.build());
				}
				/* get_ address row id */
				String address_rowId = "";
				Cursor addressCursor = activity.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,null,ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID+ " = ? ", new String[] { contactID },null);
				while (addressCursor.moveToNext()) {

					address_rowId = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal._ID));

				}
//				ApzLogger.i(TAG,"Contacts:Address=" + address_rowId);
				addressCursor.close();

				/**/
				if (!addressNew.equals("") && !(address_rowId.equals(""))) {
					ops.add(ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(ContactsContract.CommonDataKinds.StructuredPostal._ID+ "=? AND "	+ ContactsContract.Data.MIMETYPE + "=?",	new String[] {address_rowId,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE })
							.withValue(	ContactsContract.CommonDataKinds.StructuredPostal.TYPE,	ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
							.withValue(	ContactsContract.CommonDataKinds.StructuredPostal.STREET,addressNew).build());
				}
				/* get_ webSite row id */
				String webSite_rowId = "";
				Cursor websiteCursor = activity.getApplicationContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI,null,ContactsContract.Data.CONTACT_ID + " = ? AND "+ ContactsContract.Data.MIMETYPE+ "= ?",new String[] {contactID,ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE },	null);
				while (websiteCursor.moveToNext()) {
					webSite_rowId = websiteCursor.getString(websiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website._ID));

				}
				websiteCursor.close();

				/**/

				if (!websiteNew.equals("") && !(webSite_rowId.equals(""))) {
					ops.add(ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(ContactsContract.CommonDataKinds.Website._ID	+ "=? AND "	+ ContactsContract.Data.MIMETYPE+ "=?",	new String[] {webSite_rowId,ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE })
							.withValue(ContactsContract.CommonDataKinds.Website.DATA,websiteNew)
							.withValue(ContactsContract.CommonDataKinds.Website.TYPE,ContactsContract.CommonDataKinds.Website.TYPE_WORK)
							.build());
				}
				try {
					ContentProviderResult[] res = cr.applyBatch(ContactsContract.AUTHORITY, ops);
					if (res != null)
						isUpdated = true;
				} catch (RemoteException e) {
					ApzLogger.e(TAG,e.toString());
					return;
				} catch (OperationApplicationException e) {
					ApzLogger.e(TAG,e.toString());
					return;
				}
				if (isUpdated) {
					JSONObject result = new JSONObject();
					result.put("successMessage", "Updated");
					ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);

				} else {
					ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-042", null, this.activity, this.webView, true);//Update Failed
				}
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}

		} else {
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-045", null, this.activity, this.webView, true);//Multiple Contact Exist

		}

	}

	private void fectchContacts(int PICK_CONTACTS){
		Intent intent = null;
		try{
			intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		}catch(Exception ex){

		}
		//activity.startActivityForResult(intent, PICK_CONTACTS);
		this.activity.startActivityForResult(intent, PICK_CONTACTS, new ExternalActivityResultHandler() {

			@Override
			public void handleActivityResult(int resultCode, Intent data) {
				if(resultCode == Activity.RESULT_OK){
					String mime;    // MIME type
					int dataIdx;    // Index of DATA1 column
					int mimeIdx;    // Index of MIMETYPE column
					int nameIdx;    // Index of DISPLAY_NAME column
					String name = "";
					Uri contactData = data.getData();
					String email = "";
					String phone= "";
					String base64 = "";
					List<String> allNumbers = new ArrayList<String>();
					List<String> allemails = new ArrayList<String>();
					Cursor cursor;  // Cursor object

					cursor =  activity.managedQuery(contactData, null, null, null, null);
					if (cursor.moveToFirst()) {
						nameIdx = cursor.getColumnIndex(
								ContactsContract.Contacts.DISPLAY_NAME);
						name = name = cursor.getString(nameIdx);
						base64 = retrieveContactPhoto(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
						String[] projection = {
								ContactsContract.Data.DISPLAY_NAME,
								ContactsContract.Contacts.Data.DATA1,
								ContactsContract.Contacts.Data.MIMETYPE };

						// Query ContactsContract.Data
						cursor = activity.getApplicationContext().getContentResolver().query(
								ContactsContract.Data.CONTENT_URI, projection,
								ContactsContract.Data.DISPLAY_NAME + " = ?",
								new String[] { name },
								null);

						if (cursor.moveToFirst()) {
							// Get the indexes of the MIME type and data
							mimeIdx = cursor.getColumnIndex(
									ContactsContract.Contacts.Data.MIMETYPE);
							dataIdx = cursor.getColumnIndex(
									ContactsContract.Contacts.Data.DATA1);
							if (cursor.moveToFirst()) {
								// Get the indexes of the MIME type and data
								mimeIdx = cursor.getColumnIndex(
										ContactsContract.Contacts.Data.MIMETYPE);
								dataIdx = cursor.getColumnIndex(
										ContactsContract.Contacts.Data.DATA1);

								// Match the data to the MIME type, store in variables
								do {
									mime = cursor.getString(mimeIdx);
									if (ContactsContract.CommonDataKinds.Email
											.CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
										email = cursor.getString(dataIdx);
										allemails.add(email);
									}
									if (ContactsContract.CommonDataKinds.Phone
											.CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
										phone = cursor.getString(dataIdx);
										phone = PhoneNumberUtils.formatNumber(phone);
										allNumbers.add(phone);
									}
								} while (cursor.moveToNext());
							}
						}

					}
					final String[] items = allNumbers.toArray(new String[allNumbers.size()]);
					final String[] emailArr = allemails.toArray(new String[allemails.size()]);
					JSONArray jsonarr = null;
					JSONArray emailjson = null;
					try {
						jsonarr = new JSONArray(items);
						emailjson = new JSONArray(emailArr);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block

					}
					final JSONObject jsonObj = new JSONObject();
					try {
						jsonObj.put("name", name);
						jsonObj.put("phoneno", jsonarr);
						jsonObj.put("email", emailjson);
						jsonObj.put("encodedImage",base64);

					} catch (Exception e) {

					}
					ApzPluginUtil.sendSuccess(mCallbackId, jsonObj, false, activity, webView, true);


				}else{
					ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-328", null, activity, webView, true);
				}

			}
		});
	}


	private String retrieveContactPhoto(String contactID) {

		Bitmap photo = null;
		String encoded = "";

		try {
			InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(activity.getContentResolver(),
					ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

			if (inputStream != null) {
				photo = BitmapFactory.decodeStream(inputStream);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
				byte[] byteArray = byteArrayOutputStream.toByteArray();
				encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
				assert inputStream != null;
				inputStream.close();
			}else{
				encoded = "";
			}

		} catch (IOException e) {

		}
		return encoded;
	}


	public static boolean isContacts() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		try {
			mJsonObject = params;
			mAction = params.getString("action");
			mCallbackId = params.getString("id");

			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
				if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
						!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS)
						!= PackageManager.PERMISSION_GRANTED ) {
					permissions = new String[]{
							Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
					requestForPermission();
				} else {
					proceedContacts();
				}
			} else {
				proceedContacts();
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}

	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_CONTACTS, new OnPermissionsResultHandler() {
					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_CONTACTS) {
							boolean denied = false;
							boolean never_ask_again = false;
							for (String permission : permissions) {
								if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
									denied = true;
								} else {
									if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
										//proceedcontacts();
									} else {
										never_ask_again = true;
									}
								}
							}
							if (never_ask_again) {
								PermissionDeniedCallback();
							}else if (denied) {
								displayReconfirmationMessage();
							} else {
								proceedContacts();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}
	private void displayReconfirmationMessage() {
		String message = "To add or edit contacts, allow app to access by granting requested permissions";
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setTitle("Permission Denied");
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						requestForPermission();
					}
				}).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				PermissionDeniedCallback();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	private void PermissionDeniedCallback(){
		ApzPluginUtil.sendPermissionDenied("Contacts",mCallbackId, this.activity,this.webView);
	}

	private void proceedContacts(){
		if(mAction.equalsIgnoreCase("ADDCONTACT")){
			addContacts(mJsonObject);
		}else if(mAction.equalsIgnoreCase("DELETECONTACT")){
			deleteContact(mJsonObject);
		}else if(mAction.equalsIgnoreCase("EDITCONTACT")){
			editContacts(mJsonObject);
		}else if(mAction.equalsIgnoreCase("SEARCHCONTACT")){
			searchContacts(mJsonObject);
		}else if(mAction.equalsIgnoreCase("FETCHCONTACT")){
			fectchContacts(PICK_CONTACTS);
		}
	}
}

