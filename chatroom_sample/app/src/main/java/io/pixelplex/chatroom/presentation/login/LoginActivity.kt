package io.pixelplex.chatroom.presentation.login

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.data.auth.RegistrationException
import io.pixelplex.chatroom.presentation.base.BaseErrorActivity
import io.pixelplex.chatroom.presentation.main.MainActivity
import io.pixelplex.chatroom.presentation.registration.RegistrationActivity
import io.pixelplex.chatroom.support.hideSoftKeyboard
import io.pixelplex.chatroom.support.toast
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Presents user login feature
 */
class LoginActivity : BaseErrorActivity() {

    private val viewModel by viewModel<LoginViewModel>()

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
    }

    private fun initViews() {
        lytRoot.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideSoftKeyboard()
        }

        initBackgroundStateForView(etLogin, vBgLogin)
        initBackgroundStateForView(etPassword, vBgPassword)

        tvEntire.setOnClickListener {
            startActivity(RegistrationActivity.getIntent(this))
            finishWithSlideUp()
        }

        tvEnter.setOnClickListener {
            showLoader()
            lytRoot.requestFocus()
            viewModel.login(etLogin.text.toString(), etPassword.text.toString())
        }

        viewModel.loginState.observe(this, Observer {
            hideLoader()
            startActivity(Intent(this, MainActivity::class.java))
            finishWithStay()
        })

        viewModel.error.observe(this, Observer { error ->
            hideLoader()
            (error as? RegistrationException)?.let {
                tvError.visibility = View.VISIBLE
            } ?: error?.let { toast(it.message ?: "") }
        })

    }

    private fun initBackgroundStateForView(mainView: View, viewBackground: View) {
        mainView.setOnFocusChangeListener { _, hasFocus ->
            clearError()
            viewBackground.setBackgroundResource(
                if (hasFocus) {
                    R.drawable.bg_text_input_selected
                } else {
                    R.drawable.bg_text_input_unselected
                }
            )
        }
    }

    private fun clearError() {
        tvError.visibility = View.GONE
        vBgLogin.setBackgroundResource(R.drawable.bg_text_input_unselected)
        vBgPassword.setBackgroundResource(R.drawable.bg_text_input_unselected)
    }

    private fun finishWithStay() {
        hideSoftKeyboard()
        finish()
        overridePendingTransition(R.anim.stay, R.anim.slide_down)
    }

    private fun finishWithSlideUp() {
        hideSoftKeyboard()
        finish()
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }

}