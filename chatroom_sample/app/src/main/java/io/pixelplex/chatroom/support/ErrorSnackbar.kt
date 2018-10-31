package io.pixelplex.chatroom.support

import android.content.Context
import android.support.design.widget.BaseTransientBottomBar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.pixelplex.chatroom.R
import kotlinx.android.synthetic.main.snackbar_newtwork_error.view.*

/**
 * Presents connectivity error and opportunity to reconnect
 */
class ErrorSnackbar private constructor(
    parent: ViewGroup,
    content: View,
    callback: ContentViewCallback
) : BaseTransientBottomBar<ErrorSnackbar>(parent, content, callback) {

    /**
     * Defines [listener] for reconnection button action
     */
    fun setAction(listener: View.OnClickListener): ErrorSnackbar {
        view.vAction.setOnClickListener {
            listener.onClick(view)
        }
        return this
    }

    /**
     * Defines [listener] for reconnection button action
     */
    fun setAction(listener: (View) -> Unit): ErrorSnackbar {
        view.vAction.setOnClickListener {
            listener(view)
        }
        return this
    }

    private fun initAction() {
        view.vAction.setOnClickListener {
            dismiss()
        }
    }

    private fun config(context: Context) {
        val params = this.view.layoutParams as ViewGroup.MarginLayoutParams

        val bottomMargin =
            context.resources.getDimensionPixelSize(R.dimen.error_snackbar_bottom_margin)
        val horizontalMargin: Int =
            context.resources.getDimensionPixelSize(R.dimen.error_snackbar_horizantal_margin)

        params.setMargins(horizontalMargin, 0, horizontalMargin, bottomMargin)

        with(view) {
            layoutParams = params
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorError))
            setPadding(0, 0, 0, 0)

            view.retryProgress.indeterminateDrawable
                .setColorFilter(
                    ContextCompat.getColor(context, android.R.color.white),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                )
        }

        ViewCompat.setElevation(this.view, 0f)
    }

    /**
     * Switch action button on progress and vise versa
     */
    fun progress(show: Boolean) {
        setVisibility(show, view.retryProgress)
        setVisibility(!show, view.vAction)
    }

    private class ContentViewCallback(private val content: View) :
        BaseTransientBottomBar.ContentViewCallback {

        override fun animateContentIn(delay: Int, duration: Int) {
            content.scaleY = 0f
            content.animate().scaleY(1f).setDuration(duration.toLong())
                .setStartDelay(delay.toLong()).start()
        }

        override fun animateContentOut(delay: Int, duration: Int) {
            content.scaleY = 1f
            content.animate().scaleY(0f).setDuration(duration.toLong())
                .setStartDelay(delay.toLong()).start()
        }
    }

    companion object {

        /**
         * Configures error snackbar
         */
        fun make(parent: ViewGroup, durationValue: Int): ErrorSnackbar {
            val content = LayoutInflater.from(parent.context)
                .inflate(R.layout.snackbar_newtwork_error, parent, false)

            return ErrorSnackbar(parent, content, ContentViewCallback(content))
                .apply {
                    initAction()
                    config(parent.context)
                    duration = durationValue
                }
        }
    }
}
