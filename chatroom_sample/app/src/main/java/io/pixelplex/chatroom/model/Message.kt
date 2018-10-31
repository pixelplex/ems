package io.pixelplex.chatroom.model

/**
 * Describes model of chat message
 *
 * Contains message content [message] and message type flag [incoming]
 */
data class Message(val message: String, val incoming: Boolean)