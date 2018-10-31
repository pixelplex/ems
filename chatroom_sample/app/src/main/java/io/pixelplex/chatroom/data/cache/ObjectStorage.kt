package io.pixelplex.chatroom.data.cache

import java.io.File
import java.io.IOException

/**
 * Extension of [FileStorage]for persisting required objects by generic type
 */
class ObjectStorage(
    root: File,
    private val serializer: Serializer = StandardJavaSerializer()
) :
    FileStorage(root) {

    /**
     * Puts object [source] in storage by [key]
     */
    fun putObject(key: String, source: Any) =
        try {
            put(key, source.serialize(serializer))
        } catch (e: IOException) {
            throw SerializationException("Unable to put object {${source.javaClass.name}}", e)
        }

    /**
     * Get required object of type [T] by [key] from storage
     */
    fun <T> getObject(key: String): T? =
        try {
            val content = get(key)
            content?.let { content.deserialize<T>(serializer) }
        } catch (e: IOException) {
            throw SerializationException("Unable to read object by key {$key}", e)
        }

}