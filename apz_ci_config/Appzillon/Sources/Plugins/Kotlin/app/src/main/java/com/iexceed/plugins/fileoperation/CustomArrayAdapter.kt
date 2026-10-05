package com.iexceed.plugins.fileoperation

import android.app.Activity
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.iexceed.appzillonapp.R
import java.io.File

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class CustomArrayAdapter (private val context: Activity, resource: Int, var items: ArrayList<File>) :
    ArrayAdapter<File?>(
        context, R.layout.filebrowser_list_row, items as List<File?>
    ) {
    lateinit var files: Array<File>
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.filebrowser_list_row, null, true)
        files = items.toTypedArray()
        val txtTitle = rowView.findViewById<View>(R.id.rowtext) as TextView
        val imageView = rowView.findViewById<View>(R.id.icon) as ImageView
        //  TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        txtTitle.text = files[position].name
        if (files[position].isDirectory) {
            imageView.setImageResource(R.drawable.folder)
        } else {
            handleIfFile(position, imageView)
        }
        // extratxt.setText("Description "+itemname[position]);
        return rowView
    }

    private fun handleIfFile(position: Int, imageView: ImageView) {
        val selectedUri = Uri.fromFile(files[position])
        val fileExt = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)
        if (mimeType != null) {
            if (fileExt.equals(
                    "pdf",
                    ignoreCase = true
                )
            ) imageView.setImageResource(R.drawable.pdf)
            else if (fileExt.equals(
                    "txt",
                    ignoreCase = true
                ) || fileExt.equals("text", ignoreCase = true)
            ) imageView.setImageResource(R.drawable.text)
            else if (fileExt.equals(
                    "doc",
                    ignoreCase = true
                ) || fileExt.equals("docx", ignoreCase = true)
            ) imageView.setImageResource(R.drawable.doc)
            else if (fileExt.equals(
                    "ppt",
                    ignoreCase = true
                ) || fileExt.equals("pptx", ignoreCase = true) || fileExt.equals(
                    "pptm",
                    ignoreCase = true
                )
            ) imageView.setImageResource(R.drawable.ppt)
            else if (fileExt.equals(
                    "war",
                    ignoreCase = true
                ) || fileExt.equals("zip", ignoreCase = true) || fileExt.equals(
                    "jar",
                    ignoreCase = true
                )
            ) imageView.setImageResource(R.drawable.archive)

            checkOtherFormats(fileExt, imageView)
        }
        else imageView.setImageResource(R.drawable.unknownfile)
    }

    private fun checkOtherFormats(fileExt: String?, imageView: ImageView) {
        if (fileExt.equals(
                "html",
                ignoreCase = true
            ) || fileExt.equals("htm", ignoreCase = true)
        ) imageView.setImageResource(R.drawable.html)
        else if (fileExt.equals(
                "apk",
                ignoreCase = true
            )
        ) imageView.setImageResource(R.drawable.apk)
        else if (fileExt.equals(
                "xls",
                ignoreCase = true
            ) || fileExt.equals("xlsx", ignoreCase = true)
        ) imageView.setImageResource(R.drawable.xlsx)
        else if (fileExt.equals(
                "png",
                ignoreCase = true
            ) || fileExt.equals("jpeg", ignoreCase = true) || fileExt.equals(
                "jpg",
                ignoreCase = true
            )
        ) imageView.setImageResource(R.drawable.image)
        else if (fileExt.equals(
                "mp3",
                ignoreCase = true
            )
        ) imageView.setImageResource(R.drawable.audio)
        else if (fileExt.equals(
                "mp4",
                ignoreCase = true
            )
        ) imageView.setImageResource(R.drawable.video_file)
        else if (fileExt.equals(
                "xml",
                ignoreCase = true
            )
        ) imageView.setImageResource(R.drawable.xml_file)
        else imageView.setImageResource(R.drawable.simplefile)
    }
}