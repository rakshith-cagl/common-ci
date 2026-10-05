package com.iexceed.utils.misc

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object ApzMisc {
    fun showDialog(
        context: Context, title: String, msg: String, btnPos: String?, btnNeg: String?,
        onClickCallback: (which: Int) -> Unit
    ) {
        val ocListener = DialogInterface.OnClickListener() { _, which ->
            onClickCallback(which)
        }
        val db = AlertDialog.Builder(context)
        db.setCancelable(false) // can be changed
        db.setTitle(title)
        db.setMessage(msg)
        if (btnPos != null) db.setPositiveButton(btnPos, ocListener)
        if (btnNeg != null) db.setNegativeButton(btnNeg, ocListener)
        db.setIcon(android.R.drawable.ic_dialog_alert)
        db.show()
    }
}