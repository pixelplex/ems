package io.pixelplex.chatroom.support

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import org.echo.mobile.framework.Callback
import org.echo.mobile.framework.exception.LocalException
import kotlin.coroutines.experimental.Continuation

/**
 * Shows virtual keyboard for activity
 */
fun AppCompatActivity.showSoftKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(currentFocus, InputMethodManager.SHOW_FORCED)
}

/**
 * Hides virtual keyboard for activity
 */
fun AppCompatActivity.hideSoftKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

/**
 * Shows virtual keyboard for dialog
 */
fun AppCompatDialog.showSoftKeyboard() {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(currentFocus, InputMethodManager.SHOW_FORCED)
}

/**
 * Hides virtual keyboard for dialog
 */
fun AppCompatDialog.hideSoftKeyboard() {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

/**
 * Clears focus on current window
 */
fun AppCompatActivity.clearFocus() {
    window.decorView.clearFocus()
}

/**
 * Inflates view with required [layout]
 */
fun <T : View> inflate(parent: ViewGroup, layout: Int, attach: Boolean = false): T =
    LayoutInflater.from(parent.context).inflate(layout, parent, attach) as T

/**
 * Inflates view with required [layoutId]
 */
fun <T : View> inflate(context: Context, layoutId: Int): T =
    LayoutInflater.from(context).inflate(layoutId, null) as T

/**
 * Maps [Continuation] to [Callback] functions
 */
fun <T, R> Continuation<T>.toMapCallback(success: (Continuation<T>, R) -> Unit): Callback<R> {
    return object : Callback<R> {
        override fun onError(error: LocalException) {
            this@toMapCallback.resumeWithException(error)
        }

        override fun onSuccess(result: R) {
            success(this@toMapCallback, result)
        }
    }
}

/**
 * Maps [Continuation] to [Callback] functions with required [success] and [error] actions
 */
fun <T, R> Continuation<T>.toMapCallback(
    success: (Continuation<T>, R) -> Unit,
    error: (Continuation<T>, LocalException) -> Unit
): Callback<R> {
    return object : Callback<R> {
        override fun onError(error: LocalException) {
            error(this@toMapCallback, error)
        }

        override fun onSuccess(result: R) {
            success(this@toMapCallback, result)
        }
    }
}

/**
 * Maps [Continuation] to [Callback] functions
 */
fun <T> Continuation<T>.toCallback(): Callback<T> {
    return object : Callback<T> {
        override fun onError(error: LocalException) {
            this@toCallback.resumeWithException(error)
        }

        override fun onSuccess(result: T) {
            this@toCallback.resume(result)
        }
    }
}

fun <T> Continuation<T>.toMapErrorCallback(
    error: (Continuation<T>, LocalException) -> Unit
): Callback<T> {
    return object : Callback<T> {
        override fun onError(error: LocalException) {
            error(this@toMapErrorCallback, error)
        }

        override fun onSuccess(result: T) {
            this@toMapErrorCallback.resume(result)
        }
    }
}




