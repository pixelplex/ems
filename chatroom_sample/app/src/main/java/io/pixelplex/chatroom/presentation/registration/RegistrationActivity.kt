package io.pixelplex.chatroom.presentation.registration

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.data.auth.RegistrationException
import io.pixelplex.chatroom.presentation.base.BaseErrorActivity
import io.pixelplex.chatroom.presentation.login.LoginActivity
import io.pixelplex.chatroom.presentation.main.MainActivity
import io.pixelplex.chatroom.support.hideSoftKeyboard
import kotlinx.android.synthetic.main.activity_registration.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Presents user registration feature screen
 */
class RegistrationActivity : BaseErrorActivity() {

    private val viewModel by viewModel<RegistrationViewModel>()

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, RegistrationActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        initViews()
        initOutputs()
    }

    private fun initViews() {
        lytRoot.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideSoftKeyboard()
            }
        }

        initBackgroundStateForView(etLogin, vBgLogin)
        initBackgroundStateForView(etPassword, vBgPassword)
        initBackgroundStateForView(etRepeatPassword, vBgRepeatPassword)

        tvEntire.setOnClickListener {
            startActivity(LoginActivity.getIntent(this))
            finishWithSlideUp()
        }

        tvEnter.setOnClickListener {
            lytRoot.requestFocus()
            showLoader()
            viewModel.register(
                etLogin.text.toString(),
                etPassword.text.toString(),
                etRepeatPassword.text.toString()
            )
        }

    }

    private fun initOutputs() {
        viewModel.registrationState.observe(this, Observer {
            hideLoader()
            startActivity(Intent(this, MainActivity::class.java))
            finishWithStay()
        })

        viewModel.error.observe(this, Observer { error ->
            hideLoader()
            (error as? RegistrationException)?.let {
                tvError.visibility = View.VISIBLE
            } ?: error?.let {
                Toast.makeText(this, error.message ?: "", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearError() {
        tvError.visibility = View.GONE

        vBgLogin.setBackgroundResource(R.drawable.bg_text_input_unselected)
        vBgPassword.setBackgroundResource(R.drawable.bg_text_input_unselected)
        vBgRepeatPassword.setBackgroundResource(R.drawable.bg_text_input_unselected)
    }

    private fun showError(message: String) {
        showError(message, ErrorField.UNKNOWN)
    }

    private fun showError(message: String, errorField: ErrorField) {
        when (errorField) {
            ErrorField.PASSWORD -> passwordError()
            ErrorField.PASSWORDS -> passwordsError()
            ErrorField.EMAIL -> emailError()
            else -> error()
        }

        showErrorText(message)
    }

    private fun passwordError() {
        etPassword.requestFocus()
        vBgPassword.setBackgroundResource(R.drawable.bg_text_input_error)
    }

    private fun passwordsError() {
        etPassword.requestFocus()
        vBgPassword.setBackgroundResource(R.drawable.bg_text_input_error)
        vBgRepeatPassword.setBackgroundResource(R.drawable.bg_text_input_error)
    }

    private fun emailError() {
        etLogin.requestFocus()
        vBgLogin.setBackgroundResource(R.drawable.bg_text_input_error)
    }

    private fun error() {
        etLogin.requestFocus()
        vBgLogin.setBackgroundResource(R.drawable.bg_text_input_error)
        vBgPassword.setBackgroundResource(R.drawable.bg_text_input_error)
        vBgRepeatPassword.setBackgroundResource(R.drawable.bg_text_input_error)
    }

    private fun initBackgroundStateForView(mainView: View, viewBackground: View) {
        mainView.setOnFocusChangeListener { _, hasFocus ->
            clearError()
            viewBackground.setBackgroundResource(if (hasFocus) R.drawable.bg_text_input_selected else R.drawable.bg_text_input_unselected)
        }
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

    private fun showErrorText(message: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = message
    }
}