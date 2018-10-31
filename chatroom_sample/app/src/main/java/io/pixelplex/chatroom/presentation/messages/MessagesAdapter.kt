package io.pixelplex.chatroom.presentation.messages

import android.view.ViewGroup
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.model.Message
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.support.BaseViewHolder
import io.pixelplex.chatroom.support.ModifiableRecyclerAdapter
import io.pixelplex.chatroom.support.setVisibility
import kotlinx.android.synthetic.main.item_incoming_message.view.*
import kotlinx.android.synthetic.main.item_outcoming_message.view.*

/**
 * Adapter for room messages list
 */
class MessagesAdapter(private val room: Room) :
    ModifiableRecyclerAdapter<Message, BaseViewHolder<MessagesAdapter.MessageWrapper>>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): BaseViewHolder<MessageWrapper> {
        return when (type) {
            INCOMING_MESSAGE_TYPE -> IncomingMessageViewHolder(parent, room)
            OUTCOMING_MESSAGE_TYPE -> OutcomingMessageViewHolder(parent, room)
            else -> throw IllegalArgumentException("Unknown type $type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<MessageWrapper>, position: Int) {
        holder.bind(MessageWrapper(items[position], needShowName(position)))
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].incoming) {
            true -> INCOMING_MESSAGE_TYPE
            false -> OUTCOMING_MESSAGE_TYPE
        }
    }

    private fun needShowName(position: Int) =
        position == itemCount - 1 || items[position].incoming != items[position + 1].incoming

    /**
     * Holder for incoming message model
     */
    class IncomingMessageViewHolder(parent: ViewGroup, private val room: Room) :
        BaseViewHolder<MessageWrapper>(parent, R.layout.item_incoming_message) {

        override fun bind(value: MessageWrapper) {
            itemView.incomingContent.text = value.message.message

            itemView.tvCompanionName.text = room.companionName
            setVisibility(value.needShowName, itemView.tvCompanionName)
        }

    }

    /**
     * Holder for outcoming message model
     */
    class OutcomingMessageViewHolder(parent: ViewGroup, private val room: Room) :
        BaseViewHolder<MessageWrapper>(parent, R.layout.item_outcoming_message) {

        override fun bind(value: MessageWrapper) {
            itemView.outcomingContent.text = value.message.message

            itemView.tvOwnerName.text = room.ownerName
            setVisibility(value.needShowName, itemView.tvOwnerName)
        }

    }

    /**
     * Wrapper for [Message] model.
     *
     * Contains additional information about user name showing
     */
    data class MessageWrapper(val message: Message, val needShowName: Boolean)

    companion object {
        private const val INCOMING_MESSAGE_TYPE = 1
        private const val OUTCOMING_MESSAGE_TYPE = 2
    }

}