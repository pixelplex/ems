package io.pixelplex.chatroom.presentation.messages

import android.arch.lifecycle.MutableLiveData
import io.pixelplex.chatroom.data.contract.MessageRepository
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.model.Message
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.presentation.base.BaseViewModel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.coroutineContext

/**
 * View model for [ChatActivity] screen feature
 *
 * Contains all logic of information preparing for user presentation
 */
class ChatViewModel(
    private val userService: UserService,
    private val messageRepository: MessageRepository
) : BaseViewModel() {

    val messages = MutableLiveData<List<Message>>()
    val error = MutableLiveData<Exception>()

    private var messagesChanel: ReceiveChannel<List<Message>>? = null

    /**
     * Begins to observe user messages in required [room]
     */
    fun startMessagesFetching(room: Room) {
        if (messagesChanel == null) {
            launch(parent = job) {
                val user = userService.getUser()!!
                messagesChanel =
                        messageRepository.startMessagesObserving(
                            user.name,
                            user.password,
                            room.companionName,
                            room.contractId
                        )

                messagesChanel?.consumeEach {
                    messages.postValue(it.reversed())
                }
            }
        }
    }

    /**
     * Stops to observe user messages
     */
    fun stopMessagesFetching() {
        if (messagesChanel != null) {
            launch(parent = job) {
                messageRepository.stopMessagesObserving()
                messagesChanel?.cancel()
                messagesChanel = null
            }
        }
    }

    /**
     * Send [message] in required [room]'s contract
     */
    fun send(message: String, room: Room) {
        launch(parent = job) {
            try {
                withContext(coroutineContext) {
                    messageRepository.uploadMessage(
                        userService.getUser()!!.name,
                        userService.getUser()!!.password,
                        room.companionName, message, room.contractId
                    )
                }
            } catch (e: Exception) {
                error.postValue(e)
            }
        }
    }

}
