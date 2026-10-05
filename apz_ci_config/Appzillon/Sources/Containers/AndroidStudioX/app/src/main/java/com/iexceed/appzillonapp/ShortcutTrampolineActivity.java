package com.iexceed.appzillonapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class ShortcutTrampolineActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trampoline);
        Intent intent = new Intent(this, AppzillonMainScreen.class);
        intent.putExtra("type", "com.iexceed.shortcut");
        intent.putExtra("action", getIntent().getAction());
        startActivity(intent);
        finish();
    }
}
