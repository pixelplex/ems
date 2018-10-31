package io.pixelplex.chatroom.presentation.addingroom.jointoroom

import io.pixelplex.chatroom.data.UiException

/**
 * Base exception for [JoinToRoomActivity] screen feature
 */
class JoinToRoomException : UiException {

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
     * UI error types of joining existing room feature
     */
    enum class Type {
        JOIN_ERROR,
        WRONG_CONTRACT_ID,
        ACCOUNT_NOT_FOUND
    }

}