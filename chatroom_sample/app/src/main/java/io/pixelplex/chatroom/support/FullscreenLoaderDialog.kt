package io.pixelplex.chatroom.support

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import io.pixelplex.chatroom.R

/**
 * Fullscreen loading dialog
 */
class FullscreenLoaderDialog constructor(context: Context, theme: Int = R.style.LoaderDialog) :
    Dialog(context, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
        setOnDismissListener {
            it.dismiss()
        }
    }

}