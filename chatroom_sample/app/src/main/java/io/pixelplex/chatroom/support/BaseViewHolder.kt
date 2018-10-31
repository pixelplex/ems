package io.pixelplex.chatroom.support

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Base implementation of recycler view [RecyclerView.ViewHolder]
 */
abstract class BaseViewHolder<in T>(parent: ViewGroup, @LayoutRes itemLayout: Int) :
    RecyclerView.ViewHolder(inflate(parent, itemLayout)) {

    /**
     * Fills required views with [value] information
     */
    abstract fun bind(value: T)

}