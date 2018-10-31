package io.pixelplex.chatroom.model

import java.io.Serializable

/**
 * Describes single room model
 *
 * Contains information about room name and participants, contract id for room
 */
data class Room(
    var name: String = "",
    val ownerName: String,
    val companionName: String,
    val contractId: String
) : Serializable