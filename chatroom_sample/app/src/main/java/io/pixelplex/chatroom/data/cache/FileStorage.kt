package io.pixelplex.chatroom.data.cache

import java.io.File
import java.io.IOException

/**
 * Implementation of [Storage] based on files
 */
open class FileStorage(private val root: File) : Storage {

    override fun put(key: String, source: ByteArray) =
        try {
            checkRoot()
            val storage = getFile(key)
            storage.writeBytes(source)
        } catch (e: IOException) {
            throw SerializationException("Can not write to file with key {$key}", e)
        }

    override fun get(key: String): ByteArray? =
        try {
            val dataFile = getFile(key)
            when {
                dataFile.exists() -> dataFile.readBytes()
                else -> null
            }
        } catch (e: IOException) {
            throw SerializationException("Can not read object with key {$key}", e)
        }

    override fun contains(key: String): Boolean {
        for (file in root.listFiles()) {
            if (file.name == key) {
                return true
            }
        }

        return false
    }

    override fun clear() = root.clear()

    override fun remove(key: String) {
        for (file in root.listFiles()) {
            if (file.name == key) {
                file.deleteWithCheck()
            }
        }
    }

    private fun checkRoot() {
        if (!root.exists()) {
            val created = root.mkdir()
            if (!created) {
                throw SerializationException("Unable to create folder {$root.name}")
            }
        }
    }

    private fun getFile(name: String) = File(root, name)

}
