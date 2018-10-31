package io.pixelplex.chatroom.presentation.base

import android.support.v7.app.AppCompatActivity
import io.pixelplex.chatroom.support.FullscreenLoaderDialog

/**
 * Base implementation of [AppCompatActivity] with loader feature
 */
abstract class BaseActivity : AppCompatActivity() {

    private val loader by lazy { FullscreenLoaderDialog(this) }

    /**
     * Shows user screen loader
     */
    fun showLoader() {
        loader.show()
    }

    /**
     * Hides user screen loader
     */
    fun hideLoader() {
        loader.dismiss()
    }


}