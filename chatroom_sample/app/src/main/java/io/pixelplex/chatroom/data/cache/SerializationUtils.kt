package io.pixelplex.chatroom.data.cache

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Serialize required object using specific serializer
 *
 * @param serializer Serializer for creating byte array
 */
@Throws(IOException::class)
fun Any.serialize(serializer: Serializer): ByteArray {
    val outputStream = ByteArrayOutputStream()
    serializer.serialize(outputStream, this)
    return outputStream.toByteArray()
}

/**
 * Deserialize required byte array using specific serializer
 *
 * @param serializer Serializer for creating object from [ByteArray]
 */
@Throws(IOException::class)
fun <T> ByteArray.deserialize(serializer: Serializer): T {
    val byteArrayInputStream = ByteArrayInputStream(this)
    return serializer.deserialize(byteArrayInputStream)
}

