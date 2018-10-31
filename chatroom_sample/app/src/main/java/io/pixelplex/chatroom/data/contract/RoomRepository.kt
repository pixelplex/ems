package io.pixelplex.chatroom.data.contract

import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.model.User

/**
 * Encapsulates logic associated with chat rooms controlling
 */
interface RoomRepository {

    /**
     * Creates room with [ownerNameOrId] and [companionNameOrId] as participants
     */
    suspend fun createRoom(ownerNameOrId: String, password: String, companionNameOrId: String): Room

    /**
     * Checks whether [userNameOrId] can join [contractId] room
     */
    suspend fun canJoinRoom(userNameOrId: String, contractId: String): Boolean

    /**
     * Adds [roomName] room with id [contractId] to [user]'s account
     */
    suspend fun joinRoom(
        contractId: String,
        roomName: String,
        companionNameOrId: String,
        user: User
    )

}