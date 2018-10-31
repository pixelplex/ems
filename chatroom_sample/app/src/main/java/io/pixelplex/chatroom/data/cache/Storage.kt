package io.pixelplex.chatroom.data.cache

/**
 * Describes main functionality of key/value storage
 */
interface Storage {

    /**
     * Save object [source], presented as byte array, in storage by specific key [key]
     */
    fun put(key: String, source: ByteArray)

    /**
     * Read object from storage by key [key]
     */
    operator fun get(key: String): ByteArray?

    /**
     * Check, whether storage contains object with specified key [key]
     */
    operator fun contains(key: String): Boolean

    /**
     * Remove all objects from storage
     */
    fun clear()

    /**
     * Remove object from storage by key [key]
     */
    fun remove(key: String)

}
