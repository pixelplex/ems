package io.pixelplex.chatroom.presentation.registration

/**
 * Contains all possible field types that can lead to error
 */
enum class ErrorField(val value: String) {

    ERROR("error"),
    EMAIL("email"),
    PASSWORD("password"),
    PASSWORDS("passwords"),
    FILE("file"),
    TITLE("title"),
    DESCRIPTION("description"),
    PHONE("phone"),
    UNKNOWN("");

    companion object {

        /**
         * Finds [ErrorField] with required [findValue]
         */
        fun from(findValue: String) =
            try {
                ErrorField.values().first { it.value == findValue }
            } catch (ex: NoSuchElementException) {
                ErrorField.UNKNOWN
            }

    }
}