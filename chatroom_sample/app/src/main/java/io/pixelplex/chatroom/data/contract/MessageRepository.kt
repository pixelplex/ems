package io.pixelplex.chatroom.data.contract

import io.pixelplex.chatroom.model.Message
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Encapsulates logic associated with chat messages controlling
 */
interface MessageRepository {

    /**
     * Begins [contractId] room messages observing.
     *
     * Uses private key of [userNameOrId] with [password] and
     * public key of account [companionNameOrId] to decrypt incoming messages
     */
    suspend fun startMessagesObserving(
        userNameOrId: String,
        password: String,
        companionNameOrId: String,
        contractId: String
    ): ReceiveChannel<List<Message>>

    suspend fun stopMessagesObserving()

    /**
     * Checks whether [contractId] room has messages by [blockNum]
     */
    suspend fun haveCompanionMessages(
        userNameOrId: String,
        blockNum: Long,
        contractId: String
    ): Boolean

    /**
     * Receives another participant messages by [blockNum]
     *
     * Uses private key of [userNameOrId] with [password] and
     * public key of account [companionNameOrId] to decrypt incoming messages
     */
    suspend fun getCompanionMessages(
        userNameOrId: String,
        password: String,
        companionNameOrId: String,
        blockNum: Long,
        contractId: String
    ): List<String>

    /**
     * Uploads [message] sent by [fromNameOrId] to [toNameOrId] in [contractId] room
     */
    suspend fun uploadMessage(
        fromNameOrId: String, password: String,
        toNameOrId: String,
        message: String, contractId: String
    )

}