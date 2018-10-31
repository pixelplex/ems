package io.pixelplex.chatroom.model

import java.io.Serializable

/**
 * Chat account model for persistence purposes
 */
data class Account(val accountId: String, val rooms: MutableList<Room> = mutableListOf()) :
    Serializable

/**
 * Adds room in account
 */
fun Account.addRoom(room: Room) = this.rooms.add(room)

/**
 * Removes room from account
 */
fun Account.removeRoom(room: Room) = this.rooms.remove(room)