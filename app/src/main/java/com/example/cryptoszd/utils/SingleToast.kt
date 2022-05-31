package com.example.cryptoszd.utils

import android.content.Context
import android.widget.Toast

/**
 * Toasts can't stack, so if a new toast arrives too quickly it will cancel the previous one.
 */
 object SingleToast {
    private var mToast: Toast? = null

    fun show(context: Context?, text: String?, duration: Int) {
        if (mToast != null) mToast!!.cancel()
        mToast = Toast.makeText(context, text, duration)
        mToast!!.show()
    }
 }
