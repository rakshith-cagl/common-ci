package com.iexceed.plugins.fileoperation

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonUtils.getApzFile
import com.iexceed.common.FileUtils
import com.iexceed.common.StringUtils
import com.iexceed.common.StringUtils.getString
import com.iexceed.common.UserSettings.getAppValue
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.common.ApzHelperUtils
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import com.iexceed.utils.localstorage.FileAccessHelper
import kotlinx.coroutines.launch
import java.io.File
import java.io.FilenameFilter
import java.util.*

class DirectoryBrowser : AppCompatActivity()
{
    private var mFileItems: ArrayList<File> = ArrayList()
    private var mFilePathItems: MutableList<String> = ArrayList()
    private var prevDir: String? = null
    private var canGoBack = false
    var mFileListAdapter: CustomArrayAdapter? = null
    private var settings: SharedPreferences? = null
    private var languageCode: String? = null

    /* For EXT */
    private var mFilterEnabled = false
    private var mFileExtGrp: Array<String?> = listOf<String>().toTypedArray()
    private val TAG = "FILE BROWSER"
    var list: ListView? = null

    public override fun onCreate(icicle: Bundle?)
    {
        super.onCreate(icicle)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        ApzLogger.d(TAG, "inside oncreate")
        //    AuditLog.makeString("FILE BROWSER","on cerate");
        /* To change language setting */
        FILE_DIR = returnFileDirectory()
        settings = EncryptedPrefHelper.init(applicationContext)
        languageCode = getAppValue(
            AppzillonMainScreen.APP_NAME, "DEFAULTLANGUAGE", getString(
                StringUtils.DEFAULT_LANG
            ), (settings)!!
        )
        val appLocale = Locale(languageCode!!)
        ApzHelperUtils.setLocale(this, appLocale)
        setContentView(R.layout.file_browser_activity)
        val emptyText = findViewById<TextView>(R.id.empty)
        list = findViewById<View>(R.id.list) as ListView
        list!!.emptyView = emptyText
        // check for filter type
        if ((intent.getStringExtra("location") == "EXTERNAL")) {
            handleExternalLocation()
        }
        else if (intent.getStringExtra("location") != "") {
            // check for SD Card availability
            handleWhenLocationNotEmpty()
        }
        else if (!intent.getStringExtra("filter").isNullOrEmpty()) {
            handleFilterOptions()
        }
        else if (("DEFAULT" == intent.getStringExtra("root"))) {
            handleDefaultOptions()
        } else {
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.invalid_search))
                .setNeutralButton(
                    resources.getString(R.string.ok)
                ) { _, _ -> finish() }.show()
        }
        list!!.onItemClickListener =
            OnItemClickListener { _, _, _, id ->
                val selectedRow: Int = id.toInt()
                ApzLogger.i(TAG, "File Browser:$selectedRow")
                canGoBack = true
                val file: File = getApzFile(mFilePathItems[selectedRow], null)
                if (file.isDirectory) {
                    prevDir = file.toString()
                    ApzLogger.i(TAG, "File:Prev FILE:$file");
                    getFiles(file.listFiles())
                } else {
                    handleWhenItIsAFile(file)
                }
            }
    }

    private fun handleWhenItIsAFile(file: File) {
        ApzLogger.i(TAG, "File Opener:$file");
        try {
            if ((intent.getStringExtra("openFile") == "Y")) {
                try {
                    if (file.exists()) {
                        val i: Intent = Intent(
                            Intent.ACTION_VIEW,
                            FileProvider.getUriForFile(
                                this@DirectoryBrowser,
                                BuildConfig.APPLICATION_ID,
                                file
                            )
                        )
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(i)
                    }
                } catch (act: ActivityNotFoundException) {
                    ApzLogger.e(TAG, act.toString())
                    val `in`: Intent = Intent()
                    `in`.putExtra("error", "ANF")
                    setResult(RESULT_CANCELED, `in`)
                    finish()
                }
            } else {
                val `in`: Intent = Intent()
                `in`.putExtra("filePath", file.path)
                setResult(RESULT_OK, `in`)
                finish()
            }
        } catch (anf: ActivityNotFoundException) {
            ApzLogger.e(TAG, anf.toString())
            val `in`: Intent = Intent()
            `in`.putExtra("error", "ANF")
            setResult(RESULT_CANCELED, `in`)
            finish()
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
        }
    }

    private fun handleDefaultOptions() {
        if (FileUtils.isSDCardPresent()) {
            //if filecategory is default , then open app sandbox
            //updated the path for new folder structure followed START
            val sandbox = getApzFile(
                AppzillonMainScreen.SANDBOX_LOC + "/" + AppzillonMainScreen.ASSET_APP_LOC,
                null
            )
            if (!sandbox.exists()) {
                sandbox.mkdirs()
            }
            getFiles((sandbox).listFiles())
        } else {
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.sdcard_unavailable))
                .setNeutralButton(
                    resources.getString(R.string.ok)
                ) { _, _ -> finish() }.show()
        }
    }

    private fun handleFilterOptions() {
        if (FileUtils.isSDCardPresent()) {
            val lFilters = intent.getStringExtra("filter")
            if (!lFilters.isNullOrEmpty()) {
                mFileExtGrp = lFilters.split(",").toTypedArray()
            }
            mFilterEnabled = true
            mFileItems = ArrayList()
            mFilePathItems = ArrayList()
            mFileListAdapter = CustomArrayAdapter(this, R.layout.filebrowser_list_row, mFileItems)
            list?.adapter = mFileListAdapter
            if (mFileExtGrp.isNotEmpty()) {
                filterByExtension(mFileExtGrp)
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.sdcard_unavailable))
                .setNeutralButton(
                    resources.getString(R.string.ok)
                ) { _, _ -> // do nothing
                    finish()
                }.show()
        }
    }

    private fun handleWhenLocationNotEmpty() {
        if (FileUtils.isSDCardPresent()) {
            //Abhishek 23Feb 2015 if location is not null then it will same for both the cases of filter START
            //Abhishek 08 April 2015 updated the path for new folder structure followed START
            FILE_DIR =
                AppzillonMainScreen.SANDBOX_LOC + "/" + AppzillonMainScreen.ASSET_APP_LOC +
                        intent.getStringExtra("location") + "/"
            // if location is not null then it will same for both the cases of filter END
            /*if filter empty, then filter all contents*/
            if ((intent.getStringExtra("filter").isNullOrEmpty())) {
                getFiles(File(FILE_DIR!!).listFiles())
            } else {
                val lfilter = intent.getStringExtra("filter")
                if (!lfilter.isNullOrEmpty()) {
                    mFileExtGrp = lfilter.split(",").toTypedArray()
                }
                mFileItems = ArrayList()
                mFilePathItems = ArrayList()
                mFileListAdapter =
                    CustomArrayAdapter(this, R.layout.filebrowser_list_row, mFileItems)
                list?.adapter = mFileListAdapter
                filterByExtension(mFileExtGrp)
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.sdcard_unavailable))
                .setNeutralButton(resources.getString(R.string.ok)
                ) { _, _ -> finish() }.show()
        }
    }

    private fun handleExternalLocation() {
        if (FileUtils.isSDCardPresent()) {
            val sandbox = File(FILE_DIR!!)
            if (!sandbox.exists()) {
                sandbox.mkdirs()
            }
            getFiles((sandbox).listFiles())
        }
    }

    private fun returnFileDirectory() = if ("Y".equals(
            this.resources.getString(R.string.INTERNALSANDBOX),
            ignoreCase = true
        )
    ) {
        AppzillonMainScreen.SANDBOX_LOC + "/"
    } else {
        try {
            FILE_DIR = FileAccessHelper.getExternalFileDirFile(this)
            val lFileDir = FILE_DIR
            if (lFileDir != null) {
                FILE_DIR = lFileDir.substring(0, lFileDir.lastIndexOf("/Android"))
            }
        } catch (e: Exception) {
            //Sonar fix
        }
        FILE_DIR
    }

    private fun getFiles(files: Array<File>?)
    {
        if (files != null)
        {
            mFileItems = ArrayList()
            mFilePathItems = ArrayList()
            for (file: File in files)
            {
                if (!file.name.equals("screens", ignoreCase = true) && !file.name.equals(
                        "scripts",
                        ignoreCase = true) &&
                    !file.name.equals("sqlite", ignoreCase = true)) {
                    mFileItems.add(file)
                    mFilePathItems.add(file.path)
                }
            }
            mFileListAdapter = CustomArrayAdapter(this, R.layout.filebrowser_list_row, mFileItems)
            list?.adapter = mFileListAdapter
        }
    }

    override fun onBackPressed()
    {
        if (mFilterEnabled) {
            finish()
        } else if (canGoBack && prevDir != null) {
            val prevPath = (prevDir!!.substring(0, prevDir!!.lastIndexOf("/")))
            if (!prevPath.endsWith("/apps")) {
                prevDir = (prevPath.substring(0, prevPath.lastIndexOf("/")) + "/")
                if ((prevPath == FILE_DIR)) {
                    ApzLogger.i(TAG, "If Sd Card Directory")
                    getFiles(getApzFile("$prevPath/", null).listFiles())
                    canGoBack = false
                } else {
                    getFiles(getApzFile("$prevPath/", null).listFiles())
                }
            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    // inner class, generic extension filter
    inner class GenericExtFilter(private val ext: String) : FilenameFilter {
        override fun accept(dir: File, name: String): Boolean {
            return (name.endsWith(ext))
        }
    }

    private fun filterByExtension(ext: Array<String?>)
    {
        if (mFileItems.size > 0) {
            mFileItems.clear()
            mFilePathItems.clear()
        }
        lifecycleScope.launch {
            for (element in ext) {
                if(element !=null){
                    listFileWithExt(FILE_DIR, element.trim())
                }
            }
        }
        mFileListAdapter?.notifyDataSetChanged()
    }

    /**
     * Used to list files with provided file extension type
     * @param fileDir
     * @param fileExt
     */
    private fun listFileWithExt(fileDir: String?, fileExt: String) {
		ApzLogger.i(TAG, "folder:$fileDir  ext:$fileExt");
        processDirectory(getApzFile(fileDir, null), fileExt)
    }

    private fun processDirectory(dir: File, fileExt: String) {
        if (dir.isFile) {
            processFile(dir, fileExt)
        } else if (dir.isDirectory) {
            val listOfFiles = dir.listFiles()
            if (listOfFiles != null) {
                for (i in listOfFiles.indices) processDirectory(listOfFiles[i], fileExt)
            } else {
                ApzLogger.d(TAG, "FILE LIST : [ACCESS DENIED]")
            }
        }
    }

    private fun processFile(file: File, fileExt: String) {
        try {
            if (!file.name.endsWith(fileExt)) return
            // Add file path to another array of files
            mFileItems.add(file)
            mFilePathItems.add(file.path)
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
        }
    }

    companion object {
        private var FILE_DIR: String? = null
    }
}