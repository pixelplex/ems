package io.pixelplex.chatroom.support

import android.content.Context
import android.support.v7.app.AppCompatDialog
import android.view.ViewGroup
import android.view.Window
import io.pixelplex.chatroom.R
import kotlinx.android.synthetic.main.dialog_add_room.*

/**
 * Custom dialog for room adding feature variants
 */
class AddRoomDialog(context: Context) : AppCompatDialog(context) {

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(inflate<ViewGroup>(context, R.layout.dialog_add_room))

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Defines listener for own room creating action
     */
    fun createOwnRoomListener(success: () -> Unit): AddRoomDialog {
        btnCreateOwnRoom.setOnClickListener {
            this@AddRoomDialog.dismiss()
            success()
        }
        return this
    }

    /**
     * Defines listener for existing room adding action
     */
    fun joinToRoomListener(success: () -> Unit): AddRoomDialog {
        btnJoinToRoom.setOnClickListener {
            this@AddRoomDialog.dismiss()
            success()
        }
        return this
    }

}
