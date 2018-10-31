package io.pixelplex.chatroom.support

import android.support.v7.widget.RecyclerView

/**
 * Extension of  [RecyclerView.Adapter]
 *
 * Describes functionality of mutable(modifiable) list adapter
 */
abstract class ModifiableRecyclerAdapter<T, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {

    var items: MutableList<T> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    /**
     * Adds item to the end of list
     *
     * @param item Element for adding
     */
    fun add(item: T) {
        this.items.add(item)
        notifyItemInserted(items.indexOf(item))
    }

    /**
     * Adds all elements in list [items] to the end of existing list
     *
     * @param items Items list for adding
     */
    fun addAll(items: List<T>) {
        val startPosition = itemCount
        this.items.addAll(items)
        notifyItemRangeInserted(startPosition, items.size)
    }

    /**
     * Inserts all elements of list [items] in position [position] of existing list
     *
     * @param position Inserting position
     * @param items Items list for inserting
     */
    fun addAll(position: Int, items: List<T>) {
        this.items.addAll(position, items)
        notifyItemRangeInserted(position, items.size)
    }

    /**
     * Replaces item in required position with required item
     *
     * @param position Item position for replacing
     * @param item Item for replacing
     */
    fun replace(position: Int, item: T) {
        this.items[position] = item
        notifyItemChanged(position)
    }

    /**
     * Removes all items in adapter
     */
    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

}