package io.pixelplex.chatroom.support

import android.content.Context
import android.support.annotation.StringRes
import android.view.View
import android.widget.Toast

/**
 * Hides [views] according to [visible] flag
 */
fun setVisibility(visible: Boolean, vararg views: View) =
    views.forEach { it.visibility = if (visible) View.VISIBLE else View.GONE }

/**
 * Displays toast with required [textResource]
 */
fun Context.toast(@StringRes textResource: Int, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, textResource, length).show()

/**
 * Displays toast with required [text]
 */
fun Context.toast(@StringRes text: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, length).show()