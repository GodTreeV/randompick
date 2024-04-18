package com.app.randompick

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context.toast(msg: () -> String) {
    Toast.makeText(this, msg(), Toast.LENGTH_SHORT).show()
}

fun mainLaunch(block: () ->Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block.invoke()
    }
}

fun ioLaunch(bk: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        bk.invoke()
    }
}

fun log(msg : () -> String) {
    Log.e("ysw", msg())
}