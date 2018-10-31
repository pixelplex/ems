package io.pixelplex.chatroom.presentation.main

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.presentation.addingroom.createownroom.CreateOwnRoomActivity
import io.pixelplex.chatroom.presentation.addingroom.jointoroom.JoinToRoomActivity
import io.pixelplex.chatroom.presentation.base.BaseErrorActivity
import io.pixelplex.chatroom.presentation.messages.ChatActivity
import io.pixelplex.chatroom.presentation.messages.ChatActivity.Companion.ROOM_KEY
import io.pixelplex.chatroom.presentation.qr.ShareDataActivity
import io.pixelplex.chatroom.support.AddRoomDialog
import io.pixelplex.chatroom.support.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Presents rooms controlling feature of chat application
 */
class MainActivity : BaseErrorActivity() {

    private val viewModel by viewModel<MainViewModel>()
    private val roomsAdapter by lazy {
        RoomsAdapter(
            {
                startActivity(Intent(this, ChatActivity::class.java).apply {
                    putExtra(ROOM_KEY, it)
                })
            },
            {
                startActivity(
                    ShareDataActivity.newIntent(
                        it.contractId, it.name,
                        viewModel.getCompanionId(it), this
                    )
                )
            },
            {
                viewModel.deleteRoom(it)
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showLoader()

        tvTitle.text = getString(R.string.rooms_screen_title)
        rvDoors.adapter = roomsAdapter

        ivAdd.setOnClickListener { showRoomAddingDialog() }

        with(viewModel) {
            rooms.observe(this@MainActivity, Observer { rooms ->
                hideLoader()
                rooms?.let { roomsAdapter.items = it.toMutableList() }
            })
            user.observe(this@MainActivity, Observer {
                it?.let { user -> tvUserName.text = user.name }
            })

            deleteError.observe(this@MainActivity, Observer {
                toast(R.string.delete_room_error)
            })
            fetchError.observe(this@MainActivity, Observer {
                toast(R.string.fetch_rooms_error)
            })
        }
    }

    private fun showRoomAddingDialog() =
        AddRoomDialog(this)
            .createOwnRoomListener {
                startActivity(Intent(this, CreateOwnRoomActivity::class.java))

            }
            .joinToRoomListener {
                startActivity(Intent(this, JoinToRoomActivity::class.java))
            }
            .show()

    override fun onResume() {
        super.onResume()
        viewModel.fetchUser()
        viewModel.fetchRooms()
    }

}
