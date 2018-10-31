package io.pixelplex.chatroom.data.service.internal

import io.pixelplex.chatroom.data.cache.Cache
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.model.User

/**
 * Implementation of [UserService] based on [Cache] implementations
 */
class UserServiceImpl(private val cache: Cache) : UserService {

    override fun getUser(): User? = cache[USER_KEY]

    override fun saveUser(user: User) = cache.put(USER_KEY, user)

    companion object {
        const val USER_KEY = "userKey"
    }

}