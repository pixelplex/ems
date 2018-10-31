package io.pixelplex.chatroom.data.provider

import android.content.Context

/**
 * Provides contract bytecode
 */
class ContractCodeProvider(private val context: Context) : Provider<String> {

    override fun provide(): String =
        context.assets.open(FILE_NAME_BYTECODE).bufferedReader().use {
            it.readText()
        }

    companion object {
        private const val FILE_NAME_BYTECODE = "bytecode_contract"
    }

}
