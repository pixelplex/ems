package io.pixelplex.chatroom.presentation.main

import android.view.ViewGroup
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.support.BaseViewHolder
import io.pixelplex.chatroom.support.ModifiableRecyclerAdapter
import kotlinx.android.synthetic.main.item_detail.view.*

/**
 * Adapter for rooms list of main  application screen
 */
class RoomsAdapter(
    private val clickAction: (Room) -> Unit,
    private val editAction: (Room) -> Unit,
    private val deleteAction: (Room) -> Unit
) : ModifiableRecyclerAdapter<Room, RoomsAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RoomViewHolder {
        return RoomViewHolder(clickAction, editAction, deleteAction, parent)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * Holder for single room item in list
     */
    class RoomViewHolder(
        private val clickAction: (Room) -> Unit,
        private val shareAction: (Room) -> Unit,
        private val deleteAction: (Room) -> Unit,
        parent: ViewGroup
    ) :
        BaseViewHolder<Room>(parent, R.layout.item_detail) {

        override fun bind(value: Room) {
            with(itemView) {
                setOnClickListener { clickAction(value) }
                tvTitle.text = value.name
                tvId.text = "[${value.contractId}]"

                tvParticipants.text = itemView.context.getString(
                    R.string.participantsLabelPattern,
                    value.ownerName,
                    value.companionName
                )

                ivDelete.setOnClickListener {
                    deleteAction(value)
                }

                ivShare.setOnClickListener { shareAction(value) }
            }
        }

    }

}