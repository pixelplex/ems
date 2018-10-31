package io.pixelplex.chatroom.data.cache

/**
 * Implementation of [Cache] based on LRU stored map
 */
class LruCache : Cache {

    private val storage: MutableMap<String, Any> = hashMapOf()

    override fun <T> put(key: String, value: T) {
        storage[key] = value as Any
    }

    override fun <T> get(key: String): T = storage[key] as T

    override fun contains(key: String): Boolean = storage.containsKey(key)

    override fun clear() = storage.clear()

    override fun remove(key: String) {
        storage.remove(key)
    }


}