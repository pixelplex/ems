package io.pixelplex.chatroom.presentation.launcher

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.presentation.login.LoginActivity
import io.pixelplex.chatroom.support.toast
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Launcher screen of application
 *
 * Initializes all required services for application
 */
class LauncherActivity : AppCompatActivity(), Observer<Any> {

    private val viewModel by viewModel<LauncherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        viewModel.startedLibrary.observe(this, this)

        viewModel.error.observe(this, Observer { error ->
            error?.let { toast(it.message ?: getString(R.string.unexpected_error)) }
            startLogin()
        })

        viewModel.initialize()
    }

    override fun onChanged(t: Any?) {
        startLogin()
    }

    private fun startLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_up, R.anim.stay)
        finishAffinity()
    }
}