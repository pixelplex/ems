package io.pixelplex.chatroom.presentation.base

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.Job

/**
 * Base implementation of android [ViewModel]
 *
 * Cancels all coroutines jobs with [job] as parent
 */
open class BaseViewModel : ViewModel() {

    /**
     * Parent job for all coroutines that must be stopped in [onCleared] method
     */
    protected val job = Job()

    override fun onCleared() {
        job.cancel()
    }

}