package io.pixelplex.chatroom.data.cache

import java.io.*

/**
 * Standard implementation of [Serializer] based on [Serializable] objects
 */
class StandardJavaSerializer : Serializer {

    override fun serialize(outputStream: OutputStream, source: Any) {
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(source)
        objectOutputStream.flush()
    }

    override fun <T> deserialize(inputStream: InputStream): T {
        val objectInputStream = ObjectInputStream(inputStream)
        try {
            return objectInputStream.readObject() as T
        } catch (e: IOException) {
            throw SerializationException("Cannot deserialize object", e)
        }
    }

}
