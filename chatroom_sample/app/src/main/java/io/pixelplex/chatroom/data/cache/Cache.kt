package io.pixelplex.chatroom.data.cache


/**
 * Describes application's object cache functionality
 */
interface Cache {

    /**
     * Put value in cache by specific key
     *
     * @param key String Object key
     * @param value T Value to save in cache
     */
    fun <T> put(key: String, value: T)

    /**
     * Read object from cache by [key]
     */
    operator fun <T> get(key: String): T?

    /**
     * Remove object from cache by [key]
     */
    fun remove(key: String)

    /**
     * Check whether specific [key] exists in cache
     */
    operator fun contains(key: String): Boolean

    /**
     * Remove all key-value pairs from cache
     */
    fun clear()

}