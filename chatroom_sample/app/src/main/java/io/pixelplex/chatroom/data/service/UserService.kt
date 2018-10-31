package io.pixelplex.chatroom.data.service

import io.pixelplex.chatroom.model.User

/**
 * Describes functionality of user controlling service
 */
interface UserService {

    /**
     * Returns current user of application
     */
    fun getUser(): User?

    /**
     * Persists current [user] of application
     */
    fun saveUser(user: User)

}