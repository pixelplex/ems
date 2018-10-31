package io.pixelplex.chatroom.data

/**
 * Base exception of application
 */
open class UiException : RuntimeException {

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

}