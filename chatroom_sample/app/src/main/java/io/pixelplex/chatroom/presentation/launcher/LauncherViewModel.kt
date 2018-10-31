package io.pixelplex.chatroom.presentation.launcher

import android.arch.lifecycle.MutableLiveData
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.presentation.base.BaseViewModel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.coroutineContext

/**
 * View model of [LauncherActivity] screen feature
 */
class LauncherViewModel(private val echoFrameworkService: EchoFrameworkService) : BaseViewModel() {

    val startedLibrary = MutableLiveData<Any>()
    val error = MutableLiveData<Exception>()

    /**
     * Initializes echo framework
     */
    fun initialize() {
        launch(parent = job) {
            try {
                withContext(coroutineContext) { echoFrameworkService.restart() }
                startedLibrary.postValue(Any())
            } catch (ex: Exception) {
                error.postValue(ex)
            }
        }
    }
}