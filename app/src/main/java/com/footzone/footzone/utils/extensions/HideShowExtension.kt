
package com.footzone.footzone.utils.extensions

import android.view.View

fun View.hide() {
    try {
        this.visibility = View.GONE
    } catch (e: Exception) {
    }
}

fun View.show() {
    try {
        this.visibility = View.VISIBLE
    } catch (e: Exception) {
    }
}

fun View.invisible() {
    try {
        this.visibility = View.INVISIBLE
    } catch (e: Exception) {
    }
}