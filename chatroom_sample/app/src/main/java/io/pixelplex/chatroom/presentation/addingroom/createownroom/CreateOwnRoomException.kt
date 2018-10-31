package io.pixelplex.chatroom.presentation.addingroom.createownroom

import io.pixelplex.chatroom.data.UiException

/**
 * Base exception for [CreateOwnRoomActivity] screen feature
 */
class CreateOwnRoomException : UiException {

    val type: Type

    constructor(type: Type) : super() {
        this.type = type
    }

    constructor(type: Type, message: String?) : super(message) {
        this.type = type
    }

    constructor(type: Type, message: String?, cause: Throwable?) : super(message, cause) {
        this.type = type
    }

    /**
     * UI error types of creating room feature
     */
    enum class Type {
        CREATING_ERROR,
        ACCOUNT_NOT_FOUND
    }

}