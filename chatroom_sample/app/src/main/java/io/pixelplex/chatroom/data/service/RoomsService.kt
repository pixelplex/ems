package io.pixelplex.chatroom.data.service

import io.pixelplex.chatroom.data.UiException
import io.pixelplex.chatroom.model.Room

/**
 * Describes functionality of rooms persistence service
 */
interface RoomsService {

    /**
     * Fetches rooms of [accountId] account
     */
    fun getRooms(accountId: String): List<Room>

    /**
     * Adds [room] to [accountId] account
     */
    fun addRoom(accountId: String, room: Room): List<Room>

    /**
     * Deletes [room] from [accountId] account
     */
    fun deleteRoom(accountId: String, room: Room): List<Room>

    /**
     * Adds account [accountId] if it does not exist
     */
    fun addNonExistentAccount(accountId: String)
}

/**
 * Base exception of rooms persistence service
 */
class RoomException : UiException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}