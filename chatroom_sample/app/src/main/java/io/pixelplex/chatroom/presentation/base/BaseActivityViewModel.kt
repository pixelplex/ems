package io.pixelplex.chatroom.presentation.base

import android.arch.lifecycle.MutableLiveData
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch

/**
 * Base implementation of user screen view model
 *
 * Contains functionality of reconnecting to echo node after connectivity problems and
 * observing current connectivity state
 */
class BaseActivityViewModel(private val echoFrameworkService: EchoFrameworkService) :
    BaseViewModel() {

    val connectionState = MutableLiveData<Boolean>()

    private var channel: ReceiveChannel<Boolean>? = null

    /**
     * Reconnects to echo node
     */
    fun reconnect() {
        launch(parent = job) {
            try {
                echoFrameworkService.restart()
            } catch (exception: Exception) {
                connectionState.postValue(false)
            }
        }
    }

    /**
     * Observes state of connectivity to echo node
     */
    fun observeState() {
        launch(parent = job) {
            channel = echoFrameworkService.observeState()
            channel!!.consumeEach { state ->
                connectionState.postValue(state)
            }
        }
    }

    /**
     * Cancels echo node connectivity state observing
     */
    fun cancelStateObserve() {
        channel?.cancel()
        channel = null
    }

}