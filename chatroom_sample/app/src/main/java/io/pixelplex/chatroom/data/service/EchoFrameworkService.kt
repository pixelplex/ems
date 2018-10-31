package io.pixelplex.chatroom.data.service

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import org.echo.mobile.framework.EchoFramework
import org.echo.mobile.framework.model.Account

/**
 * Describes functionality of service for working with [EchoFramework]
 */
interface EchoFrameworkService {

    /**
     * Restarts framework
     */
    suspend fun restart()

    /**
     * Executes required [block] on [EchoFramework]
     */
    fun execute(block: EchoFramework.() -> Unit)

    /**
     * Returns required account with name [name]
     */
    suspend fun getAccount(name: String): Account

    /**
     * Retrieves and parses contract address using specified [historyId]
     */
    suspend fun getContractAddressFromResult(historyId: String): String

    /**
     * Observes status of framework and returns subscription chanel
     */
    fun observeState(): ReceiveChannel<Boolean>
}