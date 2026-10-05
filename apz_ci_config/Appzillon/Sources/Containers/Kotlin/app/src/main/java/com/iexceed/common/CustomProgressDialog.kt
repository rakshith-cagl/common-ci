package com.iexceed.common



import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent

class CustomProgressDialog(context: Context? ) : AlertDialog( context) {

    internal inner class MyOnKeyListener : DialogInterface.OnKeyListener {
        override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
            return keyCode == KeyEvent.KEYCODE_SEARCH

        }

    }

    init {
        setOnKeyListener(MyOnKeyListener())
    }
}
