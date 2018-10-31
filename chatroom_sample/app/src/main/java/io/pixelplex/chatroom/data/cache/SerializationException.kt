package io.pixelplex.chatroom.data.cache

/**
 * Base exception of serialization process
 */
class SerializationException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}