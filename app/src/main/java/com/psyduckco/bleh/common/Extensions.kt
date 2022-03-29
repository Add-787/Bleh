package com.psyduckco.bleh.common

import android.app.Activity
import android.widget.Toast

internal fun Activity.makeToast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_LONG
    ).show()
}