package io.pixelplex.chatroom.data.contract.internal

import io.pixelplex.chatroom.data.UiException
import io.pixelplex.chatroom.data.contract.MessageRepository
import io.pixelplex.chatroom.data.service.ContractService
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.model.Message
import io.pixelplex.chatroom.support.toMapCallback
import io.pixelplex.chatroom.support.toMapErrorCallback
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlinx.coroutines.experimental.withContext
import org.echo.mobile.framework.model.Account
import org.echo.mobile.framework.model.DynamicGlobalProperties
import org.echo.mobile.framework.model.contract.input.InputValue
import org.echo.mobile.framework.model.contract.input.NumberInputValueType
import org.echo.mobile.framework.model.contract.input.StringInputValueType
import org.echo.mobile.framework.model.contract.output.ContractOutputDecoder
import org.echo.mobile.framework.model.contract.output.StringOutputValueType
import org.echo.mobile.framework.service.UpdateListener
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.coroutineContext
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Implementation of [MessageRepositoryImpl]
 */
class MessageRepositoryImpl(
    private val contractService: ContractService,
    private val echoFrameworkService: EchoFrameworkService
) : MessageRepository {

    private val messagesChanel = ConflatedBroadcastChannel<List<Message>>()

    override suspend fun startMessagesObserving(
        userNameOrId: String,
        password: String,
        companionNameOrId: String,
        contractId: String
    ): ReceiveChannel<List<Message>> {
        val blockSubscriptionResult =
            subscribeOnBlock(userNameOrId, password, companionNameOrId, contractId)

        if (!blockSubscriptionResult) {
            throw UiException("Unable to subscribe block changes")
        }

        return messagesChanel.openSubscription()
    }

    private suspend fun subscribeOnBlock(
        userNameOrId: String,
        password: String,
        companionNameOrId: String,
        contractId: String
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            echoFrameworkService.execute {
                subscribeOnBlockchainData(object : UpdateListener<DynamicGlobalProperties> {
                    override fun onUpdate(data: DynamicGlobalProperties) {
                        onBlockUpdate(data, userNameOrId, contractId, password, companionNameOrId)
                    }

                }, continuation.toMapErrorCallback { continuation, ex ->
                    ex.printStackTrace()
                    continuation.resume(false)
                })
            }
        }
    }

    private fun onBlockUpdate(
        data: DynamicGlobalProperties,
        userNameOrId: String,
        contractId: String,
        password: String,
        companionNameOrId: String
    ) {
        launch {
            val lastBlockNum = data.headBlockNumber - 1

            val requiredMessagesFetching = withContext(coroutineContext) {
                haveCompanionMessages(userNameOrId, lastBlockNum, contractId)
            }

            if (requiredMessagesFetching) {
                updateMessages(userNameOrId, contractId, password, companionNameOrId, lastBlockNum)
            }
        }
    }

    private suspend fun updateMessages(
        userNameOrId: String,
        contractId: String,
        password: String,
        companionNameOrId: String,
        lastBlockNum: Long
    ) {
        val messages = getCompanionMessages(
            userNameOrId, password, companionNameOrId, lastBlockNum, contractId
        ).map { Message(it, true) }


        if (messages.isNotEmpty()) {
            val oldMessages =
                if (messagesChanel.valueOrNull != null) {
                    ArrayList(messagesChanel.value)
                } else {
                    mutableListOf<Message>()
                }
            oldMessages.addAll(messages)

            messagesChanel.offer(oldMessages)
        }

    }

    override suspend fun stopMessagesObserving() {
        messagesChanel.offer(emptyList())

        val blockUnsubscriptionResult = unsubscribeFromBlock()

        if (!blockUnsubscriptionResult) {
            throw UiException("Unable to subscribe block changes")
        }
    }

    private suspend fun unsubscribeFromBlock(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            echoFrameworkService.execute {
                unsubscribeFromBlockchainData(
                    continuation.toMapErrorCallback { continuation, ex ->
                        ex.printStackTrace()
                        continuation.resume(false)
                    })
            }
        }
    }

    override suspend fun haveCompanionMessages(
        userNameOrId: String,
        blockNum: Long,
        contractId: String
    ): Boolean {
        return suspendCoroutine { continuation ->
            contractService.queryContract(
                userNameOrId,
                contractId,
                METHOD_HAVE_COMPANION_MESSAGES,
                listOf(InputValue(NumberInputValueType("uint256"), blockNum.toString())),
                continuation.toMapCallback { cont, value ->
                    cont.resume(contractService.parseBooleanMessage(value))
                }
            )
        }
    }

    override suspend fun getCompanionMessages(
        userNameOrId: String,
        password: String,
        companionNameOrId: String,
        blockNum: Long,
        contractId: String
    ): List<String> {
        return suspendCoroutine { continuation ->
            contractService.queryContract(
                userNameOrId,
                contractId,
                METHOD_GET_COMPANION_MESSAGES,
                listOf(InputValue(NumberInputValueType("uint256"), blockNum.toString())),
                continuation.toMapCallback { cont: Continuation<List<String>>, result: String ->
                    launch {
                        cont.resume(
                            parseIncomingMessages(result, userNameOrId, password, companionNameOrId)
                        )
                    }
                }
            )
        }
    }

    override suspend fun uploadMessage(
        fromNameOrId: String, password: String,
        toNameOrId: String,
        message: String, contractId: String
    ) {
        val oldMessages =
            if (messagesChanel.valueOrNull != null) {
                ArrayList(messagesChanel.value)
            } else {
                mutableListOf<Message>()
            }

        oldMessages.add(Message(message, false))

        messagesChanel.offer(oldMessages)

        val fromAccount = echoFrameworkService.getAccount(fromNameOrId)
        val toAccount = echoFrameworkService.getAccount(toNameOrId)

        uploadMessage(fromAccount, password, toAccount, message, contractId)
    }

    private suspend fun uploadMessage(
        fromAccount: Account, password: String,
        toAccount: Account,
        message: String, contractId: String
    ) {
        return suspendCoroutine { continuation ->
            contractService.callContract(
                fromAccount.getObjectId(), password,
                contractId,
                METHOD_UPLOAD_MESSAGES,
                getUploadMessageParams(fromAccount, password, toAccount, message),
                callback = continuation.toMapCallback { cont, _ ->
                    cont.resume(Unit)
                }
            )

        }
    }

    private fun getUploadMessageParams(
        fromAccount: Account, password: String, toAccount: Account, message: String
    ): List<InputValue> = listOf(
        InputValue(
            StringInputValueType(),
            contractService.encodeWithSignedHash(message, fromAccount, password, toAccount)
        )
    )

    private suspend fun parseIncomingMessages(
        input: String,
        userNameOrId: String,
        password: String,
        companionNameOrId: String
    ): List<String> {

        val decoder = ContractOutputDecoder()
        val output = decoder.decode(input.toByteArray(), listOf(StringOutputValueType()))

        return output.firstOrNull()?.let { messagesString ->

            val decodedList = mutableListOf<String>()

            val message = messagesString.value.toString()
            message.split(MESSAGE_DELIMITER)
                .asSequence()
                .filterNot { it.isEmpty() }
                .mapNotNullTo(decodedList) { encoded ->
                    val decoded = contractService.decodeWithSignedHash(
                        encoded,
                        echoFrameworkService.getAccount(userNameOrId),
                        password,
                        echoFrameworkService.getAccount(companionNameOrId)
                    )

                    if (decoded.isEmpty()) null else decoded
                }

            decodedList
        } ?: emptyList()
    }

    companion object {
        private const val METHOD_HAVE_COMPANION_MESSAGES = "haveCompanionMessages"
        private const val METHOD_GET_COMPANION_MESSAGES = "getCompanionMessages"
        private const val METHOD_UPLOAD_MESSAGES = "uploadMessage"

        private const val MESSAGE_DELIMITER = "|"
    }
}