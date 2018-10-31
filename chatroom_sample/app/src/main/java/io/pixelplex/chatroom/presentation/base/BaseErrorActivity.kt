package io.pixelplex.chatroom.presentation.base

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar.LENGTH_INDEFINITE
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.support.ErrorSnackbar
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Extension of [BaseActivity] that os capable to show connectivity error snackbar
 * [ErrorSnackbar] to user
 */
abstract class BaseErrorActivity : BaseActivity() {

    private val errorSnackbar by lazy {
        ErrorSnackbar.make(findViewById(R.id.errorContainer), LENGTH_INDEFINITE)
            .setAction {
                reconnect()
            }
    }

    private val viewModel by viewModel<BaseActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.connectionState.observe(this, Observer { state ->
            state?.let {
                errorSnackbar.progress(false)
                if (it) errorSnackbar.dismiss() else errorSnackbar.show()
            }
        })
    }

    private fun reconnect() {
        errorSnackbar.progress(true)
        viewModel.reconnect()
    }

    override fun onResume() {
        super.onResume()
        viewModel.observeState()
    }

    override fun onPause() {
        viewModel.cancelStateObserve()
        super.onPause()
    }

}