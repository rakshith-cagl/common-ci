package com.iexceed.appzillonapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle


class ShortcutTrampolineActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut_trampoline)
        val intent = Intent(this, AppzillonMainScreen::class.java)
        intent.putExtra("type", "com.iexceed.shortcut")
        intent.putExtra("action", getIntent().action)
        startActivity(intent)
        finish()
    }
}
