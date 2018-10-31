package io.pixelplex.chatroom.presentation.messages

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.presentation.base.BaseErrorActivity
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Presents single room messages feature
 */
class ChatActivity : BaseErrorActivity() {

    private val room by lazy { intent.getSerializableExtra(ROOM_KEY) as Room }

    private val viewModel by viewModel<ChatViewModel>()
    private val chatAdapter by lazy {
        MessagesAdapter(room)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        tvTitle.text = room.name
        ivBack.setOnClickListener {
            this.onBackPressed()
        }

        val manager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }

        with(rvMessages) {
            layoutManager = manager
            adapter = chatAdapter
        }

        viewModel.messages.observe(this, Observer { messages ->
            messages?.let {
                chatAdapter.items = it.toMutableList()
                rvMessages.scrollToPosition(0)
            }
        })
        viewModel.error.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(
                    this@ChatActivity,
                    R.string.message_uploading_error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        etInput.addTextChangedListener(textWatcher)

        btnSend.setOnClickListener {
            send(etInput.text.toString())
        }

        viewModel.startMessagesFetching(room)
    }

    private fun send(message: String) {
        etInput.setText("")
        viewModel.send(message, room)
    }

    override fun onDestroy() {
        viewModel.stopMessagesFetching()
        super.onDestroy()
    }

    private fun updateSendButtonEnabling() {
        btnSend.isEnabled = etInput.text.isNotEmpty()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            updateSendButtonEnabling()
        }
    }

    companion object {
        const val ROOM_KEY = "room"
    }

}
