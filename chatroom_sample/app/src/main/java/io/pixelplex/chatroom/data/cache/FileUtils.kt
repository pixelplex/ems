package io.pixelplex.chatroom.data.cache

import java.io.File

/**
 * Extension function for clearing the directory
 */
fun File.clear() {
    if (!isDirectory) {
        throw IllegalStateException("Only directory should be passed as parameter for clear function!")
    }

    for (file in listFiles { file -> file.isFile }) {
        file.deleteWithCheck()
    }
}

/**
 * Extension function for deleting file with checking, whether file deleted successfully.
 * Otherwise throw a [IllegalArgumentException]
 */
fun File.deleteWithCheck() {
    if (exists()) {
        val deleted = delete()

        if (!deleted) {
            throw IllegalArgumentException("Can not delete file {$name}")
        }
    }
}



