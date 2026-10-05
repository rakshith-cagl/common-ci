package com.iexceed.utils.localstorage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object EncryptedPrefHelper {

    private var prefs: SharedPreferences? = null
    private const val PREFS_NAME = "ENC_USER_PREFS"

    public fun getPrefs(context: Context): SharedPreferences {
        if (prefs == null) {
            init(context)
        }
        return prefs!!
    }

    public fun getPrefs(): SharedPreferences {
        return prefs!!
    }

    fun init(context: Context): SharedPreferences {
        prefs = buildPref(context)
        return prefs!!
    }

    private fun buildPref(aContext: Context) =
        EncryptedSharedPreferences.create(
            aContext,
            PREFS_NAME, getMasterKey(aContext),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )


    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    fun read(key: String, value: String): String? {
        return getPrefs().getString(key, value)
    }

    fun read(key: String, value: Long): Long {
        return getPrefs().getLong(key, value)
    }

    fun read(key: String, value: Boolean): Boolean {
        return getPrefs().getBoolean(key, value)
    }

    fun read(key: String, value: Int): Int {
        return getPrefs().getInt(key, value)
    }

    fun save(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = getPrefs().edit()
        with(prefsEditor) {
            putString(key, value)
            commit()
        }
    }

    fun save(key: String, value: Long) {
        val prefsEditor: SharedPreferences.Editor = getPrefs().edit()
        with(prefsEditor) {
            putLong(key, value)
            commit()
        }
    }

    fun save(key: String, value: Int) {
        val prefsEditor: SharedPreferences.Editor = getPrefs().edit()
        with(prefsEditor) {
            putInt(key, value)
            commit()
        }
    }

    fun save(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = getPrefs().edit()
        with(prefsEditor) {
            putBoolean(key, value)
            commit()
        }
    }

}
