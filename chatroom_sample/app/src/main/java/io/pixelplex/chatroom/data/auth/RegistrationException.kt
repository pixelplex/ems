package io.pixelplex.chatroom.data.auth

import io.pixelplex.chatroom.data.UiException

/**
 * Exception of user registration process
 */
class RegistrationException : UiException {

    val type: ExceptionType

    constructor(type: ExceptionType) : super() {
        this.type = type
    }

    constructor(type: ExceptionType, message: String?) : super(message) {
        this.type = type
    }

    constructor(type: ExceptionType, message: String?, cause: Throwable?) : super(message, cause) {
        this.type = type
    }

    /**
     * Describes type of [RegistrationException]
     */
    enum class ExceptionType {
        INCORRECT_ACCOUNT_NAME,
        INCORRECT_PASSWORD,
        PASSWORDS_DO_NOT_MATCH
    }

}
