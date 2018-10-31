package io.pixelplex.chatroom.presentation.addingroom.createownroom

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.presentation.base.BaseErrorActivity
import kotlinx.android.synthetic.main.activity_create_own_room.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Presents feature of creating own chat room
 */
class CreateOwnRoomActivity : BaseErrorActivity() {

    private val viewModel by viewModel<CreateOwnRoomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_own_room)

        btnCreate.setOnClickListener {
            val companion = etCompanion.text.toString()
            val roomName = etRoomName.text.toString()

            if (companion.isEmpty() || companion.isBlank() || roomName.isEmpty() || roomName.isBlank()) {
                tvError.text = getString(R.string.error_field_is_required)

            } else {
                showLoader()
                viewModel.newRoom(roomName, companion)
            }
        }

        viewModel.success.observe(this, Observer {
            hideLoader()
            finish()
        })

        viewModel.error.observe(this, Observer {
            it?.let { error ->
                hideLoader()
                when (error.type) {
                    CreateOwnRoomException.Type.CREATING_ERROR ->
                        tvError.setText(R.string.error_create_room)

                    CreateOwnRoomException.Type.ACCOUNT_NOT_FOUND ->
                        tvError.setText(R.string.error_account_not_found)
                }
            }
        })

        ivBack.setOnClickListener {
            super.onBackPressed()
        }
    }
}