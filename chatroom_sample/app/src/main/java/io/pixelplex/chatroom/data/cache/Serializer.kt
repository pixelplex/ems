package io.pixelplex.chatroom.data.cache

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Describes binary serializer functionality
 */
interface Serializer {

    /**
     * Converts [source] into bytes and writes it into [outputStream]
     */
    @Throws(IOException::class)
    fun serialize(outputStream: OutputStream, source: Any)

    /**
     * Reads stream of bytes and converts it into object with type [T]
     */
    @Throws(IOException::class)
    fun <T> deserialize(inputStream: InputStream): T

}
